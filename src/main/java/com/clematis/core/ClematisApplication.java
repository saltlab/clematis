package com.clematis.core;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.clematis.core.episode.episodeResource;
import com.clematis.database.MongoInterface;


public class ClematisApplication extends Application {

    private Set<Class<?>> classes=new HashSet<Class<?>>();
   private Set<Object> singletons = new HashSet<Object>();
 
	public ClematisApplication() {
		MongoInterface mongo = new MongoInterface();
		singletons.add(new episodeResource());
	}
 
	public Set<Object> getSingletons() {
		return singletons;
	}
}