package com.metis.core.trace;

import com.metis.core.episode.EpisodeSource;

public class TimeoutCallback extends TimingTrace/* implements EpisodeSource*/ {
	public TimeoutCallback() {
		super();
		setEpisodeSource(true);
	}
}
