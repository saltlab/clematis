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
import org.apache.shiro.subject.Subject;

import com.clematis.core.episode.episodeResource;
import com.clematis.database.MongoInterface;
import com.clematis.jsmodify.NewProxyPlugin;
 
public class LogFilter implements Filter {
	
	private final String USER_AGENT = "Mozilla/5.0";
 
    @SuppressWarnings("unchecked")
	public synchronized void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
 
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        //Get the IP address of client machine.
        String ipAddress = request.getRemoteAddr();
        StringBuffer url = request.getRequestURL();
        
        boolean isClemFile = isClematisFile(request.getRequestURI());
         
        //Log the IP address and current timestamp.
        //System.out.println("Request URI: " + request.getRequestURI() + ", IP "+ipAddress + ", Time " + new Date().toString());
        System.out.println("Request server name : " + request.getServerName() + " Port: " + request.getRemotePort());
        System.out.println("Request URL: " + url + "   Request Port: " + request.getServerPort() );     
        System.out.println("QUERY STRING:  "+ request.getQueryString());

        if (url.toString().contains("localhost")){
        	
        	if(url.toString().contains("http://localhost:8080/webservice/session.jsp") && !request.getQueryString().contains("beginrecord") && 
        			!request.getQueryString().contains("thisisafunctiontracingcall") && !request.getQueryString().contains("stoprecord") && !request.getQueryString().contains("toolbarstate")){
        		System.out.println("CHANGE URL");
        		changeLastURL(request);
        	}
        	
        	if (request.getQueryString() != null && request.getQueryString().contains("beginrecord")){
        	
	        	//ServletInputStream in = request.getInputStream();
	        	//String data = episodeResource.processInput(in);
	        	//System.out.println("data: " + data);
	        	      	
	        	try {      		
	        		episodeResource.startNewSessionPOST(request);	        		
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	      	
	        }
	        else if(request.getQueryString() != null && ( request.getQueryString().contains("thisisafunctiontracingcall") || 
	        		 request.getQueryString().contains("stoprecord"))){
	        	
	        	NewProxyPlugin proxy = new NewProxyPlugin();
	        	proxy.createResponse(response, request);
	        	
	        }   
	        else if (request.getQueryString() != null && (request.getQueryString().contains("url") || request.getQueryString().contains("redir=no"))){

		        	chain.doFilter(req, res);
		        	
	        }else if (request.getRequestURI() != null && !isClemFile && !matchesRestAPI(request.getRequestURI())){
            	//get url from database
        		//String relativeURL = "http://www.themaninblue.com/experiment/BunnyHunt/";
	        	
	        	String relativeURL = getLastURL();
        		        		
        		String URIString = request.getRequestURI();
        		if (URIString.contains("/webservice/")){
        			URIString = URIString.replace("/webservice/","");
        		}
        		if (URIString.contains("/rest/clematis-api/")){
        			URIString = URIString.replace("/rest/clematis-api/","");
        		}else if (URIString.contains("/rest/")){
        			URIString = URIString.replace("/rest/","");
        		}
        		
        		System.out.println("NEW QUERY STRING :" + URIString);
        		
        		if(url.toString().contains("png") || url.toString().contains("gif") || url.toString().contains("jpg")){
            		System.out.println("PNG");
            		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirectPNG?url="+relativeURL+URIString);
            	}
            	else if(url.toString().contains("css")){
            		System.out.println("CSS");
            		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirectCSS?url="+relativeURL+URIString);
            	}
            	else if(url.toString().contains("js")){
            		System.out.println("JS");
            		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirectJS?url="+relativeURL+URIString);
            	}
            	else{
            		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirectHTML?url="+relativeURL+URIString);
            	}
        		
 		
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
        	if(url.toString().contains("png") || url.toString().contains("gif") || url.toString().contains("jpg")){
        		System.out.println("PNG");
        		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirectPNG?url="+url+"&"+request.getQueryString());
        	}
        	else if(url.toString().contains("css")){
        		System.out.println("CSS");
        		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirectCSS?url="+url+"&"+request.getQueryString());
        	}
        	else if(url.toString().contains("js")){
        		System.out.println("JS");
        		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirectJS?url="+url+"&"+request.getQueryString());
        	}
        	else{
        		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirectHTML?url="+url+"&"+request.getQueryString());
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
    	
    	for (String s : paths ){
    		if (uri.contains(s)){
    			return true;
    		}
    	}
    	
    	return false;
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