/**
 * 
 */
package com.google.code.gwtosgi.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author a108600
 *
 */
public class PluginPanel extends Widget {

	/**
	 * 
	 */
	public PluginPanel(String pluginId, String baseHtml) {
		super();
		setPlugin(pluginId, baseHtml);
	}
	
	public void setPlugin(String pluginId, String baseHtml) {
		IFrameElement element = Document.get().createIFrameElement();
		String baseUrl = GWT.getModuleBaseURL();
		String pluginUrl = baseUrl + "/modules/" + pluginId + "/" + baseHtml; 
		
		element.setSrc(pluginUrl);
		setElement(element);
	}

}
