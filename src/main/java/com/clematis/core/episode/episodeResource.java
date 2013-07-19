package com.clematis.core.episode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.clematis.core.trace.DOMElementValueTrace;
import com.clematis.core.trace.DOMEventTrace;
import com.clematis.core.trace.DOMMutationTrace;
import com.clematis.core.trace.FunctionCall;
import com.clematis.core.trace.FunctionEnter;
import com.clematis.core.trace.FunctionExit;
import com.clematis.core.trace.FunctionReturnStatement;
import com.clematis.core.trace.FunctionTrace;
import com.clematis.core.trace.TimeoutCallback;
import com.clematis.core.trace.TimeoutSet;
import com.clematis.core.trace.TimingTrace;
import com.clematis.core.trace.TraceObject;
import com.clematis.core.trace.XMLHttpRequestOpen;
import com.clematis.core.trace.XMLHttpRequestResponse;
import com.clematis.core.trace.XMLHttpRequestSend;
import com.clematis.core.trace.XMLHttpRequestTrace;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

@Path("/clematis-api")
@Produces({ "application/json" })
public class episodeResource {

	private Story s1;
	private ObjectMapper mapper = new ObjectMapper();
	private Map<String, Episode> episodeMap = new HashMap<String, Episode>(200);

	public void configureObjectMapper() {
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.setVisibilityChecker(VisibilityChecker.Std.defaultInstance()
		        .withFieldVisibility(
		                Visibility.ANY));
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// to allow coercion of JSON empty String ("") to null Object value:
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	}

