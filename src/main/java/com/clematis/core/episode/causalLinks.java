package com.clematis.core.episode;

public class causalLinks {
	private int source;
	private int target;

	public causalLinks() {

	}

	public causalLinks(int source, int target) {

		this.source = source;
		this.target = target;

	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getSource() {
		return this.source;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public int getTarget() {
		return this.target;
	}

}
