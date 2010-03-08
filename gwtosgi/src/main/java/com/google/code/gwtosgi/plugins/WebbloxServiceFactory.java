/**
 * 
 */
package com.google.code.gwtosgi.plugins;

import javax.servlet.ServletContext;

import com.google.code.gwtosgi.plugins.impl.WebbloxServiceImpl;

/**
 * @author a108600
 *
 */
public class WebbloxServiceFactory {

	private static WebbloxService webBloxService;

	/**
	 * @param servletContext 
	 * @return the webbloxservice
	 */
	public static WebbloxService getWebbloxservice(ServletContext servletContext) {
		if (webBloxService == null) {
			webBloxService = new WebbloxServiceImpl(servletContext);
		}
		return webBloxService;
	}
	
}
