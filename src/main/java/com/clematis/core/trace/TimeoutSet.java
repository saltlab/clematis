package com.clematis.core.trace;

import org.codehaus.jackson.annotate.JsonSetter;
import org.json.JSONException;
import org.json.JSONObject;

public class TimeoutSet extends TimingTrace {
	private int delay;
	private JSONObject args; // Variable[]

	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
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
