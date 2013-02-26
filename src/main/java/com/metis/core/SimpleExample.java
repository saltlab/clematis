package com.metis.core;

import java.io.File;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
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
	private static WebDriver driver;

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
			s.setFileNameToAttach("/jsonml-dom.js");
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

			driver = new FirefoxDriver(profile);
			WebDriverWait wait = new WebDriverWait(driver, 10);
			boolean sessionOver = false;

			// Use WebDriver to visit specified URL
			driver.get(URL);

			while (!sessionOver) {
				// Wait until the user/tester has closed the browser

				try {
					waitForWindowClose(wait);

					// At this point the window was closed, no TimeoutException
					sessionOver = true;
				} catch (TimeoutException e) {
					// 10 seconds has elapsed and the window is still open
					sessionOver = false;
				} catch (WebDriverException wde) {
					wde.printStackTrace();
					sessionOver = false;
				}
			}

			tracer.postCrawling();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	static boolean waitForWindowClose(WebDriverWait w) throws TimeoutException {
		// Function to check if window has been closed

		w.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver d) {
				try {
					return d.getWindowHandles().size() < 1;
				} catch (Exception e) {
					return true;
				}
			}
		});
		return true;
	}

	public static boolean isAlertPresent() 
	{ 
		// Selenium bug where all alerts must be closed before 
		// driver.execute(String) can be executed
		try { 
			driver.switchTo().alert(); 
			return true; 
		} catch (NoAlertPresentException Ex) {
			return false; 
		}   
	}

	public static String getOutputFolder() {
		return Helper.addFolderSlashIfNeeded(outputFolder);
	}
}
