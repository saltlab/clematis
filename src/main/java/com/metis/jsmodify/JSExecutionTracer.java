/*
    Automatic JavaScript Invariants is a plugin for Crawljax that can be
    used to derive JavaScript invariants automatically and use them for
    regressions testing.
    Copyright (C) 2010  crawljax.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package com.metis.jsmodify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

//import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.crawljax.util.Helper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.metis.core.episode.Episode;
import com.metis.core.episode.Story;
//import com.metis.core.trace.Trace;
import com.metis.core.trace.TraceObject;
import com.metis.visual.EpisodeGraph;
import com.metis.visual.JSUml2Story;
import com.metis.visual.SequenceDiagram;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

/**
 * Reads an instrumentation array from the webbrowser and
 * saves the contents in a JSON trace file.
 * 
 * @author Frank Groeneveld
 * @version $Id: JSExecutionTracer.java 6162 2009-12-16 13:56:21Z frank $
 */
public class JSExecutionTracer {

	private static final int ONE_SEC = 1000;

	private static String outputFolder;
	private static String traceFilename;

	private static JSONArray points = new JSONArray();

	private static final Logger LOGGER = Logger
			.getLogger(JSExecutionTracer.class.getName());

	public static final String FUNCTIONTRACEDIRECTORY = "functiontrace/";

	private static PrintStream output;

	//	private Trace trace;
	private Story story;
	//	private ArrayList<TraceObject> sortedTraceList;
	//	private ArrayList<Episode> episodeList;

	/**
	 * @param filename
	 * 
	 */
	public JSExecutionTracer(String filename) {
		traceFilename = filename;
	}

