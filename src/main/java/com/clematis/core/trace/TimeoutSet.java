package com.clematis.core.trace;

import org.codehaus.jackson.annotate.JsonSetter;
import org.json.JSONArray;
import org.json.JSONException;

public class TimeoutSet extends TimingTrace {
	private int delay;
	private JSONArray args = new JSONArray(); // Variable[]

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public String getArgs() {
		// return args;
		return args == null ? null : args.toString();
	}

	@JsonSetter("args")
	public void setArgs(String args_string) {
		try {
			this.args = new JSONArray("[" + args_string + "]");
		} catch (JSONException e) {
			System.out.println("Exception constructing JSONObject from string " + args_string);
			e.printStackTrace();
		}
	}
}
