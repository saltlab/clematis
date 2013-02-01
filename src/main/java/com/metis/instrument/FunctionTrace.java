package com.metis.instrument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.Symbol;

public class FunctionTrace extends AstInstrumenter {

	/**
	 * This is used by the JavaScript node creation functions that follow.
	 */
	private CompilerEnvirons compilerEnvirons = new CompilerEnvirons();

	/**
	 * Contains the scopename of the AST we are visiting. Generally this will be the filename
	 */
	private String scopeName = null;

	/**
	 * List with regular expressions of variables that should not be instrumented.
	 */
	private ArrayList<String> excludeList = new ArrayList<String>();
	private ArrayList<PointOfInterest> functionTokens = new ArrayList<PointOfInterest>();
	private String src = "";

	/**
	 * Construct without patterns.
	 */
	public FunctionTrace() {
		super();
	}

	/**
	 * Constructor with patterns.
	 * 
	 * @param excludes
	 *            List with variable patterns to exclude.
	 */
	public FunctionTrace(ArrayList<String> excludes) {
		super(excludes);
		excludeList = excludes;
	}

	/**
	 * Parse some JavaScript to a simple AST.
	 * 
	 * @param code
	 *            The JavaScript source code to parse.
	 * @return The AST node.
	 */
	public AstNode parse(String code) {
		Parser p = new Parser(compilerEnvirons, null);
		return p.parse(code, null, 0);

	}

	/**
	 * Find out the function name of a certain node and return "anonymous" if it's an anonymous
	 * function.
	 * 
	 * @param f
	 *            The function node.
	 * @return The function name.
	 */
	protected String getFunctionName(FunctionNode f) {
		Name functionName = f.getFunctionName();

		if (functionName == null) {
			return "anonymous" + f.getLineno();
		} else {
			return functionName.toSource();
		}
	}

