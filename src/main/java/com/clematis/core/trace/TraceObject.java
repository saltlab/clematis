package com.clematis.core.trace;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class TraceObject implements Comparable<TraceObject> {
	private int id;
	private int counter;
	private String messageType;
	private long timeStamp;
	private boolean isEpisodeSource;
//	private boolean isBookmarked;
	
	public TraceObject() {
		isEpisodeSource = false;
//		isBookmarked = false;
	}
	
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public int compareTo(TraceObject o) {
		if (counter < o.getCounter())
			return -1;
		else if (counter > o.getCounter())
			return 1;
		return 0;
	}
	
	public boolean isEpisodeSource() {
		return isEpisodeSource;
	}
	
	public void setEpisodeSource(boolean isEpisodeSource) {
		this.isEpisodeSource = isEpisodeSource;
	}
/*
	public boolean getIsBookmarked() {
		return isBookmarked;
	}

	public void setIsBookmarked(boolean isBookmarked) {
		this.isBookmarked = isBookmarked;
	}
	*/
}
