package com.metis.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.metis.instrument.*;

import com.crawljax.core.configuration.ProxyConfiguration;
import com.crawljax.core.plugin.ProxyServerPlugin;
import com.crawljax.plugins.jsmodify.JSModifyProxyPlugin;
import com.crawljax.plugins.webscarabwrapper.WebScarabWrapper;

public class SimpleExample {

	private static final String URL = "http://localhost:8080/same-game/same-game.html";

	public static void main(String[] args) {
		try {

			// Create a new instance of the firefox driver
			ProxyConfiguration prox = new ProxyConfiguration();
			FirefoxProfile profile = new FirefoxProfile();
			WebScarabWrapper web = new WebScarabWrapper();
			
			SamplePlugin s = new SamplePlugin();
			s.setFileNameToAttach("/addvariable.js");
			s.instrumentDOMModifications();
			
			// Proxy plugin
			JSModifyProxyPlugin p = new JSModifyProxyPlugin(s);
			p.excludeDefaults();
			web.addPlugin(p);
			
			((ProxyServerPlugin) web).proxyServer(prox);
			
			
		//	web.addPlugin(s);
			
			if (prox != null) {
				profile.setPreference("network.proxy.http", prox.getHostname());
				profile.setPreference("network.proxy.http_port", prox.getPort());
				profile.setPreference("network.proxy.type", prox.getType().toInt());
				/* use proxy for everything, including localhost */
				profile.setPreference("network.proxy.no_proxies_on", "");
			}
			
			WebDriver driver = new FirefoxDriver(profile);
			
			// And now use this to visit Google
			driver.get(URL);

			// Find the text input element by its name
			//WebElement element = driver.findElement(By.name("q"));

			// Enter something to search for
			//element.sendKeys("Cheese!");

			// Now submit the form. WebDriver will find the form for us from the element
			//element.submit();

			// Check the title of the page
			//System.out.println("Page title is: " + driver.getTitle());

			/*	MetisConfiguration config = new MetisConfiguration();
			config.setBrowser(BrowserType.firefox);

			MetisController metis = new MetisController(config);
			metis.run();*/
			
			// Close the driver/browser
			//driver.close();
			
			driver.wait();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
