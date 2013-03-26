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
		return TargetFunction.trim().replaceAll(" ", "");
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

	public String getArgsString() {

		String[] labels = JSONObject.getNames(getArgs());
		String arguments = "";

		for (int i=0; i<labels.length; i++) {
			try {
				arguments += ", " + getArgs().get(labels[i]).toString().replaceAll("\"", "");
			} catch (JSONException e) {
				System.setOut(System.out);
				System.out.println("Error translating arguments into String.");
				e.printStackTrace();
			}	
		}

		return arguments.replaceFirst(", ", "");
	}

	public String getArgsLabels() {
		if (getArgs() == null) {
			return "";
		} else {
			String[] labels = JSONObject.getNames(getArgs());
			String argumentLabels = "";

			for (int i=0; i<labels.length; i++) {
				argumentLabels += ", " + labels[i].replaceAll("\"", "");	
			}

			return argumentLabels.replaceFirst(", ", "");
		}
	}
}
