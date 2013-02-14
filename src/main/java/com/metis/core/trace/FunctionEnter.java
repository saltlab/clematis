package com.metis.core.trace;

import org.codehaus.jackson.annotate.JsonSetter;
import org.json.JSONException;
import org.json.JSONObject;

public class FunctionEnter extends FunctionTrace {
	private String TargetFunction;
	private JSONObject args;
	String scopeName;

	public String getScopeName() {
		return scopeName;
	}

	public void setScopeName(String sn) {
		this.scopeName = sn;
	}
	
	public String getTargetFunction() {
		return TargetFunction;
	}

	public void setTargetFunction(String targetFunction) {
		TargetFunction = targetFunction;
	}

	public JSONObject getArgs() {
		return args;
	}

	@JsonSetter("args")
	public void setArgs(String args_string) {
		try {
			this.args = new JSONObject(args_string);			
		} catch (JSONException e) {
			System.out.println("Exception constructing JSONObject from string " + args_string);
			e.printStackTrace();
		}
	}
}
