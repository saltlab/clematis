package com.metis.core.episode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.exec.TimeoutObserver;

import com.metis.core.trace.TimeoutCallback;
import com.metis.core.trace.TimeoutSet;
import com.metis.core.trace.TraceObject;
import com.metis.core.trace.XMLHttpRequestOpen;
import com.metis.core.trace.XMLHttpRequestResponse;
import com.metis.core.trace.XMLHttpRequestSend;

public class Story {
	private ArrayList<TraceObject> domEventTraces;
	private ArrayList<TraceObject> functionTraces;
	private ArrayList<TraceObject> timingTraces;
	private ArrayList<TraceObject> xhrTraces;
	private ArrayList<TraceObject> orderedTraceList;
	private ArrayList<Episode> episodes;
	
	private HashMap<Integer, TimeoutSet> timeoutSets; // by to_id
	private HashMap<Integer, TimeoutCallback> timeoutCallbacks;
	private HashMap<Integer, XMLHttpRequestOpen> xhrOpens;
	private HashMap<Integer, XMLHttpRequestSend> xhrSends;
	private HashMap<Integer, XMLHttpRequestResponse> xhrResponses;
	
//	private HashMap<TraceObject, Episode> traceObjectToEpisodeMap;
	
	public Story(Collection<TraceObject> domEventCollection, Collection<TraceObject> functionCollection, Collection<TraceObject> timingCollection, Collection<TraceObject> xhrCollection) {
		domEventTraces = new ArrayList<TraceObject>(domEventCollection);
		functionTraces = new ArrayList<TraceObject>(functionCollection);
		timingTraces = new ArrayList<TraceObject>(timingCollection);
		xhrTraces = new ArrayList<TraceObject>(xhrCollection);
		orderedTraceList = new ArrayList<TraceObject>();
		episodes = new ArrayList<Episode>();
		
		timeoutSets = new HashMap<Integer, TimeoutSet>();
		timeoutCallbacks = new HashMap<Integer, TimeoutCallback>();
		linkTimeoutComponents();
		
		xhrOpens = new HashMap<Integer, XMLHttpRequestOpen>();
		xhrSends = new HashMap<Integer, XMLHttpRequestSend>();
		xhrResponses = new HashMap<Integer, XMLHttpRequestResponse>();
		linkXhrComponents();
	}
	
	// Linking different components of timeouts
	private void linkTimeoutComponents() {
		for (TraceObject to : timingTraces) {
			if (to instanceof TimeoutSet)
				timeoutSets.put(((TimeoutSet) to).getTimeoutId(), (TimeoutSet) to);
			else if (to instanceof TimeoutCallback) 
				timeoutCallbacks.put(((TimeoutCallback) to).getTimeoutId(), (TimeoutCallback) to);
			else
				System.err.println("invalid timing trace");
		}
	}
	
	public TimeoutCallback getTimeoutCallback(TimeoutSet timeoutSet) {
		return timeoutCallbacks.get(timeoutSet.getTimeoutId());
	}
	
	public TimeoutSet getTimeoutSet(TimeoutCallback timeoutCallback) {
		return timeoutSets.get(timeoutCallback.getTimeoutId());
	}
	
	// Linking different components of xhrs
	private void linkXhrComponents() {
		for (TraceObject to : xhrTraces) {
			if (to instanceof XMLHttpRequestOpen)
				xhrOpens.put(((XMLHttpRequestOpen) to).getXhrId(), (XMLHttpRequestOpen) to);
			else if (to instanceof XMLHttpRequestSend)
				xhrSends.put(((XMLHttpRequestSend) to).getXhrId(), (XMLHttpRequestSend) to);
			else if (to instanceof XMLHttpRequestResponse)
				xhrResponses.put(((XMLHttpRequestResponse) to).getXhrId(), (XMLHttpRequestResponse) to);
			else
				System.err.println("invalid xhr trace");
			
		}
	}
	
	public XMLHttpRequestOpen getXMLHttpRequestOpen(XMLHttpRequestSend xhrSend) {
		return xhrOpens.get(xhrSend.getXhrId());
	}

	public XMLHttpRequestOpen getXMLHttpRequestOpen(XMLHttpRequestResponse xhrResponse) {
		return xhrOpens.get(xhrResponse.getXhrId());
	}
	
	public XMLHttpRequestSend getXMLHttpRequestSend(XMLHttpRequestOpen xhrOpen) {
		return xhrSends.get(xhrOpen.getXhrId());
	}

	public XMLHttpRequestSend getXMLHttpRequestSend(XMLHttpRequestResponse xhrResponse) {
		return xhrSends.get(xhrResponse.getXhrId());
	}
	
	public XMLHttpRequestResponse getXMLHttpRequestResponse(XMLHttpRequestOpen xhrOpen) {
		return xhrResponses.get(xhrOpen.getXhrId());
	}
	
	public XMLHttpRequestResponse getXMLHttpRequestResponse(XMLHttpRequestSend xhrSend) {
		return xhrResponses.get(xhrSend.getXhrId());
	}

	// Get information about an episode
	
	public TraceObject getEpisodeSource(Episode e) {
		return e.getSource();
	}
	
	public EpisodeTrace getEpisodeTrace(Episode e) {
		return e.getTrace();
	}
	
	public String getEpisodeDom(Episode e) {
		return e.getDom();
	}
	
	// Get information about the trace
	
	public TraceObject getNextTraceObject(TraceObject to) {
		for (int i = 0; i < orderedTraceList.size(); i ++) {
			if (orderedTraceList.get(i) == to) { // copy constructor (?)
				if (i + 1 < orderedTraceList.size())
					return orderedTraceList.get(i + 1);
			}
		}
		return null;
	}
	
	public Episode getEpisode(TraceObject to) {
		for (Episode e : episodes) {
			if (e.getTrace().getTrace().contains(to))
				return e;
		}
		return null;
	}

	public ArrayList<TraceObject> getDomEventTraces() {
		return domEventTraces;
	}

	public void setDomEventTraces(ArrayList<TraceObject> domEventTraces) {
		this.domEventTraces = domEventTraces;
	}

	public ArrayList<TraceObject> getFunctionTraces() {
		return functionTraces;
	}

	public void setFunctionTraces(ArrayList<TraceObject> functionTraces) {
		this.functionTraces = functionTraces;
	}

	public ArrayList<TraceObject> getTimingTraces() {
		return timingTraces;
	}

	public void setTimingTraces(ArrayList<TraceObject> timingTraces) {
		this.timingTraces = timingTraces;
	}

	public ArrayList<TraceObject> getXhrTraces() {
		return xhrTraces;
	}

	public void setXhrTraces(ArrayList<TraceObject> xhrTraces) {
		this.xhrTraces = xhrTraces;
	}

	public ArrayList<TraceObject> getOrderedTraceList() {
		return orderedTraceList;
	}

	public void setOrderedTraceList(ArrayList<TraceObject> orderedTraceList) {
		this.orderedTraceList = orderedTraceList;
	}

	public ArrayList<Episode> getEpisodes() {
		return episodes;
	}

	public void setEpisodes(ArrayList<Episode> episodes) {
		this.episodes = episodes;
	}
	

}
