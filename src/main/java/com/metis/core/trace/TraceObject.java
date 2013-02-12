package com.metis.core.trace;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class TraceObject implements Comparable {
	private int id;
	private int counter;
	private String messageType;
	private long timeStamp;

	
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
	@Override
	public int compareTo(Object o) {
		if (timeStamp < ((TraceObject)o).getTimeStamp())
			return -1;
		else if (timeStamp > ((TraceObject)o).getTimeStamp())
			return 1;
		return 0;
	}
	
}
