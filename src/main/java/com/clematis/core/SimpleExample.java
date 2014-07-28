package com.clematis.core;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.netty.channel.ChannelHandlerContext;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.Framework;
import org.owasp.webscarab.plugin.proxy.Proxy;

import com.clematis.core.configuration.ProxyConfiguration;
import com.clematis.instrument.FunctionTrace;
import com.clematis.jsmodify.JSExecutionTracer;
import com.clematis.jsmodify.JSModifyProxyPlugin;
import com.crawljax.util.Helper;

public class SimpleExample {

	public static final String SERVER_PREFIX2 = "--url";
	public static final String SERVER_PREFIX1 = "--u";

	private boolean urlProvided = false;
	private String URL = "";
	private String clientIP;
	
	private String userName;
	private Double sessionNum;
	
	private String outputFolder = "";
	private WebDriver driver;
	
	public SimpleExample (String IP, String userName, Double sessionNum){
		this.setIP(IP);
		this.userName = userName;
		this.sessionNum = sessionNum;
	}

	
	public void begin(String args){
		try{
			//FILESYSTEM
			outputFolder = Helper.addFolderSlashIfNeeded("clematis-output");
			//FILESYSTEM
			//JSExecutionTracer tracer = new JSExecutionTracer("function.trace");
			JSExecutionTracer tracer = new JSExecutionTracer();

			tracer.setOutputFolder(outputFolder + "ftrace");
	
			//config.addPlugin(tracer);
			tracer.preCrawling();
	
			// Create a new instance of the firefox driver
			FirefoxProfile profile = new FirefoxProfile();
	
			// Instantiate proxy components
			ProxyConfiguration prox = new ProxyConfiguration();
	
			// Modifier responsible for parsing Ast tree
			FunctionTrace s = new FunctionTrace();
			
			//FILESYSTEM
			// Add necessary files from resources
			s.setFileNameToAttach("/esprima.js");
			s.setFileNameToAttach("/esmorph.js");
			s.setFileNameToAttach("/jsonml-dom.js");
			s.setFileNameToAttach("/addvariable.js");
			s.setFileNameToAttach("/asyncLogger.js");
			s.setFileNameToAttach("/applicationView.js");
			s.setFileNameToAttach("/instrumentDOMEvents.js");
			s.setFileNameToAttach("/domMutations.js");
			s.setFileNameToAttach("/mutation_summary.js");
			s.instrumentDOMModifications();
	
			// Interface for Ast traversal
			JSModifyProxyPlugin p = new JSModifyProxyPlugin(s, tracer, this.userName, this.sessionNum);
			p.excludeDefaults(); 
	
			//Framework framework = new Framework();
			
	
			/* set listening port before creating the object to avoid warnings */
			
			//Preferences.setPreference("Proxy.listeners", "127.0.0.1:" + prox.getPort());
			//Preferences.setPreference("Proxy.listeners", clientIP + ":" + prox.getPort());

			//Proxy proxy = new Proxy(framework);
	
			/* add the plugins to the proxy */
			//proxy.addPlugin(p);
	
			//framework.setSession("FileSystem", new File("convo_model"), "");
	
			/* start the proxy */
			//proxy.run();
	
			/*if (prox != null) {
				System.out.println("hostname: "+ prox.getHostname() + "port: " + prox.getPort());
				profile.setPreference("network.proxy.http", prox.getHostname());
				profile.setPreference("network.proxy.http_port", prox.getPort());
				profile.setPreference("network.proxy.type", prox.getType().toInt());
				// use proxy for everything, including localhost 
				profile.setPreference("network.proxy.no_proxies_on", "");
			}*/
	
			//driver = new FirefoxDriver(profile);
			//WebDriverWait wait = new WebDriverWait(driver, 10);
			boolean sessionOver = false;
	
			try {
				// Use WebDriver to visit specified URL
				//driver.get(URL);
			} catch (WebDriverException e) {
				System.err.println("Error reaching application, please ensure URL is valid.");
				e.printStackTrace();
				System.exit(1);
			}
			
			while (!sessionOver) {
				// Wait until the user/tester has closed the browser
	
				try {
					//waitForWindowClose(wait);
	
					// At this point the window was closed, no TimeoutException
					//sessionOver = true;
				} catch (TimeoutException e) {
					// 10 seconds has elapsed and the window is still open
					sessionOver = false;
				} catch (WebDriverException wde) {
					wde.printStackTrace();
					sessionOver = false;
				}
			}
			
			//proxy.stop();
			urlProvided = false;
	
			tracer.postCrawling(this.userName, this.sessionNum);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public String getUserName(){
		return this.userName;
	}
	
	public String getIP(){
		return this.clientIP;
	}
	
	public void setURL (boolean provided, String newUrl){
		this.urlProvided = provided;
		this.URL = newUrl;
		System.out.println("URL Provided: " + urlProvided + " URL: " + URL);
	}
	
	public void checkURL(String url){
		String correctURL;
		if (!(url.contains(".com") || url.contains("www.") || url.contains("http://"))){
			System.err.println("Invalid arguments. Please provide URL for target application as argument (E.g. --url http://localhost:8888/phormer331/index.php)");
			throw new IllegalArgumentException();
		}
		else if (!url.contains("http://")){
			correctURL = "http://" + url;
		}
		else {
			correctURL = url;
		}
		setURL(true, correctURL);
	}
	
	public void setIP(String ip){
		this.clientIP = ip;
	}

	static boolean waitForWindowClose(WebDriverWait w) throws TimeoutException {
		// Function to check if window has been closed

		w.until(new ExpectedCondition<Boolean>() {
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

	public boolean isAlertPresent()
	{
		// Selenium bug where all alerts must be closed before
		try {
			this.driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException Ex) {
			return false;
		}
	}

	public String getOutputFolder() {
		return Helper.addFolderSlashIfNeeded(this.outputFolder);
	}

	private void parse(String arg) throws IllegalArgumentException {
		if (arg.equals(SERVER_PREFIX1) || arg.equals(SERVER_PREFIX2)) {
			this.urlProvided = true;
		}
	}

	private boolean checkOptions() {
		return this.urlProvided;
	}
	
}
