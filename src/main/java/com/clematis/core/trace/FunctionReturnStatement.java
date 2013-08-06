package com.clematis.core.trace;

import org.codehaus.jackson.annotate.JsonSetter;
import org.json.JSONException;
import org.json.JSONObject;

public class FunctionReturnStatement extends FunctionTrace {
	private String label, value;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	/*
	private JSONObject returnValue;

	public Object getReturnValue() {
		try {
			return getReturn().get("value");
		} catch (JSONException e) {
			return null;
		}	
	}

	public String getReturnLabel() {
		try {
			return (String) getReturn().get("label");
		} catch (JSONException e) {
			return "";
		}
	}

	public JSONObject getReturn() {
		return returnValue;
	}

	@JsonSetter("returnValue")
	public void setReturnValue(String rv_string) {
		if(rv_string == null) {
			this.returnValue = null;
			return;
		}
		
		// Convert return value to JSONObject if not null
		try {
			this.returnValue = new JSONObject(rv_string);			
		} catch (JSONException e) {
			System.out.println("Exception constructing JSONObject from string " + rv_string);
			e.printStackTrace();
		} catch (Exception ee) {
			System.out.println("Exception constructing JSONObject from string " + rv_string);
			ee.printStackTrace();
		}
	}	
	*/
}