	/**
	 * @param scopeName
	 *            the scopeName to set
	 */
	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}

	/**
	 * @return the scopeName
	 */
	public String getScopeName() {
		return scopeName;
	}

	@Override
	public  boolean visit(AstNode node){
		int tt = node.getType();
		if (tt == org.mozilla.javascript.Token.FUNCTION) {
			handleFunction((FunctionNode) node);
		} else if (tt == org.mozilla.javascript.Token.CALL) {
			handleFunctionCall((FunctionCall) node);
		} else if (tt == org.mozilla.javascript.Token.RETURN) {
			handleReturn((ReturnStatement) node);
		}
		return true;  // process kids
	}

	@Override
	public AstNode createNodeInFunction(FunctionNode function, int lineNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AstNode createNode(FunctionNode function, String postfix, int lineNo) {
		String name;
		String code;

		name = getFunctionName(function);
		if (postfix == ":::EXIT") {
			postfix += lineNo;
		}

		/* only add instrumentation code if there are variables to log */

		/* TODO: this uses JSON.stringify which only works in Firefox? make browser indep. */
		/* post to the proxy server */
		code = "send(new Array('" + getScopeName() + "." + name + "', '" + postfix + "'));";

		return parse(code);
	}

	@Override
	public AstNode createPointNode(String shouldLog, int lineNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AstRoot finish(AstRoot node) {
		// Adds necessary instrumentation to the root node src
		String isc = addInstrumentationCode(src);
		AstRoot iscNode = rhinoCreateNode(isc);

		// Add wrapper functions to top of JS node
		iscNode.addChildToFront(jsLoggingFunctions());

		// Return new instrumented node/code
		return iscNode;
	}

	@Override
	public void start(String node) {
		src=node;
	}

	/**
	 * Returns all variables in scope.
	 * 
	 * @param func
	 *            The function.
	 * @return All variables in scope.
	 */
	protected String[] getVariablesNamesInScope(Scope scope) {
		TreeSet<String> result = new TreeSet<String>();

		do {
			/* get the symboltable for the current scope */
			Map<String, Symbol> t = scope.getSymbolTable();

			if (t != null) {
				for (String key : t.keySet()) {
					/* read the symbol */
					Symbol symbol = t.get(key);
					/* only add variables and function parameters */
					if (symbol.getDeclType() == Token.LP || symbol.getDeclType() == Token.VAR) {
						result.add(symbol.getName());
					}
				}
			}

			/* get next scope (upwards) */
			scope = scope.getEnclosingScope();
		} while (scope != null);

		/* return the result as a String array */
		return result.toArray(new String[0]);
	}

	/**
	 * Check if we should instrument this variable by matching it against the exclude variable
	 * regexps.
	 * 
	 * @param name
	 *            Name of the variable.
	 * @return True if we should add instrumentation code.
	 */
	protected boolean shouldInstrument(String name) {
		if (name == null) {
			return false;
		}

		/* is this an excluded variable? */
		for (String regex : excludeList) {
			if (name.matches(regex)) {
				return false;
			}
		}
		return true;
	}

	public ArrayList<String> getExcludeList() {
		return this.excludeList;
	}

	private PointOfInterest createEntry(String name, int type, int[] range, int lineNo, String body, int hashCode) {
		PointOfInterest toke = null;
		try {
			toke = new PointOfInterest(new Object[]{name, type, range[0], range[1], lineNo, body, hashCode});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toke;
	}

	private void handleFunction(FunctionNode node) {
		// Store information on function declarations
		AstNode parent = node.getParent();
		String name = node.getName();
		String body = node.toSource();
		int[] range = {node.getBody().getAbsolutePosition()+1,node.getEncodedSourceEnd()-1};
		int hash = node.hashCode();	
		int type = node.getType();
		int lineNo = node.getLineno();

		if (node.getFunctionType() == FunctionNode.FUNCTION_EXPRESSION) {
			// Complicated Case
			if (node.getName() == "" && parent.getType() == org.mozilla.javascript.Token.COLON) {
				// Assignment Expression					
				name = src.substring(node.getParent().getAbsolutePosition(),node.getEncodedSourceStart());
				name = name.substring(0,name.indexOf(":"));
			} else if (node.getName() == "" && parent.getType() == org.mozilla.javascript.Token.ASSIGN) {
				name = src.substring(node.getParent().getAbsolutePosition(),node.getEncodedSourceStart());
				name = name.substring(name.lastIndexOf(".")+1,name.indexOf("="));
			}
			PointOfInterest toke = createEntry(name, type, range, lineNo, body, hash);
			if (toke != null) functionTokens.add(toke);
		}
		else if (node.getFunctionType() == FunctionNode.FUNCTION_STATEMENT) {
			// Simple Case
			PointOfInterest toke = createEntry(name, type, range, lineNo, body, hash);
			if (toke != null) functionTokens.add(toke);
		}
		else if (node.getFunctionType() == FunctionNode.FUNCTION_EXPRESSION_STATEMENT) {
			PointOfInterest toke = createEntry(name, type, range, lineNo, body, hash);
			if (toke != null) functionTokens.add(toke);
		}
		else {
			// unrecognized;
			System.out.println("Unrecognized function name at " + lineNo);
		}
	}

	private void handleFunctionCall(FunctionCall node) {
		// Store information on function calls
		AstNode target = node.getTarget();
		String targetBody = src.substring(target.getAbsolutePosition(),target.getAbsolutePosition()+target.getLength());
		String body = node.toSource();		
		int hash = 0;
		int[] range = {0,0};
		int lineNo = node.getLineno();

		range[0] = node.getAbsolutePosition();
		range[1] = node.getAbsolutePosition()+node.getLength();

		int tt = target.getType();
		if (tt == org.mozilla.javascript.Token.NAME) {
			// Regular function call, 39
			// E.g. parseInt, print, startClock
			hash = -1;
		} else if (tt == org.mozilla.javascript.Token.GETPROP) {
			// Class specific function call, 33
			// E.g. document.getElementById, e.stopPropagation
			hash = -2;
			String[] methods = targetBody.split("\\.");
			range[0] += targetBody.lastIndexOf(methods[methods.length-1])-1;
			targetBody = methods[methods.length-1];
		} 

		PointOfInterest toke = createEntry(targetBody, node.getType(), range, lineNo, body, hash);
		if (toke != null && !(targetBody.contains("function") && targetBody.contains("("))) { 
			functionTokens.add(toke);
		} else if (targetBody.contains("function")) {
			System.out.println("No support for self invoking.");
		} else {
			System.out.println("Error instrumenting function call.");
		}
	}

	private void handleReturn(ReturnStatement node) {
		// return statements
		FunctionNode enclosingFunction = node.getEnclosingFunction();
		String name = node.toSource();
		String body = "";
		int[] range = {-1,-1};
		int hash = 0;
		int lineNo = node.getLineno();

		// Get name of enclosing function
		String enclosingFunctionName = new String();
		if (enclosingFunction != null) {
			if (enclosingFunction.getName() == "" && enclosingFunction.getType() == org.mozilla.javascript.Token.COLON) {
				// Assignment Expression					
				enclosingFunctionName = src.substring(enclosingFunction.getParent().getAbsolutePosition(),enclosingFunction.getEncodedSourceStart()-1);
				enclosingFunctionName = enclosingFunctionName.substring(0,enclosingFunctionName.indexOf(":"));
			} else {
				enclosingFunctionName = enclosingFunction.getName();
			}
		}

		if (node.getReturnValue() != null) {
			// Wrap return value
			range[0] = node.getReturnValue().getAbsolutePosition();
			range[1] = node.getReturnValue().getAbsolutePosition()+node.getReturnValue().getLength();
		} else {
			// Return value is void
			range[0] = node.getAbsolutePosition()+node.getLength()-1;
			range[1] = node.getAbsolutePosition()+node.getLength()-1;
			hash = -1;
		}
		body = src.substring(range[0], range[1]);
		PointOfInterest toke = createEntry(name, node.getType(), range, lineNo, body, hash);
		if (toke != null) functionTokens.add(toke);
	}


	private String addInstrumentationCode (String javaScriptBody) {

		ArrayList<PointOfInterest> pointsOfInstrumentation = new ArrayList<PointOfInterest>();

		for (int i = functionTokens.size()-1; i >= 0; i--) {
			// Generate lines to add
			if (functionTokens.get(i).getType() == org.mozilla.javascript.Token.CALL) {
				pointsOfInstrumentation.add(functionTokens.get(i));
			} else if (functionTokens.get(i).getType() == org.mozilla.javascript.Token.FUNCTION 
					|| functionTokens.get(i).getType() == org.mozilla.javascript.Token.RETURN) {
				// Add code at beginning of function declaration
				pointsOfInstrumentation.add(new PointOfInterest(new Object[]{functionTokens.get(i).getName(),
						functionTokens.get(i).getType(),
						functionTokens.get(i).getRange()[0],
						-1,
						functionTokens.get(i).getLineNo(),
						functionTokens.get(i).getBody(),
						functionTokens.get(i).getHash()}));

				// Add code before end of function declaration
				pointsOfInstrumentation.add(new PointOfInterest(new Object[]{functionTokens.get(i).getName(),
						functionTokens.get(i).getType(),
						functionTokens.get(i).getRange()[1],
						-2,
						functionTokens.get(i).getLineNo(),
						functionTokens.get(i).getBody(),
						functionTokens.get(i).getHash()}));
			}
		}

		// Sort list so we can begin instrumenting from bottom upwards
		Collections.sort(pointsOfInstrumentation, new Comparator<PointOfInterest>(){
			public int compare(PointOfInterest s1, PointOfInterest s2) {
				return s1.getBegin()-s2.getBegin();
			}
		});

		for (int i = pointsOfInstrumentation.size()-1; i >= 0; i--) {
			// Insert instrumentation statements

			PointOfInterest POI = pointsOfInstrumentation.get(i);

			if (POI.getType() == org.mozilla.javascript.Token.CALL) {
				if (POI.getHash() == -2) {
					// Class method
					try {
						javaScriptBody = javaScriptBody.substring(0,POI.getRange()[0]) +
								javaScriptBody.substring(POI.getRange()[0], POI.getRange()[1]).replaceFirst("."+POI.getName(), "[FCW(\""+POI.getName()+"\", "+ POI.getLineNo() +")]") +
								javaScriptBody.substring(POI.getRange()[1]);

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// Regular function call, POI.getHash() == -1
					try {
						javaScriptBody = javaScriptBody.substring(0, POI.getRange()[0]) +
								javaScriptBody.substring(POI.getRange()[0], POI.getRange()[1]).replaceFirst(POI.getName(), "FCW("+POI.getName()+",'"+POI.getName()+"',"+POI.getLineNo()+")") +
								javaScriptBody.substring(POI.getRange()[1]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					javaScriptBody = javaScriptBody.substring(0, POI.getRange()[0]) +
							POI.toString()+
							javaScriptBody.substring(POI.getRange()[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} 
		}	
		return javaScriptBody;
	}
}
