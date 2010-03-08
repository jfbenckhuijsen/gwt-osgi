/**
 * 
 */
package com.google.code.gwtosgi.plugins.descriptor;

import static com.atlassian.plugin.util.validation.ValidationPattern.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.dom4j.Element;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.servlet.ServletModuleManager;
import com.atlassian.plugin.servlet.descriptors.ServletModuleDescriptor;
import com.atlassian.plugin.util.validation.ValidationPattern;

/**
 * @author a108600
 *
 */
public class GwtModuleDescriptor extends ServletModuleDescriptor {

	public static final String PREFIX_INIT_PARAM = "gwt_modules_prefix";
	
	private String prefix;
	
	/**
	 * 
	 */
	public GwtModuleDescriptor(final HostContainer hostContainer, final ServletModuleManager servletModuleManager, final String prefix) {
		super(hostContainer, servletModuleManager);	
		this.prefix = prefix;
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.plugin.servlet.descriptors.BaseServletModuleDescriptor#init(com.atlassian.plugin.Plugin, org.dom4j.Element)
	 */
	@Override
	public void init(Plugin plugin, Element element) throws PluginParseException {
		super.init(plugin, element);
	}

	/* (non-Javadoc)
	 * @see com.atlassian.plugin.servlet.descriptors.BaseServletModuleDescriptor#provideValidationRules(com.atlassian.plugin.util.validation.ValidationPattern)
	 */
	@Override
	protected void provideValidationRules(ValidationPattern pattern) {
		 pattern.rule(test("@key").withError("The key is required"));
	}

	/* (non-Javadoc)
	 * @see com.atlassian.plugin.servlet.descriptors.BaseServletModuleDescriptor#getPaths()
	 */
	@Override
	public List<String> getPaths() {
		String pluginKey = this.getPlugin().getKey();
		
		List<String> result = new ArrayList<String>();
		
		String path = "/" + pluginKey + "/*";
		result.add(path);
		
		return result;
	}

	/* (non-Javadoc)
	 * @see com.atlassian.plugin.servlet.descriptors.BaseServletModuleDescriptor#getInitParams()
	 */
	@Override
	public Map<String, String> getInitParams() {
		Map<String, String> params = super.getInitParams();
		Map<String, String> result = new HashMap<String, String>(params);
		result.put(PREFIX_INIT_PARAM, prefix);
		return result;
	}

	@Override
	public HttpServlet getModule() {
		ClassLoader pluginCl = this.getPlugin().getClassLoader();
		String pluginKey = this.getPlugin().getKey();
		return new GwtServlet(pluginCl, pluginKey);
	}

}
