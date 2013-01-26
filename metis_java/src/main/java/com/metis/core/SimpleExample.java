package com.metis.core;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.plugin.Framework;
import org.owasp.webscarab.plugin.proxy.Proxy;

import com.metis.core.configuration.ProxyConfiguration;
import com.metis.instrument.*;
import com.metis.jsmodify.JSModifyProxyPlugin;

public class SimpleExample {

	private static final String URL = "http://localhost:8080/same-game/same-game.html";

	public static void main(String[] args) {
		try {

			// Create a new instance of the firefox driver
			FirefoxProfile profile = new FirefoxProfile();

			// Instantiate proxy components
			ProxyConfiguration prox = new ProxyConfiguration();
			//WebScarabWrapper web = new WebScarabWrapper();

			// Modifier responsible for parsing Ast tree
			FunctionTrace s = new FunctionTrace();
			s.setFileNameToAttach("/addvariable.js");
			s.instrumentDOMModifications();

			// Interface for Ast traversal
			JSModifyProxyPlugin p = new JSModifyProxyPlugin(s);
			p.excludeDefaults();

			Framework framework = new Framework();

			/* set listening port before creating the object to avoid warnings */
			Preferences.setPreference("Proxy.listeners", "127.0.0.1:" + prox.getPort());

			Proxy proxy = new Proxy(framework);

			/* add the plugins to the proxy */
			proxy.addPlugin(p);

			framework.setSession("FileSystem", new File("convo_model"), "");

			
			/* start the proxy */
			proxy.run();


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