	/**
	 * Initialize the plugin and create folders if needed.
	 * 
	 * @param browser
	 *            The browser.
	 */
	public void preCrawling() {
		try {
			Helper.directoryCheck(getOutputFolder());
			output = new PrintStream(getOutputFolder() + getFilename());

			// Add opening bracket around whole trace
			PrintStream oldOut = System.out;
			System.setOut(output);
			System.out.println("{");
			System.setOut(oldOut);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the JavaScript instrumentation array from the webbrowser and
	 * writes its contents in Daikon format to a file.
	 * 
	 * @param session
	 *            The crawling session.
	 * @param candidateElements
	 *            The candidate clickable elements.
	 */

	public void preStateCrawling() {

		String filename = getOutputFolder() + FUNCTIONTRACEDIRECTORY
				+ "jstrace-";

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		filename += dateFormat.format(date) + ".dtrace";

		try {

			LOGGER.info("Reading execution trace");

			LOGGER.info("Parsing JavaScript execution trace");

			// session.getBrowser().executeJavaScript("sendReally();");
			Thread.sleep(ONE_SEC);

			LOGGER.info("Saved execution trace as " + filename);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a list with all trace files in the executiontracedirectory.
	 * 
	 * @return The list.
	 */
	public List<String> allTraceFiles() {
		ArrayList<String> result = new ArrayList<String>();

		/* find all trace files in the trace directory */
		File dir = new File(getOutputFolder() + FUNCTIONTRACEDIRECTORY);

		String[] files = dir.list();
		if (files == null) {
			return result;
		}
		for (String file : files) {
			if (file.endsWith(".dtrace")) {
				result.add(getOutputFolder() + FUNCTIONTRACEDIRECTORY + file);
			}
		}
		return result;
	}

	public void postCrawling() {
		try {
			// Add closing bracket
			PrintStream oldOut = System.out;
			System.setOut(output);
			System.out.println(" ");
			System.out.println("}");
			System.setOut(oldOut);

			/* close the output file */
			output.close();

			extraxtTraceObjects();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method parses the JSON file containing the trace objects and
	 * extracts the objects
	 */
	private void extraxtTraceObjects() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			// Register the module that serializes the Guava Multimap
			mapper.registerModule(new GuavaModule());

			Multimap<String, TraceObject> traceMap = mapper
					.<Multimap<String, TraceObject>> readValue(
							new File("metis-output/ftrace/function.trace"),
							new TypeReference<TreeMultimap<String, TraceObject>>() {
							});

			Collection<TraceObject> timingTraces = traceMap.get("TimingTrace");
			Collection<TraceObject> domEventTraces = traceMap
					.get("DOMEventTrace");
			Collection<TraceObject> XHRTraces = traceMap.get("XHRTrace");
			Collection<TraceObject> functionTraces = traceMap
					.get("FunctionTrace");

			story = new Story(domEventTraces, functionTraces, timingTraces, XHRTraces);
			story.setOrderedTraceList(sortTraceObjects());
			story.setEpisodes(buildEpisodes());

			System.out.println("# of trace objects: " + story.getOrderedTraceList().size());
			System.out.println("# of episodes: " + story.getEpisodes().size());

			// JavaScript episodes for JSUML2
			Helper.directoryCheck(outputFolder+ "/sequence_diagrams/");
			PrintStream JSepisodes = new PrintStream(outputFolder+"/sequence_diagrams/allEpisodes.js");
			
			for (Episode e:story.getEpisodes()) {
				// Create pic files for each episode's sequence diagram
				designSequenceDiagram(e, JSepisodes);
			}

			// Once all episodes have been saved to JS file, close
			JSepisodes.close();
			
			// Create graph containing all episodes with embedded sequence diagrams
			EpisodeGraph eg = new EpisodeGraph(getOutputFolder(), story.getEpisodes());
			eg.createGraph();
			


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void designSequenceDiagram(Episode e, PrintStream jSepisodes) {
		// Given an episode (source, trace included), a pic file will be created
		// in metis-output/ftrace/sequence_diagrams

		/*SequenceDiagram sd = new SequenceDiagram(getOutputFolder(), e);
		sd.createComponents();
		sd.createMessages();
		sd.close();*/

		try {
			JSUml2Story jsu2story = new JSUml2Story(jSepisodes, e);
			jsu2story.createComponents();
			jsu2story.createMessages();
			jsu2story.close();

		} catch (FileNotFoundException e1) {
			System.out.println("Error initializing print stream for allEpisodes.js");
			e1.printStackTrace();
		} catch (IOException e2) {
			System.out.println("IOException while printing episodes to JS.");
			e2.printStackTrace();
		}


	}

	/**
	 * This method sorts all four groups of trace objects into one ordered list of trace objects
	 */
	private ArrayList<TraceObject> sortTraceObjects() {
		ArrayList<TraceObject> sortedTrace = new ArrayList<TraceObject>();

		ArrayList<Collection<TraceObject>> allCollections = new ArrayList<Collection<TraceObject>>();

		if (story.getDomEventTraces().size() > 0)
			allCollections.add(story.getDomEventTraces());
		if (story.getFunctionTraces().size() > 0)
			allCollections.add(story.getFunctionTraces());
		if (story.getTimingTraces().size() > 0)
			allCollections.add(story.getTimingTraces());
		if (story.getXhrTraces().size() > 0)
			allCollections.add(story.getXhrTraces());

		if (allCollections.size() == 0) {
			System.out.println("No log");
			return null;
		}

		ArrayList<Integer> currentIndexInCollection = new ArrayList<Integer>();
		for (int i = 0; i < allCollections.size(); i ++)
			currentIndexInCollection.add(0);

		while (true) {
			int currentMinArray = 0;

			for (int i = 0; i < allCollections.size(); i ++) {
				TraceObject traceObj = Iterables.get(allCollections.get(i), currentIndexInCollection.get(i));
				TraceObject currObj = Iterables.get(allCollections.get(currentMinArray), currentIndexInCollection.get(currentMinArray));
				if (traceObj.getCounter() < currObj.getCounter())
					currentMinArray = i;
			}

			sortedTrace.add(Iterables.get(allCollections.get(currentMinArray), currentIndexInCollection.get(currentMinArray)));

			currentIndexInCollection.set(currentMinArray, currentIndexInCollection.get(currentMinArray) + 1);
			if (currentIndexInCollection.get(currentMinArray) >= allCollections.get(currentMinArray).size()) {
				allCollections.remove(currentMinArray);
				currentIndexInCollection.remove(currentMinArray);
				if (allCollections.size() == 0)
					break;
			}
		}

		return sortedTrace;
	}

	private ArrayList<Episode> buildEpisodes() {
		ArrayList<Episode> episodes = new ArrayList<Episode>();
		int i, j, previousEpisodeEnd = 0;

		for (i = 0; i < story.getOrderedTraceList().size(); i++) {
			// Iterate through all TraceObjects and identify episodes
			TraceObject sourceTraceObj = story.getOrderedTraceList().get(i);

			if (sourceTraceObj.isEpisodeSource() ){
				//	&& !(sourceTraceObj.getClass().toString().contains("TimeoutCallback"))
				//	&& !(sourceTraceObj.getClass().toString().contains("XMLHttpRequestResponse"))) {
				// Simple case
				// If the TraceObject is the beginning of an episode
				// i.e. DOMEvent, XHRRequest, create an episode
				Episode episode = new Episode(sourceTraceObj);

				for (j = i+1; j < story.getOrderedTraceList().size(); j++ ) {
					// Go through the succeeding TraceObjects looking for the
					// end of the episode (as indicated by another episode starter
					// (DOMEvent, TimingEvent, etc.)

					TraceObject currentTraceObj = story.getOrderedTraceList().get(j);

					if (Math.abs(currentTraceObj.getTimeStamp() - sourceTraceObj.getTimeStamp()) < 80) {
						// If the succeeding TraceObject is not the beginning of
						// another episode, add it to the current episode
						episode.addToTrace(currentTraceObj);
					} else {
						// End of current episode, break out of inner-loop
						break;
					}
				}
				// Add the newly created episode to the list of episodes
				episodes.add(episode);
				// Update i to the end of the newly created episode
				i=j-1;
				previousEpisodeEnd = i;

			} else if (sourceTraceObj.getClass().toString().contains("TimeoutCallback") 
					|| sourceTraceObj.getClass().toString().contains("XMLHttpRequestResponse")) {
				// Special case
				// TimeoutCallback is triggered after the callback function
				// of a timeout has completed. Therefore, have to search backwards in
				// Episode.
				// e.g. FunctionEnter -> FunctionEnter -> FuntionExit -> FunctionExit -> TimeoutCallback
				// As opposed to DOMEvent:
				// DOMEvent -> FunctionEnter -> FunctionEnter -> FuntionExit -> FunctionExit

				Episode episode = new Episode(sourceTraceObj);
				for (j = previousEpisodeEnd + 1; j<i; j++) {
					// Iterate from end of last episode to this TimeoutCallback

					TraceObject currentTraceObj = story.getOrderedTraceList().get(j);
					episode.addToTrace(currentTraceObj);
				}

				// Add the newly created episode to the list of episodes
				episodes.add(episode);
				previousEpisodeEnd = i;

			}

		}
		return episodes;
	}
	/**
	 * @return Name of the file.
	 */
	public static String getFilename() {
		return traceFilename;
	}

	public static String getOutputFolder() {
		return Helper.addFolderSlashIfNeeded(outputFolder);
	}

	public void setOutputFolder(String absolutePath) {
		outputFolder = absolutePath;
	}

	/**
	 * Dirty way to save program points from the proxy request threads. TODO:
	 * Frank, find cleaner way.
	 * 
	 * @param string
	 *            The JSON-text to save.
	 */
	public static void addPoint(String string) {
		JSONArray buffer = null;
		JSONObject targetAttributes = null;
		JSONObject targetElement = null;
		String JSONLabel = new String();

		try {
			/* save the current System.out for later usage */
			PrintStream oldOut = System.out;
			/* redirect it to the file */
			System.setOut(output);

			buffer = new JSONArray(string);
			for (int i = 0; i < buffer.length(); i++) {

				if (points.length() > 0) {
					// Add comma after previous trace object
					System.out.println(",");
				}

				points.put(buffer.getJSONObject(i));

				if (buffer.getJSONObject(i).has("args") && ((String) buffer.getJSONObject(i).get("messageType")).contains("FUNCTION_ENTER")) {
					try {
						JSONObject args = (JSONObject) buffer.getJSONObject(i).get("args");
						String newValue = args.toString();				
						buffer.getJSONObject(i).remove("args");
						buffer.getJSONObject(i).put("args", newValue);
					} catch (JSONException jse) {
						// argument is not a JSON object
						continue;
					}
				}
				if (buffer.getJSONObject(i).has("returnValue") && 
						!buffer.getJSONObject(i).get("returnValue").getClass().toString().contains("Null")) {
					try {
						JSONObject rv = (JSONObject) buffer.getJSONObject(i).get("returnValue");
						String newValue = rv.toString();				
						buffer.getJSONObject(i).remove("returnValue");
						buffer.getJSONObject(i).put("returnValue", newValue);
					} catch (JSONException jse) {
						// argument is not a JSON object
						continue;
					}
				}
				if (buffer.getJSONObject(i).has("targetElement")) {
					JSONArray extractedArray = new JSONArray(buffer
							.getJSONObject(i).get("targetElement").toString());
					try {
						targetAttributes = extractedArray.getJSONObject(1);
						String targetType = extractedArray.get(0).toString();

						targetElement = new JSONObject("{\"elementType\":\""
								+ targetType + "\",\"attributes\":"
								+ targetAttributes.toString() + "}");

					} catch (Exception e) {
						// targetElement is not usual DOM element
						// E.g. DOMContentLoaded
						if (buffer.getJSONObject(i).has("eventType")
								&& buffer.getJSONObject(i).get("eventType")
								.toString().contains("ContentLoaded")) {
							targetElement = new JSONObject(
									"{\"elementType\":\"DOCUMENT\",\"attributes\":\"-\"}");
						} else {
							targetElement = new JSONObject(
									"{\"elementType\":\"UNKNOWN\",\"attributes\":\"-\"}");
						}
					}
					buffer.getJSONObject(i).remove("targetElement");
					buffer.getJSONObject(i).put("targetElement", targetElement.toString());
				}

				// Insert @class key for Jackson mapping
				if (buffer.getJSONObject(i).has("messageType")) {
					String mType = buffer.getJSONObject(i).get("messageType")
							.toString();

					// Maybe better to change mType to ENUM and use switch
					// instead of 'if's
					if (mType.contains("FUNCTION_CALL")) {
						buffer.getJSONObject(i).put("@class",
								"com.metis.core.trace.FunctionCall");
						JSONLabel = "\"FunctionTrace\":";
					} else if (mType.contains("FUNCTION_ENTER")) {
						buffer.getJSONObject(i).put("@class",
								"com.metis.core.trace.FunctionEnter");
						JSONLabel = "\"FunctionTrace\":";
					} else if (mType.contains("FUNCTION_EXIT")) {
						buffer.getJSONObject(i).put("@class",
								"com.metis.core.trace.FunctionExit");
						JSONLabel = "\"FunctionTrace\":";
					} else if (mType.contains("RETURN_STATEMENT")) {
						buffer.getJSONObject(i).put("@class",
								"com.metis.core.trace.FunctionReturnStatement");
						JSONLabel = "\"FunctionTrace\":";
					} else if (mType.contains("DOM_EVENT")) {
						buffer.getJSONObject(i).put("@class",
								"com.metis.core.trace.DOMEventTrace");
						JSONLabel = "\"DOMEventTrace\":";
					} else if (mType.contains("TIMEOUT_SET")) {
						buffer.getJSONObject(i).put("@class",
								"com.metis.core.trace.TimeoutSet");
						JSONLabel = "\"TimingTrace\":";
					} else if (mType.contains("TIMEOUT_CALLBACK")) {
						buffer.getJSONObject(i).put("@class",
								"com.metis.core.trace.TimeoutCallback");
						JSONLabel = "\"TimingTrace\":";
					} else if (mType.contains("XHR_OPEN")) {
						buffer.getJSONObject(i).put("@class",
								"com.metis.core.trace.XMLHttpRequestOpen");
						JSONLabel = "\"XHRTrace\":";
					} else if (mType.contains("XHR_SEND")) {
						buffer.getJSONObject(i).put("@class",
								"com.metis.core.trace.XMLHttpRequestSend");
						JSONLabel = "\"XHRTrace\":";
					} else if (mType.contains("XHR_RESPONSE")) {
						buffer.getJSONObject(i).put("@class",
								"com.metis.core.trace.XMLHttpRequestResponse");
						JSONLabel = "\"XHRTrace\":";
					}
					// messageType obsolete
					buffer.getJSONObject(i).remove("messageType");
				}

				System.out.print(JSONLabel + "["
						+ buffer.getJSONObject(i).toString(2) + "]");
			}

			/* Restore the old system.out */
			System.setOut(oldOut);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
