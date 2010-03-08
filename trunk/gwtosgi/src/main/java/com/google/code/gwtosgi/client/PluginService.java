/**
 * 
 */
package com.google.code.gwtosgi.client;

import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author a108600
 *
 */
@RemoteServiceRelativePath("plugins")
public interface PluginService extends RemoteService {

	void registerClient();
	
	void unregisterClient();
	
	Map<String, String> listPlugins();
	
}
