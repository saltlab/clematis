package com.metis.core.trace;

public class FunctionEnter extends FunctionTrace {
	private String TargetFunction;

	public String getTargetFunction() {
		return TargetFunction;
	}

	public void setTargetFunction(String targetFunction) {
		TargetFunction = targetFunction;
	}	
}
