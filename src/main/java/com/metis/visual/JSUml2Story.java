package com.metis.visual;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import com.crawljax.util.Helper;
import com.metis.core.episode.Episode;
import com.metis.core.trace.DOMEventTrace;
import com.metis.core.trace.FunctionCall;
import com.metis.core.trace.FunctionEnter;
import com.metis.core.trace.TimeoutSet;
import com.metis.core.trace.TimingTrace;
import com.metis.core.trace.TraceObject;
import com.metis.core.trace.XMLHttpRequestOpen;
import com.metis.core.trace.XMLHttpRequestResponse;
import com.metis.core.trace.XMLHttpRequestSend;
import com.metis.core.trace.XMLHttpRequestTrace;

public class JSUml2Story {

	static ArrayList<String> functionHeirarchy = new ArrayList<String>();
	static PrintStream output = null;
	PrintStream oldOut = null;

	// Define all the objects for the sequence diagram
	static ArrayList<TraceObject> functionTraceObjects;
	static ArrayList<String> components = null;
	static ArrayList<String> comments = null;
	static TraceObject episodeSource = null;
	static int numOfMessages = 0;

	public JSUml2Story (PrintStream fileForAllEpisodes, Episode e) throws IOException {
		// Create pic file fore sequence diagram description
		//output = new PrintStream(outputFolder+"sequence_diagrams/"+e.getSource().getCounter()+".js");
		output = fileForAllEpisodes;
		episodeSource = e.getSource();
		components = new ArrayList<String>();
		comments = new ArrayList<String>();

		// Print initializing lines
		oldOut = System.out;
		System.setOut(output);
		System.out.println("// Episode " + episodeSource.getCounter());
		System.out.println("var episode"+e.getSource().getCounter()+" = new Episode();");
		System.setOut(oldOut);

		functionTraceObjects = e.getTrace().getTrace();

	}

