package com.metis.core.trace;

import org.codehaus.jackson.annotate.JsonSetter;
import org.json.JSONException;
import org.json.JSONObject;

public class DOMEventTrace extends TraceObject {
	private String eventType;
	private String eventHandler;
	private JSONObject targetElement;

	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getEventHandler() {
		return eventHandler;
	}
	public void setEventHandler(String eventHandler) {
		this.eventHandler = eventHandler;
	}
	@JsonSetter("targetElement")
	public void setTargetElement(String targetElement_string) {
		try {
			this.targetElement = new JSONObject(targetElement_string);			
		} catch (JSONException e) {
			System.out.println("Exception constructing JSONObject from string " + targetElement_string);
			e.printStackTrace();
		}
	}
	public JSONObject getTargetElement() {
		return targetElement;
	}

}
