package com.metis.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.metis.instrument.*;
import com.metis.jsmodify.JSModifyProxyPlugin;

import com.crawljax.core.configuration.ProxyConfiguration;
import com.crawljax.core.plugin.ProxyServerPlugin;
import com.crawljax.plugins.webscarabwrapper.WebScarabWrapper;

public class SimpleExample {

	private static final String URL = "http://localhost:8080/same-game/same-game.html";

	public static void main(String[] args) {
		try {

			// Create a new instance of the firefox driver
			FirefoxProfile profile = new FirefoxProfile();
			
			// Instantiate proxy components
			ProxyConfiguration prox = new ProxyConfiguration();
			WebScarabWrapper web = new WebScarabWrapper();
			
			// Modifier responsible for parsing Ast tree
			FunctionTrace s = new FunctionTrace();
			s.setFileNameToAttach("/addvariable.js");
			s.instrumentDOMModifications();
			
			// Interface for Ast traversal
			JSModifyProxyPlugin p = new JSModifyProxyPlugin(s);
			p.excludeDefaults();
			web.addPlugin(p);
			
			// Starts the proxy server and provides the correct settings such as port number
			((ProxyServerPlugin) web).proxyServer(prox);
			
			if (prox != null) {
				profile.setPreference("network.proxy.http", prox.getHostname());
				profile.setPreference("network.proxy.http_port", prox.getPort());
				profile.setPreference("network.proxy.type", prox.getType().toInt());
				/* use proxy for everything, including localhost */
				profile.setPreference("network.proxy.no_proxies_on", "");
			}
			
			WebDriver driver = new FirefoxDriver(profile);
			
			// Use WebDriver to visit specified URL
			driver.get(URL);
			
			// Wait until user is done session/story
			driver.wait();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
