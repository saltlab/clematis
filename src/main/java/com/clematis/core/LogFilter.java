package com.clematis.core;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clematis.core.episode.episodeResource;
 
public class LogFilter implements Filter {
 
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
        
        //System.out.println("QUERY STRING:  "+request.getQueryString());
        
        if (request.getQueryString() != null && (request.getQueryString().contains("url") || request.getQueryString().contains("redir=no"))){

	        	//System.out.println("don't redirect");
	        	//System.out.println(" ");
	        	
	        	chain.doFilter(req, res);
        }
        else if (!url.toString().contains("localhost")){
        	//System.out.println("not localhost");
        	
        	response.sendRedirect("http://localhost:8080/rest/clematis-api/redirect?url="+url+"&"+request.getQueryString());

        	//chain.doFilter(wrappedRequest, res);
        }
        else{
        	System.out.println(" ");
        	chain.doFilter(req, res);
        }
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