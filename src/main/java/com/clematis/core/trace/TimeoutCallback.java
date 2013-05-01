package com.clematis.core.trace;

public class TimeoutCallback extends TimingTrace/* implements EpisodeSource*/ {
	public TimeoutCallback() {
		super();
		setEpisodeSource(true);
	}
}
