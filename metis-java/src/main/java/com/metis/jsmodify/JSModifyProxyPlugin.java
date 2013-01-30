package com.metis.jsmodify;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

//import com.crawljax.plugins.aji.executiontracer.JSExecutionTracer;
import com.metis.util.Helper;
import com.metis.jsmodify.JSModifyProxyPlugin;


/**
 * The JSInstrument proxy plugin used to add instrumentation code to JavaScript files.
 * 
 */
public class JSModifyProxyPlugin extends ProxyPlugin {

	private List<String> excludeFilenamePatterns;

	private final JSASTModifier modifier;

	/**
	 * Construct without patterns.
	 * 
	 * @param modify
	 *            The JSASTModifier to run over all JavaScript.
	 */
	public JSModifyProxyPlugin(JSASTModifier modify) {
		excludeFilenamePatterns = new ArrayList<String>();
		modifier = modify;
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
		//excludeFilenamePatterns = new ArrayList<String>();
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

		if (!shouldModify(scopename)) {
			return input;
		}
		try {
			AstRoot ast = null;

			/* initialize JavaScript context */
			Context cx = Context.enter();

			/* create a new parser */
			Parser rhinoParser = new Parser(new CompilerEnvirons(), cx.getErrorReporter());

			/* parse some script and save it in AST */
			ast = rhinoParser.parse(new String(input), scopename, 0);

			modifier.setScopeName(scopename);

			modifier.start(new String(input));

			/* recurse through AST */
			ast.visit(modifier);

			ast = modifier.finish(ast);

			/* clean up */
			Context.exit();

			System.out.println(ast.toSource());

			return ast.toSource();
		} catch (RhinoException re) {
			System.err.println(re.getMessage()
					+ "Unable to instrument. This might be a JSON response sent"
					+ " with the wrong Content-Type or a syntax error.");

		} catch (IllegalArgumentException iae) {
			System.err.println("Invalid operator exception catched. Not instrumenting code.");
		}
		System.err.println("Here is the corresponding buffer: \n" + input + "\n");

		return input;
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
		
/*		System.out.println("***************************");
		try {
			System.out.println(new String(request.getContent(), "US-ASCII"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("***************************");
*/
		if (request.getURL().toString().contains("?thisisafunctiontracingcall")) {
			System.out.println("Execution trace request " + request.getURL().toString());
			String rawResponse = new String(request.getContent());
			String[] splitResponse = rawResponse.split("endofline,");
			for(int i = 0; i < splitResponse.length; i++) {
				JSExecutionTracer.addPoint(splitResponse[i]);
			}
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
			
/*			System.out.println("++++++++++++++++++++++++++++++++++++++++++");
			System.out.println(new String(request.getContent()));
			System.out.println("))))))))))))))))))))))))))))))))))))))))))");
*/
			
			Response response = client.fetchResponse(request);

			return createResponse(response, request);
		}
	}

}

