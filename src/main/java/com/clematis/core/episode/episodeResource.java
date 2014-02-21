package com.clematis.core.episode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

	public String intialize(String fileName) {
	    
		configureObjectMapper();
		try {
			this.s1 = mapper.readValue(new File("captured_stories/" + fileName + ".json"),
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
	@Path("/capturedStories")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getCapturedStories() {
		// Directory path here
		String path = "captured_stories";
		List<String> results = new ArrayList<String>();
		// String[] files = n;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++)
		{

			if (listOfFiles[i].isFile()) {
				results.add(listOfFiles[i].getName());
			}
		}
		return results;
	}

	@GET
	@Path("{fileName}/episodes/howmany")
	@Produces(MediaType.APPLICATION_JSON)
	public int NumberOfEpisodes(@PathParam("fileName") String fileName) {
		intialize(fileName);
		return this.s1.getEpisodes().size();
	}

	@GET
	@Path("{fileName}/episodes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Episode> getEpisodes(@PathParam("fileName") String fileName) {
	    	    
		intialize(fileName);
		return this.s1.getEpisodes();

	}

	@GET
	@Path("{fileName}/episodes/bookmarked")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getBookmarks(@PathParam("fileName") String fileName) {
		intialize(fileName);
		List<Integer> b1 = new ArrayList<Integer>();
		for (int i = 0; i < this.s1.getEpisodes().size(); i++) {
			if (this.s1.getEpisodes().get(i).getIsBookmarked() == true) {
				b1.add(i);
			}
		}
		return b1;

	}

	@GET
	@Path("{fileName}/episodes/pretty")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEpisodesPretty(@PathParam("fileName") String fileName) {
		intialize(fileName);
		try {
			return mapper.writeValueAsString(this.s1.getEpisodes());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("{fileName}/episodes/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Episode getEpisode(@PathParam("fileName") String fileName, @PathParam("id") String id) {
		intialize(fileName);
		return episodeMap.get(id);
	}

	@GET
	@Path("{fileName}/episodes/{id}/DOM")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEpisodeDom(@PathParam("fileName") String fileName, @PathParam("id") String id)
	{
		intialize(fileName);

		if (episodeMap.get(id).getDom() == null) {
			return "DOM is NULL!";
		}
		else {
			return episodeMap.get(id).getDom();
		}
	}

	@GET
	@Path("{fileName}/episodes/{id}/source")
	@Produces(MediaType.APPLICATION_JSON)
	public TraceObject getEpisodeSource(@PathParam("fileName") String fileName,
			@PathParam("id") String id) {
		intialize(fileName);
		return episodeMap.get(id).getSource();
	}

	@GET
	@Path("{fileName}/episodes/{id}/trace")
	@Produces(MediaType.APPLICATION_JSON)
	public EpisodeTrace getEpisodeTrace(@PathParam("fileName") String fileName,
			@PathParam("id") String id) {
		intialize(fileName);
		return episodeMap.get(id).getTrace();
	}

	@GET
	@Path("{fileName}/episodes/{id}/trace/functionTrace")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FunctionTrace> getFunctionTrace(@PathParam("fileName") String fileName,
			@PathParam("id") String id) {
		intialize(fileName);
		List<FunctionTrace> functionTraces = new ArrayList<FunctionTrace>();

		for (TraceObject to : episodeMap.get(id).getTrace().getTrace()) {
			if (to instanceof FunctionTrace) {
				functionTraces.add((FunctionTrace) to);
			}
		}
		return functionTraces;
	}

	@GET
	@Path("{fileName}/episodes/{id}/trace/DOMMutationTrace")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DOMMutationTrace> getDOMMutationTrace(@PathParam("fileName") String fileName,
			@PathParam("id") String id) {
		intialize(fileName);
		List<DOMMutationTrace> DOMMutationTraces = new ArrayList<DOMMutationTrace>();

		for (TraceObject to : episodeMap.get(id).getTrace().getTrace()) {
			if (to instanceof DOMMutationTrace) {
				DOMMutationTraces.add((DOMMutationTrace) to);
			}
		}
		return DOMMutationTraces;
	}

	@GET
	@Path("{fileName}/episodes/{id}/trace/DOMElementValueTrace")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DOMElementValueTrace> getDOMElementValueTrace(
			@PathParam("fileName") String fileName, @PathParam("id") String id) {
		intialize(fileName);
		List<DOMElementValueTrace> DOMElementValueTraces = new ArrayList<DOMElementValueTrace>();

		for (TraceObject to : episodeMap.get(id).getTrace().getTrace()) {
			if (to instanceof DOMElementValueTrace) {
				DOMElementValueTraces.add((DOMElementValueTrace) to);
			}
		}
		return DOMElementValueTraces;
	}

	@GET
	@Path("{fileName}/episodes/{id}/trace/XMLHttpRequestTrace")
	@Produces(MediaType.APPLICATION_JSON)
	public List<XMLHttpRequestTrace> getXMLHttpRequestTrace(
			@PathParam("fileName") String fileName, @PathParam("id") String id) {
		intialize(fileName);
		List<XMLHttpRequestTrace> XMLHttpRequestTraces = new ArrayList<XMLHttpRequestTrace>();

		for (TraceObject to : episodeMap.get(id).getTrace().getTrace()) {
			if (to instanceof XMLHttpRequestTrace) {
				XMLHttpRequestTraces.add((XMLHttpRequestTrace) to);
			}
		}
		return XMLHttpRequestTraces;
	}

	@GET
	@Path("{fileName}/episodes/{id}/trace/TimingTrace")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TimingTrace> getTimingTrace(@PathParam("fileName") String fileName,
			@PathParam("id") String id) {
		intialize(fileName);
		List<TimingTrace> TimingTraces = new ArrayList<TimingTrace>();

		for (TraceObject to : episodeMap.get(id).getTrace().getTrace()) {
			if (to instanceof TimingTrace) {
				TimingTraces.add((TimingTrace) to);
			}
		}
		return TimingTraces;
	}

	@GET
	@Path("{fileName}/episodes/{id}/trace/DOMEventTrace")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DOMEventTrace> getDOMEventTrace(@PathParam("fileName") String fileName,
			@PathParam("id") String id) {
		intialize(fileName);
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
	@Path("{fileName}/episodes/{id}/trace/functionTrace/FunctionCall")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FunctionCall> getFunctionCall(@PathParam("fileName") String fileName,
			@PathParam("id") String id) {
		intialize(fileName);
		List<FunctionCall> FunctionCalls = new ArrayList<FunctionCall>();

		for (TraceObject to : getFunctionTrace(fileName, id)) {
			if (to instanceof FunctionCall) {
				FunctionCalls.add((FunctionCall) to);
			}
		}
		return FunctionCalls;
	}

	@GET
	@Path("{fileName}/episodes/{id}/trace/functionTrace/FunctionEnter")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FunctionEnter> getFunctionEnter(@PathParam("fileName") String fileName,
			@PathParam("id") String id) {
		intialize(fileName);
		List<FunctionEnter> FunctionEnters = new ArrayList<FunctionEnter>();

		for (TraceObject to : getFunctionTrace(fileName, id)) {
			if (to instanceof FunctionEnter) {
				FunctionEnters.add((FunctionEnter) to);
			}
		}
		return FunctionEnters;
	}

	@GET
	@Path("{fileName}/episodes/{id}/trace/functionTrace/FunctionExit")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FunctionExit> getFunctionExit(@PathParam("fileName") String fileName,
			@PathParam("id") String id) {
		intialize(fileName);
		List<FunctionExit> FunctionExits = new ArrayList<FunctionExit>();

		for (TraceObject to : getFunctionTrace(fileName, id)) {
			if (to instanceof FunctionExit) {
				FunctionExits.add((FunctionExit) to);
			}
		}
		return FunctionExits;
	}

	@GET
	@Path("{fileName}/episodes/{id}/trace/functionTrace/FunctionReturnStatement")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FunctionReturnStatement> getFunctionReturnStatement(
			@PathParam("fileName") String fileName, @PathParam("id") String id) {
		intialize(fileName);
		List<FunctionReturnStatement> FunctionReturnStatements =
				new ArrayList<FunctionReturnStatement>();

		for (TraceObject to : getFunctionTrace(fileName, id)) {
			if (to instanceof FunctionReturnStatement) {
				FunctionReturnStatements.add((FunctionReturnStatement) to);
			}
		}
		return FunctionReturnStatements;
	}

	// /////////////////////////////////////////////////////////////////////////////////////

	// ///////////////////Resources to get information about timing traces.////////////////

	@GET
	@Path("{fileName}/episodes/{id}/trace/TimingTrace/TimeoutCallback")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TimeoutCallback> getTimeoutCallback(@PathParam("fileName") String fileName,
			@PathParam("id") String id) {
		intialize(fileName);
		List<TimeoutCallback> TimeoutCallbacks = new ArrayList<TimeoutCallback>();

		for (TraceObject to : getTimingTrace(fileName, id)) {
			if (to instanceof TimeoutCallback) {
				TimeoutCallbacks.add((TimeoutCallback) to);
			}
		}
		return TimeoutCallbacks;
	}

	@GET
	@Path("{fileName}/episodes/{id}/trace/TimingTrace/TimeoutSet")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TimeoutSet> getTimeoutSet(@PathParam("fileName") String fileName,
			@PathParam("id") String id) {
		intialize(fileName);
		List<TimeoutSet> TimeoutSets = new ArrayList<TimeoutSet>();

		for (TraceObject to : getTimingTrace(fileName, id)) {
			if (to instanceof TimeoutSet) {
				TimeoutSets.add((TimeoutSet) to);
			}
		}
		return TimeoutSets;
	}

	// /////////////////////////////////////////////////////////////////////////////////////

	// ///////////////////Resources to get information about XMLHTTPRequest traces.////////////////

	@GET
	@Path("{fileName}/episodes/{id}/trace/XMLHttpRequestTrace/XMLHttpRequestOpen")
	@Produces(MediaType.APPLICATION_JSON)
	public List<XMLHttpRequestOpen> getXMLHttpRequestOpen(@PathParam("fileName") String fileName,
			@PathParam("id") String id) {
		intialize(fileName);
		List<XMLHttpRequestOpen> XMLHttpRequestOpens = new ArrayList<XMLHttpRequestOpen>();

		for (TraceObject to : getXMLHttpRequestTrace(fileName, id)) {
			if (to instanceof XMLHttpRequestOpen) {
				XMLHttpRequestOpens.add((XMLHttpRequestOpen) to);
			}
		}
		return XMLHttpRequestOpens;
	}

	@GET
	@Path("{fileName}/episodes/{id}/trace/XMLHttpRequestTrace/XMLHttpRequestResponse")
	@Produces(MediaType.APPLICATION_JSON)
	public List<XMLHttpRequestResponse> getXMLHttpRequestResponse(
			@PathParam("fileName") String fileName, @PathParam("id") String id) {
		intialize(fileName);
		List<XMLHttpRequestResponse> XMLHttpRequestResponses =
				new ArrayList<XMLHttpRequestResponse>();

		for (TraceObject to : getXMLHttpRequestTrace(fileName, id)) {
			if (to instanceof XMLHttpRequestResponse) {
				XMLHttpRequestResponses.add((XMLHttpRequestResponse) to);
			}
		}
		return XMLHttpRequestResponses;
	}

	@GET
	@Path("{fileName}/episodes/{id}/trace/XMLHttpRequestTrace/XMLHttpRequestSend")
	@Produces(MediaType.APPLICATION_JSON)
	public List<XMLHttpRequestSend> getXMLHttpRequestSend(@PathParam("fileName") String fileName,
			@PathParam("id") String id) {
		intialize(fileName);
		List<XMLHttpRequestSend> XMLHttpRequestSends = new ArrayList<XMLHttpRequestSend>();

		for (TraceObject to : getXMLHttpRequestTrace(fileName, id)) {
			if (to instanceof XMLHttpRequestSend) {
				XMLHttpRequestSends.add((XMLHttpRequestSend) to);
			}
		}
		return XMLHttpRequestSends;
	}

	// /////////////////////////////////////////////////////////////////////////////////////

	@GET
	@Path("{fileName}/story/timingTraces")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TraceObject> getTimingTraces(@PathParam("fileName") String fileName) {
		intialize(fileName);
		return this.s1.getTimingTraces();
	}

	@GET
	@Path("{fileName}/story/domEventTraces")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TraceObject> getDomEventTraces(@PathParam("fileName") String fileName) {
		intialize(fileName);
		return this.s1.getDomEventTraces();
	}

	@GET
	@Path("{fileName}/story/XHRTraces")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TraceObject> getXHRTraces(@PathParam("fileName") String fileName) {
		intialize(fileName);
		return this.s1.getXhrTraces();
	}

	@GET
	@Path("{fileName}/story/functionTraces")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TraceObject> getFunctionTraces(@PathParam("fileName") String fileName) {
		intialize(fileName);
		return this.s1.getFunctionTraces();
	}

	// need to find which episodes have timeouts, then need to find corresponding callbacks
	@GET
	@Path("{fileName}/story/causalLinks")
	@Produces(MediaType.APPLICATION_JSON)
	public List<causalLinks> episodesContainTimeouts(@PathParam("fileName") String fileName) {

		intialize(fileName);
		List<causalLinks> causalLinkss = new ArrayList<causalLinks>();
		boolean foundFlag = false;
		
		// Add causal edges between episodes
		for (int i = 0; i< this.s1.getEpisodes().size(); i++) {
			Episode currentEpisode = this.s1.getEpisodes().get(i);

			for (TraceObject to : currentEpisode.getTrace().getTrace()) {
				// Iterate through each TraceObject in the Episode
				if (to.getClass().toString().contains("TimeoutCallback")
						|| to.getClass().toString().contains("XMLHttpRequestResponse")) {
					
					// Need to look for origin of Timeout or XMLHttpRequest in other Episodes
					for (int j = 0; j < i; j++) {

						Episode otherEpisode = this.s1.getEpisodes().get(j);


						for (TraceObject to2 : otherEpisode.getTrace().getTrace()) {
							
							if (to.getClass().toString().contains("XMLHttpRequestResponse")
									&& to2.getClass().toString().contains("XMLHttpRequest")
									&& to2.getId() == to.getId()){
								// Found source of XMLHttpRequest
								causalLinkss.add(new causalLinks(i, j));
								foundFlag = true;
								break;
							} else if (to2.getClass().toString().contains("TimeoutSet")
									&& to.getClass().toString().contains("TimeoutCallback")
									&& ((TimeoutCallback)to).getTimeoutId() == ((TimeoutSet)to2).getTimeoutId()){
								// Found source of TimingEvent
								causalLinkss.add(new causalLinks(i, j));
								foundFlag = true;
								break;
							}
						}
						// If source of this timeout callback/XHR response was found, move onto next trace object in episode with callback
						if (foundFlag) {
							foundFlag = false;
							break;
						}

					}
				} // Otherwise no need to add causal link for TraceObject
			}
		}
		
		return causalLinkss;
	}


	@GET
	@Path("{fileName}/story/sequenceDiagram2")
	@Produces(MediaType.APPLICATION_JSON)
	public String getsequenceDiagram2(@PathParam("fileName") String fileName) {
		String output = null;
		String temp = fileName.replace("story", "allEpisodes");
		String temp2 = temp.concat(".js");
		try {
			output =
					new Scanner(new File(
							"clematis-output/ftrace/sequence_diagrams/" + temp2))
			.useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

}
