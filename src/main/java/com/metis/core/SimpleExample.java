package com.metis.core;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.plugin.Framework;
import org.owasp.webscarab.plugin.proxy.Proxy;

import com.crawljax.util.Helper;
import com.metis.jsmodify.JSExecutionTracer;
import com.metis.core.configuration.ProxyConfiguration;
import com.metis.instrument.*;
import com.metis.jsmodify.JSModifyProxyPlugin;

public class SimpleExample {

	//private static final String URL = "http://localhost:8080/same-game/same-game.html";
	private static final String URL = "http://localhost:8080/example_webapplication/index.html";
	
	private static String outputFolder = "";
	
	public static void main(String[] args) {
		try {

			outputFolder = Helper.addFolderSlashIfNeeded("metis-output");
			
			JSExecutionTracer tracer = new JSExecutionTracer("function.trace");
			tracer.setOutputFolder(outputFolder + "ftrace");
			//config.addPlugin(tracer);
			tracer.preCrawling();
			
			// Create a new instance of the firefox driver
			FirefoxProfile profile = new FirefoxProfile();

			// Instantiate proxy components
			ProxyConfiguration prox = new ProxyConfiguration();

			// Modifier responsible for parsing Ast tree
			FunctionTrace s = new FunctionTrace();

			s.setFileNameToAttach("/addvariable.js");
			s.setFileNameToAttach("/asyncLogger.js");
			s.setFileNameToAttach("/applicationView.js");
			s.setFileNameToAttach("/eventlistenersMirror.js");
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
			String mwh=driver.getWindowHandle();

			while (foundWindow(driver, mwh) == true) {
				// If window is open still, wait
				// Probably not the best solution, 'sleeping' should be avoided
				Thread.sleep(4000);
			}
			
			tracer.postCrawling();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	static boolean foundWindow(WebDriver wd, String name) {
		// Function to check if window has been closed
		try {
			wd.switchTo().window(name);		
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String getOutputFolder() {
		return Helper.addFolderSlashIfNeeded(outputFolder);
	}
}
