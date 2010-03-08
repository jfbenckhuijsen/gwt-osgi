/**
 * 
 */
package com.google.code.gwtosgi.plugins.impl;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.atlassian.plugin.DefaultModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.hostcontainer.SimpleConstructorHostContainer;
import com.atlassian.plugin.main.AtlassianPlugins;
import com.atlassian.plugin.main.PluginsConfiguration;
import com.atlassian.plugin.main.PluginsConfigurationBuilder;
import com.atlassian.plugin.osgi.container.PackageScannerConfiguration;
import com.atlassian.plugin.osgi.container.impl.DefaultPackageScannerConfiguration;
import com.atlassian.plugin.osgi.hostcomponents.ComponentRegistrar;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentProvider;
import com.atlassian.plugin.servlet.DefaultServletModuleManager;
import com.atlassian.plugin.servlet.ServletModuleManager;
import com.atlassian.plugin.servlet.descriptors.ServletFilterModuleDescriptor;
import com.atlassian.plugin.servlet.descriptors.ServletModuleDescriptor;
import com.atlassian.plugin.servlet.filter.FilterLocation;
import com.atlassian.plugin.servlet.util.DefaultPathMapper;
import com.atlassian.plugin.servlet.util.PathMapper;
import com.atlassian.plugin.servlet.util.ServletContextServletModuleManagerAccessor;
import com.google.code.gwtosgi.plugins.WebbloxService;
import com.google.code.gwtosgi.plugins.descriptor.GwtModuleDescriptor;

/**
 * @author a108600
 *
 */
public class WebbloxServiceImpl implements WebbloxService {
	
	private static final Log LOG = LogFactory.getLog(WebbloxServiceImpl.class);
	
	private final AtlassianPlugins plugins;
	private final PluginEventManager pluginManager;
	
	private class ServletModuleManagerWrapper implements ServletModuleManager {
		
		private ServletModuleManager delegate;

		public void addFilterModule(ServletFilterModuleDescriptor descriptor) {
			delegate.addFilterModule(descriptor);
		}

		public void addServletModule(ServletModuleDescriptor descriptor) {
			delegate.addServletModule(descriptor);
		}

		public Iterable<Filter> getFilters(FilterLocation location,
				String pathInfo, FilterConfig filterConfig)
				throws ServletException {
			return delegate.getFilters(location, pathInfo, filterConfig);
		}

		public HttpServlet getServlet(String path, ServletConfig servletConfig)
				throws ServletException {
			return delegate.getServlet(path, servletConfig);
		}

		public void removeFilterModule(ServletFilterModuleDescriptor descriptor) {
			delegate.removeFilterModule(descriptor);
		}

		public void removeServletModule(ServletModuleDescriptor descriptor) {
			delegate.removeServletModule(descriptor);
		}

		public void setDelegate(ServletModuleManager delegate) {
			this.delegate = delegate;
		}
		
	}
	
	private class GwtPathMapper implements PathMapper {
		private String prefix;
		private PathMapper delegate;
		
		public GwtPathMapper(String prefix) {
			this.prefix = prefix;
			this.delegate = new DefaultPathMapper();
		}

		public String get(String path) {
			return delegate.get(getSubPath(path));
		}

		public Collection<String> getAll(String path) {
			return delegate.getAll(getSubPath(path));
		}

		public void put(String key, String pattern) {
			delegate.put(key, pattern);
		}

		private String getSubPath(String path) {
			return path.substring(prefix.length());
		}
		
	}
	
	public WebbloxServiceImpl(ServletContext servletContext) {
		// TODO: Make dynamic
		String prefix = "/com.google.code.gwtosgi.Application/modules"; // /com.google.code.gwtosgi.Application/modules/testservice/greet
		
		/*
		 * Determine which packages to expose to plugins
		 */
		PackageScannerConfiguration scannerConfig = new DefaultPackageScannerConfiguration();
		scannerConfig.getPackageIncludes().add("*");
//		scannerConfig.getPackageIncludes().add("com.google.gwt*");
		
		/*
		 * Host container needed for instantiating the servlet module descriptor 
		 */
		Map<Class<?>, Object> context = new HashMap<Class<?>, Object>();
		ServletModuleManagerWrapper servletManagerWrapper = new ServletModuleManagerWrapper();
		context.put(ServletModuleManager.class, servletManagerWrapper);
		context.put(String.class, prefix);
		HostContainer hostContainer = new SimpleConstructorHostContainer(context);
		
		/*
		 * Determine which module descriptors, or extension points, to expose.
		 */
		DefaultModuleDescriptorFactory modules = new DefaultModuleDescriptorFactory(hostContainer);
		modules.addModuleDescriptor("servlet", ServletModuleDescriptor.class);
		modules.addModuleDescriptor("gwt-servlet", GwtModuleDescriptor.class);
		 
		/*
		 * Determine which service objects to expose to plugins
		 */
		HostComponentProvider host = new HostComponentProvider() {
		    public void provide(ComponentRegistrar reg)
		    {
//		        reg.register(MyServiceInterface.class).forInstance(
//		            myServiceInstance);
		    }
		};
		 
		/*
		 * Construct the configuration
		 */
		PluginsConfiguration config = new PluginsConfigurationBuilder()
		        .pluginDirectory(new File("plugins"))
		        .packageScannerConfiguration(scannerConfig)
		        .hotDeployPollingFrequency(2, TimeUnit.SECONDS)
		        .hostComponentProvider(host)
		        .moduleDescriptorFactory(modules)
		        .build();
		 
		/*
		 * Start the plugin framework
		 */
		plugins = new AtlassianPlugins(config);
		
		/*
		 * Get some managers and store the servlet module manager for creating
		 * servlet module descriptors
		 */
		this.pluginManager = plugins.getPluginEventManager();
		ServletModuleManager servletManager = new DefaultServletModuleManager(pluginManager, new GwtPathMapper(prefix), new GwtPathMapper(prefix));
		servletManagerWrapper.setDelegate(servletManager);

		ServletContextServletModuleManagerAccessor.setServletModuleManager(servletContext, servletManagerWrapper);
		
		plugins.start();
	}
	
	/* (non-Javadoc)
	 * @see com.google.code.gwtosgi.plugins.WebbloxService#registerListener(java.lang.Object)
	 */
	public void registerListener(Object listener) {
		LOG.info("Adding new listener");
		this.pluginManager.register(listener);
	}

	/* (non-Javadoc)
	 * @see com.google.code.gwtosgi.plugins.WebbloxService#unregisterListener(java.lang.Object)
	 */
	public void unregisterListener(Object listener) {
		LOG.info("Removing listener");
		this.pluginManager.unregister(listener);
	}

	/* (non-Javadoc)
	 * @see com.google.code.gwtosgi.plugins.WebbloxService#listPlugins()
	 */
	public Map<String,String> listPlugins() {
		Map<String, String> result = new HashMap<String, String>();
		PluginAccessor accessor = this.plugins.getPluginAccessor();
		for (Plugin plugin : accessor.getEnabledPlugins()) {
			result.put(plugin.getKey(), plugin.getName());
		}
		return result;
	}
}
