package com.clematis.core.trace;

public class TimingTrace extends TraceObject {
	private int timeoutId;
	private String callbackFunction;

	public int getTimeoutId() {
		return timeoutId;
	}
	public void setTimeoutId(int timeoutId) {
		this.timeoutId = timeoutId;
	}
	public String getCallbackFunction() {
		if (callbackFunction == "") {
			return "anonymous";
		} else {
			return callbackFunction;
		}
	}
	public void setCallbackFunction(String callbackFunction) {
		this.callbackFunction = callbackFunction;
	}	
}
