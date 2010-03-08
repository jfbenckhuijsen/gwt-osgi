package com.google.code.gwtosgi.client;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.zschech.gwt.comet.client.CometClient;
import net.zschech.gwt.comet.client.CometListener;

import com.google.code.gwtosgi.client.PluginServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application implements EntryPoint {
	
	private CometClient cometClient;
	private CometListener listener;
	private PluginServiceAsync pluginService;
	
	private DockLayoutPanel dockPanel;
	private FlowPanel loggingPanel;
	private FlowPanel pluginPanel;
	
	private Widget mainWidget;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		createUI();
		output("gwt-maven-plugin Archetype :: Project com.google.code.gwtosgi.webblox");

		listener = new CometListener() {
			public void onConnected(int heartbeat) {
				output("Connected:" + heartbeat);
			}

			public void onDisconnected() {
				output("Disconnected");
			}

			public void onHeartbeat() {
				output("Heartbeat");
			}

			public void onRefresh() {
				output("Refresh");
			}

			public void onError(Throwable exception, boolean connected) {
				exception.printStackTrace(System.out);
				output("Connection failed");
			}

			public void onMessage(List<? extends Serializable> messages) {
				for (Serializable message : messages) {
					output(message.toString());
				}
			}
		};
		
		pluginService = GWT.create(PluginService.class);

		pluginService.registerClient(new AsyncCallback<Void>() {

			public void onFailure(Throwable t) {
				t.printStackTrace(System.out);
				output("Registration failed");
			}

			public void onSuccess(Void arg0) {
				output("Registred client");
			}
			
		});

		cometClient = new CometClient(GWT.getModuleBaseURL() + "comet", listener);
		cometClient.start();	

	}
	
	public void output(String message) {
		System.out.println("Message received:" + message);
		loggingPanel.add(new Label(message));
		ScrollPanel s = (ScrollPanel) loggingPanel.getParent();
		s.scrollToBottom();
	}
	
	/**
	 * 
	 */
	private void createUI() {
		dockPanel = new DockLayoutPanel(Unit.EM);
		dockPanel.addNorth(new Label("Plugin example"), 2);
		
		ScrollPanel s1 = new ScrollPanel(); 
		loggingPanel = new FlowPanel();
		s1.add(loggingPanel);
		dockPanel.addSouth(s1, 6);
		
		FlowPanel nav = new FlowPanel();
		Button refresh = new Button("Refresh", new ClickHandler() {

			public void onClick(ClickEvent arg0) {
				refreshPlugins();				
			}
			
		});
		nav.add(refresh);

		ScrollPanel s2 = new ScrollPanel();
		pluginPanel = new FlowPanel();
		s2.add(pluginPanel);
		nav.add(s2);
		
		dockPanel.addWest(nav, 10);
		
		this.mainWidget = new Label("No plugin selected");
		dockPanel.add(mainWidget);
		
		RootLayoutPanel.get().add(dockPanel);
	}

	private void refreshPlugins() {
		for (Iterator<Widget> i = this.pluginPanel.iterator(); i.hasNext(); ) {
			i.next();
			i.remove();
		}
		
		this.pluginService.listPlugins(new AsyncCallback<Map<String, String>>() {

			public void onFailure(Throwable t) {
				output("Failed to retrieve plugins" + t.getMessage());
			}

			public void onSuccess(Map<String, String> plugins) {
				for (Map.Entry<String, String> plugin : plugins.entrySet()) {
					addPlugin(plugin.getKey(), plugin.getValue());
				}
			}
			
		});
	}
	
	private void addPlugin(final String pluginKey, String pluginName) {
		Button pluginButton = new Button(pluginName);
		
		pluginButton.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				showPlugin(pluginKey);
			}
			
		});
		this.pluginPanel.add(pluginButton);
	}
	
	private void showPlugin(String pluginKey) {
		output("Showing plugin " + pluginKey);
		
		this.dockPanel.remove(mainWidget);
		this.mainWidget = new PluginPanel(pluginKey, "TestService.html");
		this.dockPanel.add(mainWidget);
	}
	
}
