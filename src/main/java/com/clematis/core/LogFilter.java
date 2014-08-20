package com.clematis.core;
 
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.net.HttpURLConnection;














//import javax.net.ssl.HttpsURLConnection;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.clematis.core.episode.episodeResource;
import com.clematis.database.MongoInterface;
import com.clematis.jsmodify.NewProxyPlugin;
 
public class LogFilter implements Filter {
	
	private final String USER_AGENT = "Mozilla/5.0";
 
    @SuppressWarnings("unchecked")
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
 
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        //Get the IP address of client machine.
        //String ipAddress = request.getRemoteAddr();
        StringBuffer url = request.getRequestURL();
        
        boolean isClemFile = isClematisFile(request.getRequestURI());
         
        //Log the IP address and current timestamp.
        //System.out.println("Request URI: " + request.getRequestURI() + ", IP "+ipAddress + ", Time " + new Date().toString()); 
        //System.out.println("Request server name : " + request.getServerName() + " Port: " + request.getRemotePort());
        
        
        System.out.println("Request URL: " + url + "   Request Port: " + request.getServerPort() );     
        System.out.println("QUERY STRING:  "+ request.getQueryString());
        
        Subject currentUser = SecurityUtils.getSubject();
		String user = (String) currentUser.getPrincipal();
		
        if(user != null && !user.isEmpty()){
        	NewProxyPlugin proxy = new NewProxyPlugin();
        	proxy.excludeDefaults();
        	proxy.createResponse(response, request);
        }
    	

        if (url.toString().contains("localhost")){
        	
            if (url.toString().contains("/rest/") && !url.toString().contains("/rest/clematis-api")){
            	String realURL = upRequest(request, url, 2);
            	redirect(realURL, url, response);
            	
            }
        	else if (request.getQueryString() != null && request.getQueryString().contains("beginrecord")){
        	
	        	//ServletInputStream in = request.getInputStream();
	        	//String data = episodeResource.processInput(in);
	        	//System.out.println("data: " + data);
        		//Subject currentUser = SecurityUtils.getSubject();
        		//String user = (String) currentUser.getPrincipal();
        		
        		if (!currentUser.isAuthenticated()) {
	        		System.out.println( "current user not authenticated - guest user");
	    	        
	    	    	//find last guest # 
	    	    	Double guestNum = MongoInterface.getLastGuestUser() + 1.0;
	    	    	
	    	    	//create new account
	    	    	MongoInterface.newGuestUser(guestNum);
	    	    	
	    	    	//login
	    	    	//user = "guest"+guestNum;
	    	    	user = "Guest" + guestNum;
	    	        UsernamePasswordToken token = new UsernamePasswordToken(user, guestNum.toString());
	    	        //this is all you have to do to support 'remember me' (no config - built in!):
	    	        token.setRememberMe(true);
	    	        currentUser.login(token);
        		}
    	        
        		/*NewProxyPlugin proxy = new NewProxyPlugin();
	        	proxy.excludeDefaults();
	        	proxy.createResponse(response, request);*/
	        	      	
	        	try {      		
	        		episodeResource.startNewSessionPOST(request);	        		
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	      	
	        }
	        else if(request.getQueryString() != null && ( request.getQueryString().contains("thisisafunctiontracingcall") || 
	        		 request.getQueryString().contains("stoprecord") || request.getQueryString().contains("toolbarstate"))){
	        	
	        	/*NewProxyPlugin proxy = new NewProxyPlugin();
	        	proxy.excludeDefaults();
	        	proxy.createResponse(response, request);*/
	        	
	        }   
	        else if (request.getQueryString() != null && (request.getQueryString().contains("url") )){

		        	chain.doFilter(req, res);
		        	
	        }else if (request.getRequestURI() != null && !isClemFile && !matchesRestAPI(request.getRequestURI())){
            	//get url from database
        		//String relativeURL = "http://www.themaninblue.com/experiment/BunnyHunt/";
	        	
	        	//String relativeURL = getLastURL();
	        	
	        	//refactor here:
	        	//use referrer and don't store url in db
	        	String referrer = request.getHeader("referer");
	        	System.out.println("Referrer: " + referrer);
	        	String relativeURL;
	        	
	        	if (!url.toString().contains("/webservice") && url.toString().contains("/rest/clematis-api")){
	        		
	        		relativeURL = upRequest(request, url, 1);
	        		
	        	}
	        	else {
	        		relativeURL = upRequest(request, url, 0);
	        	}
	        	       		
        		//String URIString = request.getRequestURI();
        		/*if (URIString.contains("/webservice/")){
        			URIString = URIString.replace("/webservice/","");
        		}*/
        		
        		
        		//System.out.println("NEW QUERY STRING :" + URIString);
        		
        		//redirect(relativeURL+URIString, url, response);
       		
        		redirect(relativeURL, url, response);
        		
        	}else if (request.getRequestURI() != null && isClemFile && !matchesRestAPI(request.getRequestURI())){
        		System.out.println("HERE");
        		String URI = request.getRequestURI();
        		if ( URI.contains("/rest/clematis-api/")){
        			URI = URI.replace("/rest/clematis-api/","");
        			response.sendRedirect("http://localhost:8080/webservice/" + URI);
        		}
        		        		
        		chain.doFilter(req, res);
        	}
	        else{

	        	chain.doFilter(req, res);
	        }
        }
        else if (!url.toString().contains("localhost")){
        	System.out.println("not localhost");
        	
        	if (url.toString().contains("www.google-analytics.com")){
        		redirectGA(url.toString(), url, response, request);
        	}
        	
        	else if (request.getQueryString() != null){
        		redirect(url+"&"+request.getQueryString(), url, response);
        	}
        	else{
        		redirect(url.toString(), url, response);
        	}
        	
        }
        else{
        	System.out.println(" ");
        	chain.doFilter(req, res);
        }
    }
    
    
   public synchronized void changeLastURL(HttpServletRequest request){
	   Subject currentUser = SecurityUtils.getSubject();
	   String user = (String) currentUser.getPrincipal();
	   System.out.println("user: "+ user);
	   
	   String url = episodeResource.getUrl(request);
	   System.out.println("url: "+ url);
	   
	   MongoInterface.changelastURL(user, url);	   
   }
   
