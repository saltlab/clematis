package com.metis.core.trace;

public class TimeoutSet extends TimingTrace {
	private int delay;
	private String[] args; // Variable[]

	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	public String[] getArgs() {
		return args;
	}
	public void setArgs(String[] args) {
		this.args = args;
	}
}
