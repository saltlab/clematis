package com.clematis.core;

public class ClematisSession implements Runnable {
	private String url; 
	private SimpleExample newSession;
	
	public ClematisSession(String url, SimpleExample session){
		this.url = url;
		this.newSession = session;
	}

	public void run() {
		newSession.begin();
	}

}

