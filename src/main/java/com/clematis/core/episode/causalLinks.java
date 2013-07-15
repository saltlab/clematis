package com.clematis.core.episode;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class causalLinks {

	private int source;
	private int target;
	String description;

	public causalLinks() {

	}

	public causalLinks(int target, int source, String description) {
		this.source = source;
		this.target = target;
		this.description = new String(description);

	}

	public void setSource(int source) {
		this.source = source;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSource() {
		return this.source;
	}

	public int getTarget(String target) {
		return this.target;
	}

	public String getDescription(String description) {
		return this.description;
	}

}
