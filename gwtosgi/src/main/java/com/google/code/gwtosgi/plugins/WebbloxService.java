/**
 * 
 */
package com.google.code.gwtosgi.plugins;

import java.util.Map;

/**
 * @author a108600
 *
 */
public interface WebbloxService {

	void registerListener(Object listener);
	
	void unregisterListener(Object listener);
	
	Map<String, String> listPlugins();
}