	public String intialize() {

		configureObjectMapper();
		try {
			this.s1 = mapper.readValue(new File("story.json"),
			        Story.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < s1.getEpisodes().size(); i++) {
			episodeMap.put(Integer.toString(i), s1.getEpisodes().get(i));
		}

		return "successfully intialized story";

	}

	@GET
	@Path("/episodes/howmany")
	@Produces(MediaType.APPLICATION_JSON)
	public int NumberOfEpisodes() {
		intialize();
		return this.s1.getEpisodes().size();
	}

	@GET
	@Path("/episodes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Episode> getEpisodes() {
		intialize();
		return this.s1.getEpisodes();

	}

	@GET
	@Path("/episodes/pretty")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEpisodesPretty() {
		intialize();
		try {
			return mapper.writeValueAsString(this.s1.getEpisodes());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("/episodes/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Episode getEpisode(@PathParam("id") String id) {
		intialize();
		return episodeMap.get(id);
	}

	@GET
	@Path("/episodes/{id}/DOM")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEpisodeDom(@PathParam("id") String id)
	{
		intialize();

		if (episodeMap.get(id).getDom() == null) {
			return "DOM is NULL!";
		}
		else {
			return episodeMap.get(id).getDom();
		}
	}

	@GET
	@Path("/episodes/{id}/source")
	@Produces(MediaType.APPLICATION_JSON)
	public TraceObject getEpisodeSource(@PathParam("id") String id) {
		intialize();
		return episodeMap.get(id).getSource();
	}

	@GET
	@Path("/episodes/{id}/trace")
	@Produces(MediaType.APPLICATION_JSON)
	public EpisodeTrace getEpisodeTrace(@PathParam("id") String id) {
		intialize();
		return episodeMap.get(id).getTrace();
	}

	// ///////////////////Resources to get information about traces.////////////////
	/*
	 * @GET
	 * @Path("/episodes/{id}/trace/{type}")
	 * @Produces(MediaType.APPLICATION_JSON) public List<TraceObject> getStuff(@PathParam("id")
	 * String id, @PathParam("type") String type) { intialize(); List<FunctionTrace> functionTraces
	 * = new ArrayList<FunctionTrace>(); List<DOMMutationTrace> DOMMutationTraces = new
	 * ArrayList<DOMMutationTrace>(); List<DOMElementValueTrace> DOMElementValueTraces = new
	 * ArrayList<DOMElementValueTrace>(); List<XMLHttpRequestTrace> XMLHttpRequestTraces = new
	 * ArrayList<XMLHttpRequestTrace>(); List<TimingTrace> TimingTraces = new
	 * ArrayList<TimingTrace>(); List<DOMEventTrace> DOMEventTraces = new
	 * ArrayList<DOMEventTrace>(); for (TraceObject to : episodeMap.get(id).getTrace().getTrace()) {
	 * if (to instanceof FunctionTrace) { functionTraces.add((FunctionTrace) to); } else if (to
	 * instanceof DOMMutationTrace) { DOMMutationTraces.add((DOMMutationTrace) to); } else if (to
	 * instanceof DOMElementValueTrace) { DOMElementValueTraces.add((DOMElementValueTrace) to); }
	 * else if (to instanceof XMLHttpRequestTrace) { XMLHttpRequestTraces.add((XMLHttpRequestTrace)
	 * to); } else if (to instanceof TimingTrace) { TimingTraces.add((TimingTrace) to); } else if
	 * (to instanceof DOMEventTrace) { DOMEventTraces.add((DOMEventTrace) to); } } if (type ==
	 * "functionTrace") { return functionTraces; } else if (type == "DOMMutationTrace") { return
	 * DOMMutationTraces; } }
	 */
	@GET
	@Path("/episodes/{id}/trace/functionTrace")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FunctionTrace> getFunctionTrace(@PathParam("id") String id) {
		intialize();
		List<FunctionTrace> functionTraces = new ArrayList<FunctionTrace>();

		for (TraceObject to : episodeMap.get(id).getTrace().getTrace()) {
			if (to instanceof FunctionTrace) {
				functionTraces.add((FunctionTrace) to);
			}
		}
		return functionTraces;
	}

	@GET
	@Path("/episodes/{id}/trace/DOMMutationTrace")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DOMMutationTrace> getDOMMutationTrace(@PathParam("id") String id) {
		intialize();
		List<DOMMutationTrace> DOMMutationTraces = new ArrayList<DOMMutationTrace>();

		for (TraceObject to : episodeMap.get(id).getTrace().getTrace()) {
			if (to instanceof DOMMutationTrace) {
				DOMMutationTraces.add((DOMMutationTrace) to);
			}
		}
		return DOMMutationTraces;
	}

	@GET
	@Path("/episodes/{id}/trace/DOMElementValueTrace")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DOMElementValueTrace> getDOMElementValueTrace(@PathParam("id") String id) {
		intialize();
		List<DOMElementValueTrace> DOMElementValueTraces = new ArrayList<DOMElementValueTrace>();

		for (TraceObject to : episodeMap.get(id).getTrace().getTrace()) {
			if (to instanceof DOMElementValueTrace) {
				DOMElementValueTraces.add((DOMElementValueTrace) to);
			}
		}
		return DOMElementValueTraces;
	}

	@GET
	@Path("/episodes/{id}/trace/XMLHttpRequestTrace")
	@Produces(MediaType.APPLICATION_JSON)
	public List<XMLHttpRequestTrace> getXMLHttpRequestTrace(@PathParam("id") String id) {
		intialize();
		List<XMLHttpRequestTrace> XMLHttpRequestTraces = new ArrayList<XMLHttpRequestTrace>();

		for (TraceObject to : episodeMap.get(id).getTrace().getTrace()) {
			if (to instanceof XMLHttpRequestTrace) {
				XMLHttpRequestTraces.add((XMLHttpRequestTrace) to);
			}
		}
		return XMLHttpRequestTraces;
	}

	@GET
	@Path("/episodes/{id}/trace/TimingTrace")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TimingTrace> getTimingTrace(@PathParam("id") String id) {
		intialize();
		List<TimingTrace> TimingTraces = new ArrayList<TimingTrace>();

		for (TraceObject to : episodeMap.get(id).getTrace().getTrace()) {
			if (to instanceof TimingTrace) {
				TimingTraces.add((TimingTrace) to);
			}
		}
		return TimingTraces;
	}

	@GET
	@Path("/episodes/{id}/trace/DOMEventTrace")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DOMEventTrace> getDOMEventTrace(@PathParam("id") String id) {
		intialize();
		List<DOMEventTrace> DOMEventTraces = new ArrayList<DOMEventTrace>();

		for (TraceObject to : episodeMap.get(id).getTrace().getTrace()) {
			if (to instanceof DOMEventTrace) {
				DOMEventTraces.add((DOMEventTrace) to);
			}
		}
		return DOMEventTraces;
	}

	// //////////////////////////////////////////////////////////////////////////////////

	// ///////////////////Resources to get information about function traces.////////////////
	@GET
	@Path("/episodes/{id}/trace/functionTrace/FunctionCall")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FunctionCall> getFunctionCall(@PathParam("id") String id) {
		intialize();
		List<FunctionCall> FunctionCalls = new ArrayList<FunctionCall>();

		for (TraceObject to : getFunctionTrace(id)) {
			if (to instanceof FunctionCall) {
				FunctionCalls.add((FunctionCall) to);
			}
		}
		return FunctionCalls;
	}

	@GET
	@Path("/episodes/{id}/trace/functionTrace/FunctionEnter")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FunctionEnter> getFunctionEnter(@PathParam("id") String id) {
		intialize();
		List<FunctionEnter> FunctionEnters = new ArrayList<FunctionEnter>();

		for (TraceObject to : getFunctionTrace(id)) {
			if (to instanceof FunctionEnter) {
				FunctionEnters.add((FunctionEnter) to);
			}
		}
		return FunctionEnters;
	}

	@GET
	@Path("/episodes/{id}/trace/functionTrace/FunctionExit")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FunctionExit> getFunctionExit(@PathParam("id") String id) {
		intialize();
		List<FunctionExit> FunctionExits = new ArrayList<FunctionExit>();

		for (TraceObject to : getFunctionTrace(id)) {
			if (to instanceof FunctionExit) {
				FunctionExits.add((FunctionExit) to);
			}
		}
		return FunctionExits;
	}

	@GET
	@Path("/episodes/{id}/trace/functionTrace/FunctionReturnStatement")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FunctionReturnStatement> getFunctionReturnStatement(@PathParam("id") String id) {
		intialize();
		List<FunctionReturnStatement> FunctionReturnStatements =
		        new ArrayList<FunctionReturnStatement>();

		for (TraceObject to : getFunctionTrace(id)) {
			if (to instanceof FunctionReturnStatement) {
				FunctionReturnStatements.add((FunctionReturnStatement) to);
			}
		}
		return FunctionReturnStatements;
	}

	// /////////////////////////////////////////////////////////////////////////////////////

	// ///////////////////Resources to get information about timing traces.////////////////

	@GET
	@Path("/episodes/{id}/trace/TimingTrace/TimeoutCallback")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TimeoutCallback> getTimeoutCallback(@PathParam("id") String id) {
		intialize();
		List<TimeoutCallback> TimeoutCallbacks = new ArrayList<TimeoutCallback>();

		for (TraceObject to : getTimingTrace(id)) {
			if (to instanceof TimeoutCallback) {
				TimeoutCallbacks.add((TimeoutCallback) to);
			}
		}
		return TimeoutCallbacks;
	}

	@GET
	@Path("/episodes/{id}/trace/TimingTrace/TimeoutSet")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TimeoutSet> getTimeoutSet(@PathParam("id") String id) {
		intialize();
		List<TimeoutSet> TimeoutSets = new ArrayList<TimeoutSet>();

		for (TraceObject to : getTimingTrace(id)) {
			if (to instanceof TimeoutSet) {
				TimeoutSets.add((TimeoutSet) to);
			}
		}
		return TimeoutSets;
	}

	// /////////////////////////////////////////////////////////////////////////////////////

	// ///////////////////Resources to get information about XMLHTTPRequest traces.////////////////

	@GET
	@Path("/episodes/{id}/trace/XMLHttpRequestTrace/XMLHttpRequestOpen")
	@Produces(MediaType.APPLICATION_JSON)
	public List<XMLHttpRequestOpen> getXMLHttpRequestOpen(@PathParam("id") String id) {
		intialize();
		List<XMLHttpRequestOpen> XMLHttpRequestOpens = new ArrayList<XMLHttpRequestOpen>();

		for (TraceObject to : getXMLHttpRequestTrace(id)) {
			if (to instanceof XMLHttpRequestOpen) {
				XMLHttpRequestOpens.add((XMLHttpRequestOpen) to);
			}
		}
		return XMLHttpRequestOpens;
	}

	@GET
	@Path("/episodes/{id}/trace/XMLHttpRequestTrace/XMLHttpRequestResponse")
	@Produces(MediaType.APPLICATION_JSON)
	public List<XMLHttpRequestResponse> getXMLHttpRequestResponse(@PathParam("id") String id) {
		intialize();
		List<XMLHttpRequestResponse> XMLHttpRequestResponses =
		        new ArrayList<XMLHttpRequestResponse>();

		for (TraceObject to : getXMLHttpRequestTrace(id)) {
			if (to instanceof XMLHttpRequestResponse) {
				XMLHttpRequestResponses.add((XMLHttpRequestResponse) to);
			}
		}
		return XMLHttpRequestResponses;
	}

	@GET
	@Path("/episodes/{id}/trace/XMLHttpRequestTrace/XMLHttpRequestSend")
	@Produces(MediaType.APPLICATION_JSON)
	public List<XMLHttpRequestSend> getXMLHttpRequestSend(@PathParam("id") String id) {
		intialize();
		List<XMLHttpRequestSend> XMLHttpRequestSends = new ArrayList<XMLHttpRequestSend>();

		for (TraceObject to : getXMLHttpRequestTrace(id)) {
			if (to instanceof XMLHttpRequestSend) {
				XMLHttpRequestSends.add((XMLHttpRequestSend) to);
			}
		}
		return XMLHttpRequestSends;
	}

	// /////////////////////////////////////////////////////////////////////////////////////

	@GET
	@Path("/story/timingTraces")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TraceObject> getTimingTraces() {
		intialize();
		return this.s1.getTimingTraces();
	}

	@GET
	@Path("/story/domEventTraces")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TraceObject> getDomEventTraces() {
		intialize();
		return this.s1.getDomEventTraces();
	}

	@GET
	@Path("/story/XHRTraces")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TraceObject> getXHRTraces() {
		intialize();
		return this.s1.getXhrTraces();
	}

	@GET
	@Path("/story/functionTraces")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TraceObject> getFunctionTraces() {
		intialize();
		return this.s1.getFunctionTraces();
	}

	// need to find which episodes have timeouts, then need to find corresponding callbacks
	@GET
	@Path("/story/causalLinks")
	@Produces(MediaType.APPLICATION_JSON)
	public List<causalLinks> episodesContainTimeouts() {

		intialize();
		List<causalLinks> causalLinkss = new ArrayList<causalLinks>();

		for (int i = 0; i < episodeMap.size(); i++) {
			String strI = "" + i;
			getTimeoutSet(strI);
			// if episode contains a timeout, find the corresponding callback
			if (getTimeoutSet(strI).size() > 0) {
				for (int x = 0; x < getTimeoutSet(strI).size(); x++) {
					for (int z = 0; z < episodeMap.size(); z++) {
						String strZ = "" + z;
						if (getTimeoutCallback(strZ).size() > 0)
						{
							for (int zz = 0; zz < getTimeoutCallback(strZ).size(); zz++) {
								if (getTimeoutSet(strI).get(x).getId() == getTimeoutCallback(strZ)
								        .get(zz).getId()) {
									causalLinkss.add(new causalLinks(i, z));
								}
							}
						}
					}
				}
			}

			if (getXMLHttpRequestOpen(strI).size() > 0) {
				for (int x = 0; x < getXMLHttpRequestOpen(strI).size(); x++) {
					for (int z = 0; z < episodeMap.size(); z++) {
						String strZ = "" + z;
						if (getXMLHttpRequestResponse(strZ).size() > 0)
						{
							for (int zz = 0; zz < getXMLHttpRequestResponse(strZ).size(); zz++) {
								if (getXMLHttpRequestOpen(strI).get(x).getId() == getXMLHttpRequestResponse(
								        strZ)
								        .get(zz).getId()) {
									causalLinkss.add(new causalLinks(i, z));
								}
							}
						}
					}
				}
			}

		}
		return causalLinkss;

	}

	@GET
	@Path("/story/sequenceDiagram")
	@Produces(MediaType.APPLICATION_JSON)
	public String getsequenceDiagram() {
		intialize();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		for (Episode e : this.s1.getEpisodes()) {
			// Create pic files for each episode's sequence diagram
			com.clematis.jsmodify.JSExecutionTracer.designSequenceDiagram(e, ps);
		}
		String output = null;
		try {
			output = os.toString("UTF8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// System.out.println(output);
		ps.close();
		return output;
	}

	@GET
	@Path("/story/sequenceDiagram/new")
	@Produces(MediaType.APPLICATION_JSON)
	public String getsequenceDiagramNew() {
		intialize();
		PrintStream JSepisodes = null;
		try {
			JSepisodes = new PrintStream("allEpisodesNew.js");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (Episode e : this.s1.getEpisodes()) {
			// Create pic files for each episode's sequence diagram
			com.clematis.jsmodify.JSExecutionTracer.designSequenceDiagram(e, JSepisodes);
		}

		// Once all episodes have been saved to JS file, close
		JSepisodes.close();

		String output = "success";

		return output;
	}

}
