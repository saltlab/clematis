package com.clematis.jsmodify;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ast.AstRoot;
import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.crawljax.util.Helper;
import com.clematis.jsmodify.JSModifyProxyPlugin;


/**
 * The JSInstrument proxy plugin used to add instrumentation code to JavaScript files.
 * 
 */
public class JSModifyProxyPlugin extends ProxyPlugin {

	private List<String> excludeFilenamePatterns;

	private final JSASTModifier modifier;
	
	private static String outputFolder = "";
	private static String jsFilename = "";

	/**
	 * Construct without patterns.
	 * 
	 * @param modify
	 *            The JSASTModifier to run over all JavaScript.
	 */
	public JSModifyProxyPlugin(JSASTModifier modify) {
		excludeFilenamePatterns = new ArrayList<String>();
		modifier = modify;
		
		outputFolder = Helper.addFolderSlashIfNeeded("clematis-output") + "js_snapshot";
	}

	/**
	 * Constructor with patterns.
	 * 
	 * @param modify
	 *            The JSASTModifier to run over all JavaScript.
	 * @param excludes
	 *            List with variable patterns to exclude.
	 */
	public JSModifyProxyPlugin(JSASTModifier modify, List<String> excludes) {
		excludeFilenamePatterns = excludes;
		modifier = modify;
	}

	public void excludeDefaults() {
		excludeFilenamePatterns.add(".*jquery[-0-9.]*.js?.*");
		excludeFilenamePatterns.add(".*jquery.*.js?.*");
		excludeFilenamePatterns.add(".*prototype.*js?.*");
		excludeFilenamePatterns.add(".*scriptaculous.*.js?.*");
		excludeFilenamePatterns.add(".*mootools.js?.*");
		excludeFilenamePatterns.add(".*dojo.xd.js?.*");

		// Example application specific
		excludeFilenamePatterns.add(".*tabcontent.js?.*");
	}


	@Override
	public String getPluginName() {
		return "JSInstrumentPlugin";
	}

	@Override
	public HTTPClient getProxyPlugin(HTTPClient in) {
		return new Plugin(in);
	}

	private boolean shouldModify(String name) {
		/* try all patterns and if 1 matches, return false */
		for (String pattern : excludeFilenamePatterns) {
			if (name.matches(pattern)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This method tries to add instrumentation code to the input it receives. The original input is
	 * returned if we can't parse the input correctly (which might have to do with the fact that the
	 * input is no JavaScript because the server uses a wrong Content-Type header for JSON data)
	 * 
	 * @param input
	 *            The JavaScript to be modified
	 * @param scopename
	 *            Name of the current scope (filename mostly)
	 * @return The modified JavaScript
	 */
	private synchronized String modifyJS(String input, String scopename) {

		System.out.println("Scope: " + scopename);
		
		if (!shouldModify(scopename)) {
			return input;
		}
		try {
			
			// Save original JavaScript files/nodes
			Helper.directoryCheck(getOutputFolder());
			setFileName(scopename);
			PrintStream output = new PrintStream(getOutputFolder() + getFilename());
			PrintStream oldOut = System.out;
			System.setOut(output);
			System.out.println(input);
			System.setOut(oldOut);
			
			AstRoot ast = null;

			/* initialize JavaScript context */
			Context cx = Context.enter();

			/* create a new parser */
			Parser rhinoParser = new Parser(new CompilerEnvirons(), cx.getErrorReporter());

			/* parse some script and save it in AST */
			ast = rhinoParser.parse(new String(input), scopename, 0);

			//modifier.setScopeName(scopename);
			modifier.setScopeName(getFilename());

			modifier.start(new String(input));

			/* recurse through AST */
			ast.visit(modifier);

			ast = modifier.finish(ast);

			/* clean up */
			Context.exit();

			return ast.toSource();
		} catch (RhinoException re) {
			System.err.println(re.getMessage()
					+ "Unable to instrument. This might be a JSON response sent"
					+ " with the wrong Content-Type or a syntax error.");

		} catch (IllegalArgumentException iae) {
			System.err.println("Invalid operator exception catched. Not instrumenting code.");
		} catch (IOException ioe) {
			System.err.println("Error saving original javascript files.");
		}
		System.err.println("Here is the corresponding buffer: \n" + input + "\n");

		return input;
	}

	private void setFileName(String scopename) {
		int index = scopename.lastIndexOf("/");
		jsFilename = scopename.substring(index+1);
	}

	private static String getOutputFolder() {
		return Helper.addFolderSlashIfNeeded(outputFolder);
	}
	
	private static String getFilename() {
		return jsFilename;
	}

	/**
	 * This method modifies the response to a request.
	 * 
	 * @param response
	 *            The response.
	 * @param request
	 *            The request.
	 * @return The modified response.
	 */
	private Response createResponse(Response response, Request request) {
		String type = response.getHeader("Content-Type");
		
		if (request.getURL().toString().contains("?beginrecord")) {
			JSExecutionTracer.preCrawling();
			return response;
		}
		
        if (request.getURL().toString().contains("?stoprecord")) {
        	JSExecutionTracer.postCrawling();
			return response;
		}
		
		
		if (request.getURL().toString().contains("?thisisafunctiontracingcall")) {
			String rawResponse = new String(request.getContent());
			JSExecutionTracer.addPoint(rawResponse);
			return response;
		}


		if (type != null && type.contains("javascript")) {

			/* instrument the code if possible */
			response.setContent(modifyJS(new String(response.getContent()),
					request.getURL().toString()).getBytes());
		} else if (type != null && type.contains("html")) {
			try {
				Document dom = Helper.getDocument(new String(response.getContent()));
				/* find script nodes in the html */
				NodeList nodes = dom.getElementsByTagName("script");

				for (int i = 0; i < nodes.getLength(); i++) {
					Node nType = nodes.item(i).getAttributes().getNamedItem("type");
					/* instrument if this is a JavaScript node */
					if ((nType != null && nType.getTextContent() != null && nType
							.getTextContent().toLowerCase().contains("javascript"))) {
						String content = nodes.item(i).getTextContent();
						if (content.length() > 0) {
							String js = modifyJS(content, request.getURL() + "script" + i);
							System.out.println(js);
							nodes.item(i).setTextContent(js);
							System.out.println(nodes.item(i).getTextContent());
							continue;
						}
					}

					/* also check for the less used language="javascript" type tag */
					nType = nodes.item(i).getAttributes().getNamedItem("language");
					if ((nType != null && nType.getTextContent() != null && nType
							.getTextContent().toLowerCase().contains("javascript"))) {
						String content = nodes.item(i).getTextContent();
						if (content.length() > 0) {
							String js = modifyJS(content, request.getURL() + "script" + i);
							nodes.item(i).setTextContent(js);
						}

					}
				}
				/* only modify content when we did modify anything */
				if (nodes.getLength() > 0) {
					/* set the new content */
					response.setContent(Helper.getDocumentToByteArray(dom));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/* return the response to the webbrowser */
		return response;
	}

	/**
	 * WebScarab plugin that adds instrumentation code.
	 * 
	 */
	private class Plugin implements HTTPClient {

		private HTTPClient client = null;

		/**
		 * Constructor for this plugin.
		 * 
		 * @param in
		 *            The HTTPClient connection.
		 */
		public Plugin(HTTPClient in) {
			client = in;
		}

		@Override
		public Response fetchResponse(Request request) throws IOException {

			Response response = client.fetchResponse(request);
			return createResponse(response, request);
		}
	}

}

