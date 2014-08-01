package com.clematis.core;
 
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
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
import com.clematis.jsmodify.NewProxyPlugin;
 
public class LogFilter implements Filter {
	
	private final String USER_AGENT = "Mozilla/5.0";
 
    @SuppressWarnings("unchecked")
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
 
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        //Get the IP address of client machine.
        String ipAddress = request.getRemoteAddr();
        StringBuffer url = request.getRequestURL();
         
        //Log the IP address and current timestamp.
        System.out.println("Request URI: " + request.getRequestURI() + ", IP "+ipAddress + ", Time " + new Date().toString());
        System.out.println("Request server name : " + request.getServerName() + " Port: " + request.getRemotePort());
        System.out.println("Request URL: " + url + "   Request Port: " + request.getServerPort() );
        
        System.out.println("QUERY STRING:  "+ request.getQueryString());
        
        if (request.getQueryString() != null && request.getQueryString().contains("?beginrecord")){
        	
        	
        	ServletInputStream in = request.getInputStream();
        	String data = episodeResource.processInput(in);
        	//System.out.println("data: " + data);
        	      	
        	try {
				//sendPost("http://localhost:8080/rest/clematis-api/startSessionPOST", targetUrl, data );
        		
        		episodeResource.startNewSessionPOST(request);
        		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	      	
        }
        else if(request.getQueryString() != null && ( request.getQueryString().contains("?thisisafunctiontracingcall") || 
        		 request.getQueryString().contains("?stoprecord"))){
        	//ServletInputStream in = request.getInputStream();
        	//String data = episodeResource.processInput(in);
        	//System.out.println("data: " + data);
        	
        	//need to start new session is ?beginrecord
        	//get handle on session somehow
        	
        	//get tracer for user
        	//
        	NewProxyPlugin proxy = new NewProxyPlugin();
        	proxy.createResponse(response, request);
        }   
        else if (request.getQueryString() != null && (request.getQueryString().contains("url") || request.getQueryString().contains("redir=no"))){

	        	//System.out.println("don't redirect");
	        	//System.out.println(" ");
	        	
	        	chain.doFilter(req, res);
        }
        else if (!url.toString().contains("localhost")){
        	System.out.println("not localhost");
        	if(url.toString().contains("png") || url.toString().contains("gif") || url.toString().contains("jpg")){
        		System.out.println("PNG");
        		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirectPNG?url="+url+"&"+request.getQueryString());
        	}
        	else{
        		response.sendRedirect("http://localhost:8080/rest/clematis-api/redirect?url="+url+"&"+request.getQueryString());
        	}

        	//chain.doFilter(wrappedRequest, res);
        }
        else{
        	System.out.println(" ");
        	chain.doFilter(req, res);
        }
    }
    
    
    private void sendPost(String requestUrl, String urlParam, String data) throws Exception {
    	
    	//
    	String info = "url=" + urlParam + "&data=" + data;
    	
		URL obj = new URL(requestUrl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 	 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(info);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + requestUrl);
		System.out.println("Post parameters : " + info);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		System.out.println(response.toString());
 
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