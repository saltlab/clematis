package com.clematis.core.trace;

public class FunctionExit extends FunctionTrace {
	private String TargetFunction;
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
}
