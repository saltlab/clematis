package com.metis.core.trace;

public class FunctionEnter extends FunctionTrace {
	private String TargetFunction;
	private Variable[] args;

	public String getTargetFunction() {
		return TargetFunction;
	}

	public void setTargetFunction(String targetFunction) {
		TargetFunction = targetFunction;
	}

	public Variable[] getArgs() {
		return args;
	}

	public void setArgs(Variable[] args) {
		this.args = args;
	}
}