   public String getLastURL(){
	   Subject currentUser = SecurityUtils.getSubject();
	   String user = (String) currentUser.getPrincipal();
	   System.out.println("user: "+ user);
	   
	   String url = MongoInterface.getLastURL(user);
	   return url;
   }
    
    public boolean isClematisFile(String requestFile){
    	//getFiles(System.getProperty("user.dir")+"/src/main/webapp/")
    	boolean isFile = false;
		if (requestFile.contains("/rest/clematis-api/")){
			requestFile = requestFile.replace("/rest/clematis-api/","");
		}
    	isFile = getFiles(System.getProperty("user.dir")+"/src/main/webapp", requestFile, isFile);
    	System.out.println("isFile? " + isFile);
    	return isFile;
    }
    
    
    public boolean getFiles(String sourceFile, String requestFile, boolean isFile ){
    	File folder = new File(sourceFile);
    	//System.getProperty("user.dir")+"/src/main/webapp/" + requestFile
    	File[] listOfFiles = folder.listFiles();

    	    for (int i = 0; i < listOfFiles.length; i++) {
    	      if (listOfFiles[i].isFile()) {
    	       if((sourceFile + "/" + listOfFiles[i].getName()).contains(requestFile)){
    	    	 System.out.println("MATCH File: " + sourceFile + "/" + listOfFiles[i].getName());
    	    	 isFile = true;
    	    	 return isFile;
    	       }
    	       else {
    	    	   //System.out.println("NO MATCH File: " + sourceFile + "/" + listOfFiles[i].getName());
    	       }
    	        
    	      } else if (listOfFiles[i].isDirectory()) {
    	        //System.out.println("Directory " + listOfFiles[i].getName());
    	        isFile = getFiles(sourceFile + "/" + listOfFiles[i].getName(), requestFile, isFile);
    	      }
    	    }
    	
    	return isFile;
    }
    