	public void createComponents() {

		System.setOut(output);
		System.out.println("");
		System.out.println("// Components");

		int initialX = 90;
		int initialY = 60;

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
			initialY = 30;

			if (to.getClass().toString().contains("FunctionEnter")) {
				// Create components in the sequence diagram for developer-defined functions
				FunctionEnter feto = (FunctionEnter) to;
				System.out.println("var "+getDiagramIdentifier(to)+" = new FunctionTrace('"+feto.getTargetFunction()+"');");
				System.out.println(getDiagramIdentifier(to)+".setFileName('"+feto.getScopeName()+"');");
				System.out.println(getDiagramIdentifier(to)+".setLineNo("+feto.getLineNo()+");");
				initialY = 60;
			} else if (to.getClass().toString().contains("XMLHttpRequest")) {
				// Create actor for XMLHttpRequests
				XMLHttpRequestTrace xhtto = (XMLHttpRequestTrace) to;
				System.out.println("var "+getDiagramIdentifier(to)+" = new XHREvent(false);");
				System.out.println(getDiagramIdentifier(to)+".setXHRId('"+xhtto.getId()+"');");
			} else if (to.getClass().toString().contains("Timeout")) {
				// Create actors for child timing events, that is, timing events that spawned as a result of the origin event
				TimingTrace ttto = (TimingTrace) to;
				System.out.println("var "+getDiagramIdentifier(to)+" = new TimingTrace(false);");
				System.out.println(getDiagramIdentifier(to)+".setTimeoutId("+ttto.getId()+");");		
				System.out.println(getDiagramIdentifier(to)+".setCallbackFunction('"+ttto.getCallbackFunction()+"');");		
			} else if (to.getClass().toString().contains("DOMEventTrace")) {
				// Create actors for child DOM events
				DOMEventTrace deto = (DOMEventTrace) to;
				System.out.println("var "+getDiagramIdentifier(to)+" = new DOMEventTrace(false);");
				System.out.println(getDiagramIdentifier(to)+".setEventType('"+deto.getEventType()+"');");		
				System.out.println(getDiagramIdentifier(to)+".setEventHandler('"+deto.getEventHandler()+"');");		
				System.out.println(getDiagramIdentifier(to)+".setTargetElement('"+deto.getTargetElement()+"');");		
			}
			System.out.println(getDiagramIdentifier(to)+".createDiagramObject("+initialX+", "+initialY+");");
			System.out.println("episode"+episodeSource.getCounter()+".addComponent("+getDiagramIdentifier(to)+");");
			System.out.println("");
			initialX += 200;
		}
		push(components.get(0));
		System.setOut(oldOut);
	}

	@SuppressWarnings("unused")
	private void addDOMEventInfo(TraceObject to) {
		DOMEventTrace deto = (DOMEventTrace) to;
		System.out.println("comment("+getDiagramIdentifier(to)+",C, up, wid 1.8 ht .6 \\");
		//System.out.println("comment("+getDiagramIdentifier(to)+",C, up,");
		System.out.println("\"Event Type: "+deto.getEventType()+"\" \\");
		System.out.println("\"Handler: "+deto.getEventHandler()+"\" \\");
		System.out.println("\"Target: "+deto.getTargetElementAttributes()+"\")");
	}

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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

		int initialY = 60;		
		
		System.out.println("// Message sequences");
		for (int i=1; i<functionTraceObjects.size(); i++) {
			
			TraceObject to = functionTraceObjects.get(i);
			if (to.getClass().toString().contains("FunctionEnter")) {
				// Message entering next function
				if (functionTraceObjects.get(i-1).getClass().toString().contains("DOMEventTrace") ||
						functionTraceObjects.get(i-1).getClass().toString().contains("TimeoutCallback") ||
						functionTraceObjects.get(i-1).getClass().toString().contains("XMLHttpRequestResponse") ||
						functionTraceObjects.get(i-1).getClass().toString().contains("FunctionCall")) {
					functionEnterMessage(functionTraceObjects.get(i-1), to, initialY);
				} else {
					// Set new function as active
					functionEnter(to, initialY);
				}
				initialY += 60;
			} else if (to.getClass().toString().contains("FunctionExit")) {
				// Function ends execution, not return statement
				functionExitMessage(initialY);
			} else if (to.getClass().toString().contains("ReturnStatement")) {
				// Return to previous function
				functionReturnMessage(initialY);
				initialY += 60;
			} else if (to.getClass().toString().contains("XMLHttpRequestOpen")) {
				// XMLHttpRequest is open, recursive call 'open'
				XHROpenMessage(to, initialY);
				initialY += 60;
			} else if (to.getClass().toString().contains("XMLHttpRequestSend")) {
				XHRSendMessage(to, initialY);
				initialY += 60;
			} else if (to.getClass().toString().contains("XMLHttpRequestResponse")) {
				System.out.println("// " + functionTraceObjects.size());
				XHRResponseMessage(to, initialY);
				initialY += 60;
			} else if (to.getClass().toString().contains("TimeoutSet")) {
				TimeoutSetMessage(functionTraceObjects.get(i-1), to, initialY);
				initialY += 60;
			} else if (to.getClass().toString().contains("TimeoutCallback")) {
				//TODO
			} 
		}
		System.setOut(oldOut);
	}

	private void TimeoutSetMessage(TraceObject before, TraceObject to, int y) {
		if (before.getClass().toString().contains("FunctionCall")) {
			FunctionCall fcto = (FunctionCall) before;
			if (fcto.getTargetFunction().contains("setTimeout")) {
				// Timeout was set from the executing function	
				System.out.println("episode"+episodeSource.getCounter()+".addMessage(new UMLCallMessage({a : "+functionHeirarchy.get(functionHeirarchy.size()-1)+".getDiagramObject(), " +
						"b : "+getDiagramIdentifier(to)+".getDiagramObject(), " +
						"y : "+y+"}));");		
				//System.out.println("setTimeout()");
			}
		} else {	
			// Not sure where the timeout was set from...
			System.out.println("episode"+episodeSource.getCounter()+".addMessage(new UMLCallMessage({a : "+getDiagramIdentifier(to)+".getDiagramObject(), " +
					"b : "+getDiagramIdentifier(to)+".getDiagramObject(), " +
					"y : "+y+"}));");	
		}
	}

	private void XHROpenMessage(TraceObject to, int y) {

		push(getDiagramIdentifier(to));	
		System.out.println("episode"+episodeSource.getCounter()+".addMessage(new UMLCallMessage({a : "+functionHeirarchy.get(functionHeirarchy.size()-2)+".getDiagramObject(), " +
				"b : "+functionHeirarchy.get(functionHeirarchy.size()-1)+".getDiagramObject(), " +
				"y : "+y+"}));");
		//System.out.println("setName(open)");
		pop();
	}

	private void XHRSendMessage(TraceObject to, int y) {
		push(getDiagramIdentifier(to));
		System.out.println("episode"+episodeSource.getCounter()+".addMessage(new UMLCallMessage({a : "+functionHeirarchy.get(functionHeirarchy.size()-2)+".getDiagramObject(), " +
				"b : "+functionHeirarchy.get(functionHeirarchy.size()-1)+".getDiagramObject(), " +
				"y : "+y+"}));");		
		//System.out.println("setName(open)");
		pop();
	}

	private void XHRResponseMessage(TraceObject to, int y) {
		push(getDiagramIdentifier(to));
		System.out.println("episode"+episodeSource.getCounter()+".addMessage(new UMLCallMessage({a : "+functionHeirarchy.get(functionHeirarchy.size()-1)+".getDiagramObject(), " +
				"b : "+functionHeirarchy.get(functionHeirarchy.size()-1)+".getDiagramObject(), " +
				"y : "+y+"}));");		
		//System.out.println("setName(response)");
		pop();
	}

	public void functionExitMessage(int y) {

		pop();
		//System.out.println("step();");
	}

	private void functionEnterMessage(TraceObject from, TraceObject to, int y) {
		FunctionEnter feto = (FunctionEnter) to;
		String fromID;

		if (from.getClass().toString().contains("FunctionCall")) {
			fromID = functionHeirarchy.get(functionHeirarchy.size()-1);
		} else {
			fromID = getDiagramIdentifier(from);
		}

		if (feto.getArgs() != null) {
			System.out.println("var message_"+getDiagramIdentifier(to)+"_"+y+" = new UMLCallMessage({a : "+fromID+".getDiagramObject(), " +
					"b : "+getDiagramIdentifier(to)+".getDiagramObject(), " +
					"y : "+y+"});");
			System.out.println("message_"+getDiagramIdentifier(to)+"_"+y+".setName(\"args: "+feto.getArgsString()+"\");");
			System.out.println("message_"+getDiagramIdentifier(to)+"_"+y+".notifyChange()");
			System.out.println("episode"+episodeSource.getCounter()+".addMessage(message_"+getDiagramIdentifier(to)+"_"+y+");");
			//System.out.println(UMLCallMessage.setName(feto.getArgsString());
		} else {
			// No arguments
			System.out.println("episode"+episodeSource.getCounter()+".addMessage(new UMLCallMessage({a : "+fromID+".getDiagramObject(), " +
					"b : "+getDiagramIdentifier(to)+".getDiagramObject(), " +
					"y : "+y+"}));");;
		}
		push(getDiagramIdentifier(to));

	}

	private void functionEnter(TraceObject to, int y) {	
		System.out.println("episode"+episodeSource.getCounter()+".addMessage(new UMLCallMessage({a : "+getDiagramIdentifier(to)+".getDiagramObject(), " +
				"b : "+getDiagramIdentifier(to)+".getDiagramObject(), " +
				"y : "+y+"}));");;
				push(getDiagramIdentifier(to));
	}

	private void functionReturnMessage(int y) {
		System.out.println("episode"+episodeSource.getCounter()+".addMessage(new UMLReplyMessage({a : "+functionHeirarchy.get(functionHeirarchy.size()-1)+".getDiagramObject(), " +
				"b : "+functionHeirarchy.get(functionHeirarchy.size()-2)+".getDiagramObject(), " +
				"y :"+y+"}));");
		//		System.out.println("step();");
		pop();
	}

	@SuppressWarnings("unused")
	private void addFunctionInfo(String object, FunctionEnter fe) {
		System.out.println("comment("+object+",C, up, wid 1.6 ht .5 \\");
		System.out.println("\"File: "+fe.getScopeName()+"\" \\");
		System.out.println("\"Line Number: "+fe.getLineNo()+"\")");
	}

	public void close() {
		System.setOut(output);
		System.out.println("allEpisodes.push(episode"+episodeSource.getCounter()+");");
		System.out.println("");
		System.setOut(oldOut);
		//output.close();
	}

	private static String pop() {
		// Removes the last function called and returns the name
		// FILO
		return functionHeirarchy.remove(functionHeirarchy.size()-1);
	}

	private static void push(String functionName) {
		functionHeirarchy.add(functionName);
	}

	private static String getDiagramIdentifier(TraceObject tObject) {
		if (tObject.getClass().toString().contains("FunctionEnter")) {
			// Create components in the sequence diagram for developer-defined functions
			FunctionEnter feto = (FunctionEnter) tObject;
			return feto.getTargetFunction()+feto.getLineNo()+"_"+episodeSource.getCounter();
		} else if (tObject.getClass().toString().contains("XMLHttpRequest")) {
			// Create actor for XMLHttpRequests
			XMLHttpRequestTrace xhtto = (XMLHttpRequestTrace) tObject;
			return "XMLHttpRequest"+xhtto.getId()+"_"+episodeSource.getCounter();
		} else if (tObject.getClass().toString().contains("Timeout")) {
			// Create actors for child timing events, that is, timing events that spawned as a result of the origin event
			TimingTrace ttto = (TimingTrace) tObject;
			return "Timeout"+ttto.getId()+"_"+episodeSource.getCounter();
		} else if (tObject.getClass().toString().contains("DOMEventTrace")) {
			// Create actors for child DOM events
			DOMEventTrace deto = (DOMEventTrace) tObject;
			return "DOMEvent"+deto.getEventType()+"_"+episodeSource.getCounter();
		}
		return null;
	}

	public String getOutputFolder() {
		return null;
	}
}
