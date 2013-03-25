package com.metis.core.trace;

import org.codehaus.jackson.annotate.JsonSetter;
import org.json.JSONException;
import org.json.JSONObject;

public class DOMEventTrace extends TraceObject/* implements EpisodeSource */{
	private String eventType;
	private String eventHandler;
	private JSONObject targetElement;

	public DOMEventTrace() {
		super();
		setEpisodeSource(true);
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getEventHandler() {
		if (eventHandler == "") {
			return "anonymous";
		} else {
			return eventHandler;
		}
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

	public String getTargetElement() {
		//System.out.println(targetElement);
		try {
			return targetElement.getString("attributes").replaceAll("\"", "");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getTargetElementAttributes() {
		try {
			if (targetElement.get("attributes").toString().replaceAll("\"", "").length() > 22) {
				// If there are too many attributes for the target element, print a shorter version 
				// This is specific to the pic2plot sequence diagram as the comment box is not very big
				return targetElement.get("attributes").toString().replaceAll("\"", "").substring(0, 19)+"...";
			} else {
				return targetElement.get("attributes").toString().replaceAll("\"", "");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
}