    public boolean matchesRestAPI(String uri){
    	List<String> paths = new ArrayList<String>();
    	paths.add("/episodes");
    	paths.add("/story");
    	paths.add("/sessions");
    	paths.add("/account");
    	paths.add("/redirect");
    	paths.add("/test");
    	paths.add("/areWeRecording");
    	
    	for (String s : paths ){
    		if (uri.contains(s)){
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public String upRequest(HttpServletRequest request, StringBuffer url, int upNum){
    	System.out.println("");
    	System.out.println("UP REQUEST  !!!!!!!!!!!!!!!");
    	
    	
    	String referrer = request.getHeader("referer");
    	System.out.println("Referrer: " + referrer);
    	
    	String referLocat, realURL = "";
    	if (referrer!= null && !referrer.isEmpty()){
    		if (referrer.contains("url=")){
        		String[] referLocation = referrer.split("\\?url=");
        		referLocat = referLocation[1];
            	System.out.println("Referrer Location: " + referLocat);
        	}else{
        		referLocat = referrer;
        	}
    		
    		String[] urlArray = referLocat.split("/");
        	
        	
        	int startingPoint;
        	if (referLocat.contains("http://")){
        		startingPoint = 1;
        		realURL = "http:/";
        	}
        	else{
        		startingPoint = 0;
        		realURL = "http://";
        	}
        	
        	for( int i = startingPoint; i < urlArray.length - upNum; i++){
        		realURL = realURL + urlArray[i] + "/";
        	}
    	}
    	
    	
    	
    	String[] queryArray;
    	String queryString;
    	
    	if (url.toString().contains("/rest/clematis-api/")){
			//URIString = URIString.replace("/rest/clematis-api/","");
			queryArray = url.toString().split("/rest/clematis-api/");
			queryString = queryArray[1];
		}else if (url.toString().contains("/rest/")){
			//URIString = URIString.replace("/rest/","");
			queryArray = url.toString().split("/rest/");
			queryString = queryArray[1];
		}else if (url.toString().contains("/webservice/")){
			queryArray = url.toString().split("/webservice/");
			queryString = queryArray[1];
		}else{
			queryString = request.getRequestURI();
		}
    	
    	//String[] queryArray = url.toString().split("/rest/");
    	//if referrer is null, use original query string
    	
    	realURL = realURL + queryString;
    	
    	System.out.println("realURL: " + realURL);
    	
    	return realURL;
    }
    
    public void redirect(String redirURL, StringBuffer url, HttpServletResponse response) throws IOException{
    	if(url.toString().contains("png") || url.toString().contains("gif") || url.toString().contains("jpg")){
    		System.out.println("PNG");
    		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirectPNG?url="+redirURL);
    	}
    	else if(url.toString().contains("css")){
    		System.out.println("CSS");
    		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirectCSS?url="+redirURL);
    	}
    	else if(url.toString().contains("js")){
    		System.out.println("JS");
    		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirectJS?url="+redirURL);
    	}
    	else if (url.toString().contains("html")){
    		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirectHTML?url="+redirURL);
    	}
    	else{
    		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirect?url="+redirURL);
    	}
    }
    
    //redirects for www.google-analytics.com/_utm.gif
    public void redirectGA(String gaURL, StringBuffer url, HttpServletResponse response, HttpServletRequest request) throws IOException{
    	
		String queryString = "";
		String urlString = "";
		
		String referrer = request.getHeader("referer");
    	System.out.println("Referrer: " + referrer);
    	
    	String[] referLocation = referrer.split("\\?url=");
    	System.out.println("Referrer Location: " + referLocation[1]);
    
    	String[] utmhn = referLocation[1].split("/");
    	String location = "";
    	if (utmhn.length <= 3){
    		location = "home";
    	}
    	else {
    		for (int i = 3 ; i< utmhn.length ; i++){
    			location = location + "/" + utmhn[i];
    		}
    	}
    	
    	
		System.out.println("relative location: " + location);
    	
		@SuppressWarnings("unchecked")
		Enumeration<String> params = request.getParameterNames();
    	while(params.hasMoreElements()){
    		String paramName = (String) params.nextElement();
    		String[] paramVal = request.getParameterValues(paramName);
    		System.out.println("Param: " + paramName);
    		for (int i=0; i<paramVal.length; i++){
    			System.out.println(" "+i+": " + paramVal[i]);
    		}
    		System.out.println("");
    		
    		if(paramName.equals("utmhn")){
    			//change to url's domain
    			queryString = queryString + paramName + "=" + utmhn[2] + "&";
    		}else if (paramName.equals("utmp")){
    			//change to iframe's location
    			queryString = queryString + paramName + "=" + location + "&";
    		}else{
    			queryString = queryString + paramName + "=" + paramVal[0] +"&";
    		}
    	}
    	if (queryString != null && !queryString.isEmpty()){
    		queryString = queryString.substring(0, queryString.length() - 1);
    	}
    	
    	queryString = queryString.replace(" ", "%20");
    	
    	System.out.println("NEW QUERY GOOGLE ANALYTICS:" + queryString);
    	
    	redirect(gaURL+"?"+queryString, url, response);
    }
    
    
    public void init(FilterConfig config) throws ServletException {
         
        //Get init parameter
        String testParam = config.getInitParameter("test-param");
         
        //Print the init parameter
        System.out.println("Test Param: " + testParam);
    }
    public void destroy() {
        //add code to release any resource
    }
}