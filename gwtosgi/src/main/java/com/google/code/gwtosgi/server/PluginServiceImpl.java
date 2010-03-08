/**
 * 
 */
package com.google.code.gwtosgi.server;

import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.zschech.gwt.comet.server.CometServlet;
import net.zschech.gwt.comet.server.CometSession;

import com.google.code.gwtosgi.client.PluginService;
import com.google.code.gwtosgi.plugins.WebbloxService;
import com.google.code.gwtosgi.plugins.WebbloxServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author a108600
 *
 */
public class PluginServiceImpl extends RemoteServiceServlet implements PluginService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(PluginServiceImpl.class);
	
	private WebbloxService webBloxService;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		webBloxService = WebbloxServiceFactory.getWebbloxservice(config.getServletContext());
	}

	/* (non-Javadoc)
	 * @see com.google.code.gwtosgi.client.PluginService#registerClient()
	 */
	public void registerClient() {
		LOG.info("Registering client");
		
		// Get or create the HTTP session for the browser
        HttpSession httpSession = getThreadLocalRequest().getSession();
        // Get or create the Comet session for the browser
        CometSession cometSession = CometServlet.getCometSession(httpSession);

        this.webBloxService.registerListener(new BloxListener(cometSession, webBloxService));

		LOG.info("Client registered");
	}

	/* (non-Javadoc)
	 * @see com.google.code.gwtosgi.client.PluginService#unregisterClient()
	 */
	public void unregisterClient() {
		LOG.info("Unregistering client");
		
		// check if there is a HTTP session setup.
        HttpSession httpSession = getThreadLocalRequest().getSession(false);
        if (httpSession != null) {
            CometSession cometSession = CometServlet.getCometSession(httpSession, false);
            if (cometSession != null) {
                this.webBloxService.unregisterListener(new BloxListener(cometSession, webBloxService));
            }
        }

		LOG.info("Client unregistered");
	}

	/* (non-Javadoc)
	 * @see com.google.code.gwtosgi.client.PluginService#listPlugins()
	 */
	public Map<String, String> listPlugins() {
		return this.webBloxService.listPlugins();
	}

}
