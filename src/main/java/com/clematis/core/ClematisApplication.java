package com.clematis.core;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;
import com.clematis.core.episode.episodeResource;
import com.clematis.database.MongoInterface;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;


public class ClematisApplication extends Application {

   private Set<Class<?>> classes=new HashSet<Class<?>>();
   private Set<Object> singletons = new HashSet<Object>();
 
	public ClematisApplication() {
		System.out.println("Clematis Application");
		
		MongoInterface mongo = new MongoInterface();
		singletons.add(new episodeResource());
		
	    Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");

	    SecurityManager securityManager = factory.getInstance();

	    SecurityUtils.setSecurityManager(securityManager);
	}
 
	public Set<Object> getSingletons() {
		return singletons;
	}
}