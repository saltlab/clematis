package com.clematis.visual;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import com.crawljax.util.Helper;
import com.clematis.core.episode.Episode;
import com.clematis.core.trace.DOMEventTrace;
import com.clematis.core.trace.FunctionCall;
import com.clematis.core.trace.FunctionEnter;
import com.clematis.core.trace.TimeoutSet;
import com.clematis.core.trace.TimingTrace;
import com.clematis.core.trace.TraceObject;
import com.clematis.core.trace.XMLHttpRequestOpen;
import com.clematis.core.trace.XMLHttpRequestResponse;
import com.clematis.core.trace.XMLHttpRequestSend;
import com.clematis.core.trace.XMLHttpRequestTrace;

public class SequenceDiagram {

	static ArrayList<String> functionHeirarchy = new ArrayList<String>();
	static PrintStream output = null;
	PrintStream oldOut = null;

	// Define all the objects for the sequence diagram
	static ArrayList<TraceObject> functionTraceObjects;
	static ArrayList<String> components = null;
	static ArrayList<String> comments = null;
	static TraceObject episodeSource = null;
	static int numOfMessages = 0;

	public SequenceDiagram (String outputFolder, Episode e) {
		// Constructor
		try {
			// Create pic file fore sequence diagram description
			Helper.directoryCheck(outputFolder+ "sequence_diagrams/");
			output = new PrintStream(outputFolder+"sequence_diagrams/"+e.getSource().getCounter()+".pic");
			episodeSource = e.getSource();
			components = new ArrayList<String>();
			comments = new ArrayList<String>();

			// Print initializing lines
			oldOut = System.out;
			System.setOut(output);
			System.out.println(".PS");
			System.out.println("copy \"sequence.pic\";");
			System.out.println("");
			System.setOut(oldOut);

			functionTraceObjects = e.getTrace().getTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public void createComponents() {
		System.setOut(output);

		System.out.println("# Define the objects");
		functionTraceObjects.add(0, episodeSource);

		for (TraceObject to: functionTraceObjects) {

			if (!to.getClass().toString().contains("FunctionEnter") &&
					!to.getClass().toString().contains("XMLHttpRequest") &&
					!to.getClass().toString().contains("Timeout") &&
					!to.getClass().toString().contains("DOMEventTrace")){
				// Only want to create components for a subset of TraceObjects
				continue;
			} else if (components.contains(getDiagramIdentifier(to))) {
				// Component already created for TraceObject to
				continue;
			} else {
				components.add(getDiagramIdentifier(to));
			}

			if (to.getClass().toString().contains("FunctionEnter")) {
				// Create components in the sequence diagram for developer-defined functions
				FunctionEnter feto = (FunctionEnter) to;
				System.out.println("object("+getDiagramIdentifier(to)+
						",\""+feto.getTargetFunction()+"("+feto.getArgsLabels()+")\");");

			} else if (to.getClass().toString().contains("XMLHttpRequest")) {
				// Create actor for XMLHttpRequests
				//XMLHttpRequestTrace xhtto = (XMLHttpRequestTrace) to;
				//System.out.println("actor("+getDiagramIdentifier(to)+",\""+xhtto.getId()+":XMLHttpRequest\");");
				System.out.println("actor("+getDiagramIdentifier(to)+",\"XMLHttpRequest\");");

			} else if (to.getClass().toString().contains("Timeout")) {
				// Create actors for child timing events, that is, timing events that spawned as a result of the origin event
				//TimingTrace ttto = (TimingTrace) to;
				//System.out.println("actor("+getDiagramIdentifier(to)+",\""+ttto.getId()+":TimingEvent\");");
				System.out.println("actor("+getDiagramIdentifier(to)+",\"TimingEvent\");");

			} else if (to.getClass().toString().contains("DOMEventTrace")) {
				// Create actors for child DOM events
				System.out.println("actor("+getDiagramIdentifier(to)+",\"DOMEvent\");");
			}
		}
		push(components.get(0));

		for (TraceObject to2: functionTraceObjects) {
			// Iterate through again to add comment boxes with additional info
			// i.e. line numbers, xhr ids, etc.
			// Must be done after all components have been declared (umlgraph bug?)

			if (comments.contains(getDiagramIdentifier(to2))) {
				// Comment box already created for component
				continue;
			} else {
				comments.add(getDiagramIdentifier(to2));
			}

			if (to2.getClass().toString().contains("FunctionEnter")) {
				FunctionEnter feto = (FunctionEnter) to2;
				addFunctionInfo(feto.getTargetFunction().toUpperCase()+feto.getLineNo(), feto);

			} else if (to2.getClass().toString().contains("XMLHttpRequest")) {
				addXMLHttpRequestInfo(to2);
			} else if (to2.getClass().toString().contains("Timeout")) {
				addTimeoutInfo(to2);
			} else if (to2.getClass().toString().contains("DOMEventTrace")) {
				addDOMEventInfo(to2);
			}
		}
		System.out.println("step();");
		System.out.println("");
		System.setOut(oldOut);

	}

	private void addDOMEventInfo(TraceObject to) {
		DOMEventTrace deto = (DOMEventTrace) to;
		System.out.println("comment("+getDiagramIdentifier(to)+",C, up, wid 1.8 ht .6 \\");
		//System.out.println("comment("+getDiagramIdentifier(to)+",C, up,");
		System.out.println("\"Event Type: "+deto.getEventType()+"\" \\");
		System.out.println("\"Handler: "+deto.getEventHandler()+"\" \\");
		System.out.println("\"Target: "+deto.getTargetElementAttributes()+"\")");
	}

	private void addTimeoutInfo(TraceObject to) {
		TimingTrace ttto = (TimingTrace) to;
		if (to.getClass().toString().contains("TimeoutSet")) {
			// Only TimeoutSet stores 'delay', TimeoutCallback does not
			TimeoutSet tsto = (TimeoutSet) to;	
			System.out.println("comment("+getDiagramIdentifier(to)+",C, up, wid 1.5 ht .7 \\");
			System.out.println("\"Timing ID: "+ttto.getId()+"\" \\");
			System.out.println("\"Callback: "+ttto.getCallbackFunction()+"\" \\");
			System.out.println("\"Delay: "+tsto.getDelay()+"\")");	
		} else {
			System.out.println("comment("+getDiagramIdentifier(to)+",C, up, wid 1.5 ht .6 \\");
			System.out.println("\"Timing ID: "+ttto.getId()+"\" \\");
			System.out.println("\"Callback: "+ttto.getCallbackFunction()+"\")");	
		}
	}

	private void addXMLHttpRequestInfo(TraceObject to) {
		XMLHttpRequestTrace xhrto = (XMLHttpRequestTrace) to;
		System.out.println("comment("+getDiagramIdentifier(to)+",C, up, wid 1.2 ht .5 \\");
		System.out.println("\"XHR ID: "+xhrto.getId()+"\" \\");

		if (to.getClass().toString().contains("XMLHttpRequestOpen")) {
			// Open
			XMLHttpRequestOpen xoto = (XMLHttpRequestOpen) to;
			System.out.println("\"Method Type: "+xoto.getMethodType()+"\")");	
		} else if (to.getClass().toString().contains("XMLHttpRequestSend")) {
			// Send
			XMLHttpRequestSend xoso = (XMLHttpRequestSend) to;
			System.out.println("\"Message: "+xoso.getMessage()+"\")");
		} else {
			// Response
			XMLHttpRequestResponse xoro = (XMLHttpRequestResponse) to;
			System.out.println("\"Response: "+xoro.getResponse()+"\")");
		}
	}

	public void createMessages() {
		System.setOut(output);

		System.out.println(" # Message sequences");
		for (int i=1; i<functionTraceObjects.size(); i++) {
			TraceObject to = functionTraceObjects.get(i);
			if (to.getClass().toString().contains("FunctionEnter")) {
				// Message entering next function
				if (functionTraceObjects.get(i-1).getClass().toString().contains("DOMEventTrace") ||
						functionTraceObjects.get(i-1).getClass().toString().contains("TimeoutCallback") ||
						functionTraceObjects.get(i-1).getClass().toString().contains("XMLHttpRequestResponse") ||
						functionTraceObjects.get(i-1).getClass().toString().contains("FunctionCall")) {
					functionEnterMessage(functionTraceObjects.get(i-1), to);
				} 
				// Set new function as active
				functionEnter(to);
				numOfMessages++;
			} else if (to.getClass().toString().contains("FunctionExit")) {
				// Function ends execution, not return statement
				functionExitMessage();
				numOfMessages++;

			} else if (to.getClass().toString().contains("ReturnStatement")) {
				// Return to previous function
				functionReturnMessage();
				numOfMessages++;

			} else if (to.getClass().toString().contains("XMLHttpRequestOpen")) {
				// XMLHttpRequest is open, recursive call 'open'
				XHROpenMessage(to);
				numOfMessages++;

			} else if (to.getClass().toString().contains("XMLHttpRequestSend")) {
				XHRSendMessage(to);
				numOfMessages++;

			} else if (to.getClass().toString().contains("XMLHttpRequestResponse")) {
				XHRResponseMessage(to);
				numOfMessages++;

			} else if (to.getClass().toString().contains("TimeoutSet")) {
				TimeoutSetMessage(functionTraceObjects.get(i-1), to);
				numOfMessages++;

			} else if (to.getClass().toString().contains("TimeoutCallback")) {
				//TODO
			} 
		}
		System.setOut(oldOut);
	}

	private void TimeoutSetMessage(TraceObject before, TraceObject to) {
		if (before.getClass().toString().contains("FunctionCall")) {
			FunctionCall fcto = (FunctionCall) before;
			if (fcto.getTargetFunction().contains("setTimeout")) {
				// Timeout was set from the executing function
				System.out.println("message("+functionHeirarchy.get(functionHeirarchy.size()-1)+","
						+getDiagramIdentifier(to)+",\"setTimeout()\");");		
				System.out.println("active("+getDiagramIdentifier(to)+");");
				System.out.println("step();");
				System.out.println("inactive("+getDiagramIdentifier(to)+");");
			}
		} else {	
			// Not sure where the timeout was set from...
			System.out.println("active("+getDiagramIdentifier(to)+");");
			System.out.println("step();");
			System.out.println("inactive("+getDiagramIdentifier(to)+");");
		}
	}

	private void XHROpenMessage(TraceObject to) {

		push(getDiagramIdentifier(to));
		System.out.println("message("+functionHeirarchy.get(functionHeirarchy.size()-2)+","
				+functionHeirarchy.get(functionHeirarchy.size()-1)+",\"open\");");		
		System.out.println("active("+functionHeirarchy.get(functionHeirarchy.size()-1)+");");
		System.out.println("step();");
		System.out.println("inactive("+functionHeirarchy.get(functionHeirarchy.size()-1)+");");
		pop();
	}

	private void XHRSendMessage(TraceObject to) {
		push(getDiagramIdentifier(to));
		System.out.println("message("+functionHeirarchy.get(functionHeirarchy.size()-2)+","
				+functionHeirarchy.get(functionHeirarchy.size()-1)+",\"send\");");		
		System.out.println("active("+functionHeirarchy.get(functionHeirarchy.size()-1)+");");
		System.out.println("step();");
		System.out.println("inactive("+functionHeirarchy.get(functionHeirarchy.size()-1)+");");
		pop();
	}

	private void XHRResponseMessage(TraceObject to) {
		push(getDiagramIdentifier(to));
		System.out.println("active("+functionHeirarchy.get(functionHeirarchy.size()-1)+");");
		System.out.println("message("+functionHeirarchy.get(functionHeirarchy.size()-1)+","
				+functionHeirarchy.get(functionHeirarchy.size()-1)+",\"response\");");		
		System.out.println("inactive("+functionHeirarchy.get(functionHeirarchy.size()-1)+");");
		pop();
	}

	public void functionExitMessage() {
		System.out.println("inactive("+functionHeirarchy.get(functionHeirarchy.size()-1)+");");
		pop();
		System.out.println("step();");
	}

	private void functionEnterMessage(TraceObject from, TraceObject to) {
		FunctionEnter feto = (FunctionEnter) to;
		String fromID;

		if (from.getClass().toString().contains("FunctionCall")) {
			fromID = functionHeirarchy.get(functionHeirarchy.size()-1);
		} else {
			fromID = getDiagramIdentifier(from);
		}

		if (feto.getArgs() != null) {
			System.out.println("message("+fromID+","
					+getDiagramIdentifier(to)+",\"args: "+feto.getArgsString()+"\");");
		} else {
			// No arguments
			System.out.println("message("+fromID+","
					+getDiagramIdentifier(to)+",\"\");");
		}

	}

	private void functionEnter(TraceObject to) {		
		push(getDiagramIdentifier(to));

		// Mark new function as executing
		System.out.println("active("+functionHeirarchy.get(functionHeirarchy.size()-1)+");");
		System.out.println("step();");
	}

	private void functionReturnMessage() {
		System.out.println("rmessage("+functionHeirarchy.get(functionHeirarchy.size()-1)
				+","+functionHeirarchy.get(functionHeirarchy.size()-2)+");");
		System.out.println("inactive("+functionHeirarchy.get(functionHeirarchy.size()-1)+");");
		System.out.println("step();");
		pop();
	}

	private void addFunctionInfo(String object, FunctionEnter fe) {
		System.out.println("comment("+object+",C, up, wid 1.6 ht .5 \\");
		System.out.println("\"File: "+fe.getScopeName()+"\" \\");
		System.out.println("\"Line Number: "+fe.getLineNo()+"\")");
	}

	public void close() {
		System.setOut(output);
		System.out.println("");
		System.out.println("# Complete the lifelines");
		System.out.println("#size "+(numOfMessages*components.size()));

		System.out.println("step();");

		for (String cName: components) {
			System.out.println("complete("+cName+");");
		}

		System.out.println(".PE");
		System.setOut(oldOut);

		/* close the output file */
		output.close();
	}

	private static String pop() {
		// Removes the last function called and returns the name
		// FILO
		return functionHeirarchy.remove(functionHeirarchy.size()-1);
	}

	private static void push(String functionName) {
		functionHeirarchy.add(functionName.toUpperCase());
	}

	private static String getDiagramIdentifier(TraceObject tObject) {
		if (tObject.getClass().toString().contains("FunctionEnter")) {
			// Create components in the sequence diagram for developer-defined functions
			FunctionEnter feto = (FunctionEnter) tObject;
			return feto.getTargetFunction().toUpperCase()+feto.getLineNo();
		} else if (tObject.getClass().toString().contains("XMLHttpRequest")) {
			// Create actor for XMLHttpRequests
			XMLHttpRequestTrace xhtto = (XMLHttpRequestTrace) tObject;
			return "XMLHTTPREQUEST"+xhtto.getId();
		} else if (tObject.getClass().toString().contains("Timeout")) {
			// Create actors for child timing events, that is, timing events that spawned as a result of the origin event
			TimingTrace ttto = (TimingTrace) tObject;
			return "TIMEOUT"+ttto.getId();

		} else if (tObject.getClass().toString().contains("DOMEventTrace")) {
			// Create actors for child DOM events
			// TODO: Might need better identifier for DOM events
			DOMEventTrace deto = (DOMEventTrace) tObject;
			return "DOMEVENT"+deto.getEventType();
		}
		return null;
	}

	public String getOutputFolder() {
		return null;
	}
}
