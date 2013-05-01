package com.clematis.core.episode;

import java.util.ArrayList;
import com.clematis.core.trace.TraceObject;

public class EpisodeTrace {
	private ArrayList<TraceObject> trace;

	public EpisodeTrace() {
		trace = new ArrayList<TraceObject>();
	}
	
	public void addToTrace(TraceObject to) {
		trace.add(to);
	}

	public ArrayList<TraceObject> getTrace() {
		return trace;
	}

	public void setTrace(ArrayList<TraceObject> trace) {
		this.trace = trace;
	}
}
