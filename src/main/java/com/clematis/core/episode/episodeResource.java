package com.clematis.core.episode;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.clematis.core.ClematisSession;
import com.clematis.core.SimpleExample;
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
import com.clematis.database.MongoInterface;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Path("/clematis-api")
//@Produces({ "application/json" })
public class episodeResource {

	private Story s1;
	private ObjectMapper mapper = new ObjectMapper();
	private Map<String, Episode> episodeMap = new HashMap<String, Episode>(200);
	
	
	private File f2 = null;
	private long lastModified = -1;
	
	private final String USER_AGENT = "Mozilla/5.0";
	
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
	
	public String userLoggedIn(){
		Subject currentUser = SecurityUtils.getSubject();
		String userName = (String) currentUser.getPrincipal();
		
		 // let's login the current user so we can check against roles and permissions:
        if (!currentUser.isAuthenticated()) {
        	//System.out.println("Initialize: user not authenticated");
        	userName = "guest";
        }      
        else{
        	//System.out.println("Initialize: user authenticated - " + userName);
        }
        return userName;
	}
	
	//TODO TODO TODO
	public String intialize(Double sessionNum) {
		int i;
		
		String userName = userLoggedIn();
		
		if(userName =="guest"){
        	sessionNum = MongoInterface.getLastSessionNumber(userName);
        }
		
		System.out.println("Initialize: session number " + sessionNum);
  
		configureObjectMapper();
		try {					
			this.s1 = mapper.readValue( MongoInterface.getStoryAsString(userName, sessionNum), Story.class);
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

		for (i = 0; i < s1.getEpisodes().size(); i++) {
			episodeMap.put(Integer.toString(i), s1.getEpisodes().get(i));
		}

		return "successfully intialized story";

	}
	
	public String unusedIntialize() {
		int i;
		
		/*Subject currentUser = SecurityUtils.getSubject();
		String userName = (String) currentUser.getPrincipal();
		
		 // let's login the current user so we can check against roles and permissions:
        if (!currentUser.isAuthenticated()) {
        	System.out.println("Initialize: user not authenticated");
        	userName = "firstUser";
        }
        else{
        	System.out.println("Initialize: user authenticated - " + userName);
        }*/
		
		
		String userName = userLoggedIn();
		
		//get LAST session FOR NOW	
		Double sessionNum = MongoInterface.getLastSessionNumber(userName);
		//System.out.println("Session Num Initialize: " + sessionNum);
	    
		configureObjectMapper();
		try {
			/*
 +            f2 = new File("captured_stories/" + fileName + ".json");
 +
 +            // Used cached/saved story, no need to reinitialize
 +            if (f2.lastModified() == lastModified) {
 +                return "successfully intialized story";
 +            }
 +
 +            this.s1 = mapper.readValue(f2,
 +                    Story.class);
 +
 +            /*  this.s2 = mapper.readValue(new File("story2.json"),
 +	                    Story.class)
             lastModified = f2.lastModified();
			 */
									
			this.s1 = mapper.readValue( MongoInterface.getStoryAsString(userName, sessionNum), Story.class);
			//System.out.println("SUCESSFUL STORY RETREIVAL?! "+ MongoInterface.getStoryAsString(userName, sessionNum));
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

		for (i = 0; i < s1.getEpisodes().size(); i++) {
			episodeMap.put(Integer.toString(i), s1.getEpisodes().get(i));
		}

		return "successfully intialized story";

	}
	
	//rest/clematis-api/allEpisodes/story/sequenceDiagram2
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
		unusedIntialize();
		return this.s1.getEpisodes().size();
	}

	//CHANGED URL --- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	@GET
	@Path("{fileName}/episodes/{sessionID}/test")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Episode> getEpisodes(@PathParam("fileName") String fileName, @PathParam("sessionID") Double sessionID) {
		System.out.println("REST API session ID :" + sessionID);
		intialize(sessionID);
		return this.s1.getEpisodes();
	}
	
	@GET
	@Path("/test/{sessionID}")
	@Produces(MediaType.APPLICATION_JSON)
	public String test(@PathParam("sessionID") String sessionID){
		System.out.println("TEST session ID : " + sessionID);
		return "sure";
	}

	@GET
	@Path("{fileName}/episodes/bookmarked")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getBookmarks(@PathParam("fileName") String fileName) {
		unusedIntialize();
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
		unusedIntialize();
		try {
			return mapper.writeValueAsString(this.s1.getEpisodes());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@GET
	@Path("{fileName}/{sessionID}/episodes/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Episode getEpisode(@PathParam("fileName") String fileName, @PathParam("id") String id, @PathParam("sessionID") Double sessionID) {
		intialize(sessionID);
		return episodeMap.get(id);
	}

	@GET
	@Path("{fileName}/{sessionID}/episodes/{id}/DOM")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEpisodeDom(@PathParam("fileName") String fileName, @PathParam("id") String id, @PathParam("sessionID") Double sessionID)
	{
		intialize(sessionID);

		if (episodeMap.get(id).getDom() == null) {
			return "DOM is NULL!";
		}
		else {
			return episodeMap.get(id).getDom();
		}
	}

	@GET
	@Path("{fileName}/{sessionID}/episodes/{id}/source")
	@Produces(MediaType.APPLICATION_JSON)
	public TraceObject getEpisodeSource(@PathParam("fileName") String fileName,
			@PathParam("id") String id, @PathParam("sessionID") Double sessionID) {
		intialize(sessionID);
		return episodeMap.get(id).getSource();
	}

	@GET
	@Path("{fileName}/{sessionID}/episodes/{id}/trace")
	@Produces(MediaType.APPLICATION_JSON)
	public EpisodeTrace getEpisodeTrace(@PathParam("fileName") String fileName,
			@PathParam("id") String id, @PathParam("sessionID") Double sessionID) {
		intialize(sessionID);
		
		return episodeMap.get(id).getTrace();
	}

	@GET
	@Path("{fileName}/episodes/{id}/trace/functionTrace")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FunctionTrace> getFunctionTrace(@PathParam("fileName") String fileName,
			@PathParam("id") String id) {
		unusedIntialize();
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
		unusedIntialize();
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
		unusedIntialize();
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
		unusedIntialize();
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
		unusedIntialize();
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
		unusedIntialize();
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
		unusedIntialize();
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
		unusedIntialize();
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
		unusedIntialize();
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
		unusedIntialize();
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
		unusedIntialize();
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
		unusedIntialize();
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
		unusedIntialize();
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
		unusedIntialize();
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
		unusedIntialize();
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
		unusedIntialize();
		return this.s1.getTimingTraces();
	}

	@GET
	@Path("{fileName}/story/domEventTraces")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TraceObject> getDomEventTraces(@PathParam("fileName") String fileName) {
		unusedIntialize();
		return this.s1.getDomEventTraces();
	}

	@GET
	@Path("{fileName}/story/XHRTraces")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TraceObject> getXHRTraces(@PathParam("fileName") String fileName) {
		unusedIntialize();
		return this.s1.getXhrTraces();
	}

	@GET
	@Path("{fileName}/story/functionTraces")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TraceObject> getFunctionTraces(@PathParam("fileName") String fileName) {
		unusedIntialize();
		return this.s1.getFunctionTraces();
	}

	// need to find which episodes have timeouts, then need to find corresponding callbacks
	@GET
	@Path("{fileName}/{sessionID}/story/causalLinks")
	@Produces(MediaType.APPLICATION_JSON)
	public List<causalLinks> episodesContainTimeouts(@PathParam("fileName") String fileName, @PathParam("sessionID") Double sessionID) {

		intialize(sessionID);
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
	@Path("{fileName}/{sessionID}/story/sequenceDiagram2/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getsequenceDiagram2(@PathParam("fileName") String fileName,@PathParam("sessionID") Double sessionID) {
		String output = null;
		String temp = fileName.replace("story", "allEpisodes");
		String temp2 = temp.concat(".js");	
		
		//String userName = "firstUser";
		Subject currentUser = SecurityUtils.getSubject();
		String userName = (String) currentUser.getPrincipal();
		
		if(userName =="guest"){
        	sessionID = MongoInterface.getLastSessionNumber(userName);
        }
		
		System.out.println("REST SESSION ID : " + sessionID);
		
		//try {
			//output = new Scanner(new File("clematis-output/ftrace/sequence_diagrams/" + temp2)).useDelimiter("\\Z").next();
			output = MongoInterface.getAllEpisodesAsString(userName, sessionID);
		//} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}
		return output;
			
	}

	//ACCOUNT PAGE INFORMATION
	@GET
	@Path("/sessions")
	@Produces(MediaType.APPLICATION_JSON)
	public List<List<String>> getSessions() {
		System.out.println("Getting session info");
		String userName = userLoggedIn();
		List<List<String>> sessionList = new ArrayList<List<String>>();
		//create a json array of all the sessions, their information, and a preview of the session
		Double lastSess = MongoInterface.getLastSessionNumber(userName);
		System.out.println("last session: " + lastSess);
		
		for(Double i = 1.0; i <= lastSess; i+=1){
			//add image preview here
			
			sessionList.add(MongoInterface.getSessInfo(userName, i));
		}
		
		return sessionList;

	}
	
	@POST
	@Path("/account/create")
	@Produces(MediaType.APPLICATION_JSON)
	public String createAccount(@Context HttpServletRequest request) throws IOException {
		System.out.println("USER SIGN UP");
		
		ServletInputStream in = request.getInputStream();
		List<String> userInfo = getUserInfo(in);
		
		MongoInterface.newUser(userInfo.get(0), userInfo.get(1));
		
		return "okay";
	}
	
	@GET
	@Path("/redirect")
	@Produces("text/plain")
	public String check(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException{
		System.out.println("redirect");
	
		/*String[] params = request.getParameterValues("url");
		for (int i=0; i<params.length ; i++){
			System.out.println(params[i]);
		}
		System.out.println("");*/
		
		String queryString = "";
		String url = "";
		
		@SuppressWarnings("unchecked")
		Enumeration<String> params = request.getParameterNames();
    	while(params.hasMoreElements()){
    		String paramName = (String) params.nextElement();
    		String[] paramVal = request.getParameterValues(paramName);
    		System.out.println("Param: " + paramName);
    		for (int i=0; i<paramVal.length; i++){
    			System.out.println(" "+i+": " + paramVal[i]);
    		}
    		System.out.println("");
    		
    		if(paramName.equals("url")){
    			url = paramVal[0];
    		}else{
    			queryString = queryString + paramName + "=" + paramVal[0] +"&";
    		}
    	}
    	queryString = queryString + "redir=no";
		
    	System.out.println(queryString);
    	//response.sendRedirect(url + "?" + queryString);
    	String res = null;
    	try {
			res = sendGet(url+"?"+queryString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return res;
	}
	
	
	@POST
	@Path("/startSessionPOST")
	@Produces(MediaType.APPLICATION_JSON)
	public String startNewSessionPOST(@Context HttpServletRequest request) throws IOException{
		//USER LOGGED IN? 
		Subject currentUser = SecurityUtils.getSubject();
		String user = (String) currentUser.getPrincipal();
		System.out.println("start session");
		
		 // let's login the current user so we can check against roles and permissions:
        if (!currentUser.isAuthenticated()) {
           System.out.println( "current user not authenticated - guest user");
           user = "guest";
           UsernamePasswordToken token = new UsernamePasswordToken(user, "guest");
           //this is all you have to do to support 'remember me' (no config - built in!):
           token.setRememberMe(true);
           currentUser.login(token);
        }
        else{
        	System.out.println("USER:" + user);
        	MongoInterface.checkUser(user);
        }
		
		ServletInputStream in = request.getInputStream();
		String newUrl = splitURL(in);
		
		String ip = request.getRemoteAddr();
		System.out.println("ip:" + ip);
		
		//get user info
		/*HttpSession userSession = request.getSession();
		userSession.setAttribute("userName", user);
		String userName = (String) userSession.getAttribute("userName");
		System.out.println("User Name: " + userName );*/
		
		//Double sessionNum = MongoInterface.newSessionDocument(userName);
		Double sessionNum = MongoInterface.newSessionDocument(user, newUrl);

		//SimpleExample session = new SimpleExample(ip, userName, sessionNum);
		SimpleExample session = new SimpleExample(ip, user, sessionNum);

		try{
			session.checkURL(newUrl);
		} catch (IllegalArgumentException e){
			return "Invalid URL: Please enter a url that begins with \"www.\" or \"http://\""; 
		}
		
		Thread t = new Thread(new ClematisSession(newUrl, session));
		t.start();

		return "session started"; 
	}
	
	public String processInput (ServletInputStream in) throws IOException{
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer, "UTF-8");
		String theString = writer.toString();
		return theString;
	}
	
	public String splitURL(ServletInputStream in) throws IOException{
		
		String input = processInput(in);
		
		System.out.println("INPUT: " + input );
		String[] parts = input.split("\":\"");
		String[] urlsplit = parts[1].split("\"");
		System.out.println(urlsplit[0]);
		String newUrl = urlsplit[0];
		
		return newUrl;
	}
	
	public List<String> getUserInfo(ServletInputStream in) throws IOException{
		
		String data = processInput(in);
		System.out.println(data);
		
		String[] parts = data.split("&");
		String[] user = parts[0].split("=");
		String username = user[1];
		String[] pass = parts[1].split("=");
		String password = pass[1];
		
		System.out.println("username: " + username + " password: "  + pass[1]);
		
		List<String> userInfo = new ArrayList<String>();
		userInfo.add(username);
		userInfo.add(password);
		
		return userInfo;
	}
	
	// HTTP GET request
		private String sendGet(String url) throws Exception {
	 
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	 
			// optional default is GET
			con.setRequestMethod("GET");
	 
			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
	 
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
	 
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String responseString = response.toString();
			//print result
			//System.out.println(responseString);
	 
			return responseString;
		}
	 
		// HTTP POST request
		private void sendPost(String url, String urlParameters) throws Exception {
	 
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	 
			//add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	 	 
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
	 
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);
	 
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	 
			//print result
			System.out.println(response.toString());
	 
		}
	

}

