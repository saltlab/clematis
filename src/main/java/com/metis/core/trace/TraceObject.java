package com.metis.core.trace;

public class TraceObject {
	int lineNo;
	String messageType;
	TimeStamp timeStamp;
	String eventType;
//	DOMElement targetElement;
	//Function eventHandler;
	int ID;
	//Function callbackFunction;
	String targetFunction;
	int delayTime;
	String[] argus;//
	String methodType;
	String serverURL;
	boolean async;
	String message;
	String response;
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTargetFunction() {
		return targetFunction;
	}
	public void setTargetFunction(String targetFunction) {
		this.targetFunction = targetFunction;
	}
	public int getLineNo() {
		return lineNo;
	}
	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
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
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public int getID() {
		return ID;
	}
	public void setID(int ID) {
		this.ID = ID;
	}
	public int getDelayTime() {
		return delayTime;
	}
	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}
	public String[] getArgus() {
		return argus;
	}
	public void setArgus(String[] argus) {
		this.argus = argus;
	}
	public String getMethodType() {
		return methodType;
	}
	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}
	public String getServerURL() {
		return serverURL;
	}
	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}
	public boolean isAsync() {
		return async;
	}
	public void setAsync(boolean async) {
		this.async = async;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
/*	public DOMElement getTargetElement() {
		return targetElement;
	}
	public void setTargetElement(DOMElement targetElement) {
		this.targetElement = targetElement;
	}
	*/
	
}
