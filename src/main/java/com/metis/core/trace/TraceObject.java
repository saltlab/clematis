package com.metis.core.trace;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class TraceObject implements Comparable {
	private int id;
	private int counter;
	private String messageType;
	private TimeStamp timeStamp;

	
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
	public TimeStamp getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(TimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		if (this.timeStamp.getYear() >= ((TraceObject) arg0).getTimeStamp().getYear())
			return 1;
		else
			return -1;
	}	
}
