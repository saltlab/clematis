package com.metis.core.trace;

public class DOMEventTrace extends TraceObject {
	private String eventType;
	private DOMElement targetElement;
//	private String eventHandler; // should be of type Function?

	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public DOMElement getTargetElement() {
		return targetElement;
	}
	public void setTargetElement(DOMElement targetElement) {
		this.targetElement = targetElement;
	}
/*	public String getEventHandler() {
		return eventHandler;
	}
	public void setEventHandler(String eventHandler) {
		this.eventHandler = eventHandler;
	}
	*/	
}
