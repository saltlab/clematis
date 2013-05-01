package com.clematis.core.episode;

import com.clematis.core.trace.TraceObject;

public class Episode {
//	private EpisodeSource source;
	private TraceObject source;
	private EpisodeTrace trace;
	private String dom;
	/*
	public Episode(EpisodeSource source) {
		trace = new EpisodeTrace();
	}*/
	public Episode(TraceObject source) {
		trace = new EpisodeTrace();
		this.source = source;
	}
	public void addToTrace(TraceObject to) {
		trace.addToTrace(to);
	}
/*	public EpisodeSource getSource() {
		return source;
	}
	public void setSource(EpisodeSource source) {
		this.source = source;
	}
*/	public EpisodeTrace getTrace() {
		return trace;
	}
	public void setTrace(EpisodeTrace trace) {
		this.trace = trace;
	}
	public String getDom() {
		return dom;
	}
	public void setDom(String dom) {
		this.dom = dom;
	}
	public TraceObject getSource() {
		return source;
	}
	public void setSource(TraceObject source) {
		this.source = source;
	}
}
