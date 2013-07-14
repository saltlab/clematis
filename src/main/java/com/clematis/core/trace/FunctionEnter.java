package com.clematis.core.trace;

import org.codehaus.jackson.annotate.JsonSetter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FunctionEnter extends FunctionTrace {
	private String TargetFunction;
	private JSONArray args = new JSONArray();
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

	public String getArgs() {
		return args == null ? null : args.toString();
	}

	@JsonSetter("args")
	public void setArgs(String args_string) {
		try {
			this.args = new JSONArray(args_string);
		} catch (JSONException e) {
			System.out.println("Exception constructing JSONObject from string " + args_string);
			e.printStackTrace();
		}
	}

	public String getArgsString() {

		String arguments = "";

		for (int i = 0; i < args.length(); i++) {
			try {
				String labels[] = JSONObject.getNames(args.getJSONObject(i));
				arguments +=
				        ", "
				                + args.getJSONObject(i).get(labels[0]).toString()
				                        .replaceAll("\"", "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return arguments.replaceFirst(", ", "");
	}

	public String getArgsLabels() {
		if (args == null) {
			return "";
		} else {
			String argumentLabels = "";

			for (int i = 0; i < args.length(); i++) {
				try {
					String labels[] = JSONObject.getNames(args.getJSONObject(i));
					argumentLabels += ", " + labels[0].toString().replaceAll("\"", "");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			return argumentLabels.replaceFirst(", ", "");
		}
	}
}
