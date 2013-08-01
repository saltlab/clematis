package com.clematis.instrument;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ErrorReporter;
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

import com.clematis.jsmodify.JSModifyProxyPlugin;

public class FunctionTrace extends AstInstrumenter {

	/**
	 * This is used by the JavaScript node creation functions that follow.
	 */
	private CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
	private ErrorReporter errorReporter = compilerEnvirons.getErrorReporter();

	/**
	 * Contains the scopename of the AST we are visiting. Generally this will be the filename
	 */
	private String scopeName = null;

	/**
	 * List with regular expressions of variables that should not be instrumented.
	 */
	private ArrayList<String> excludeList = new ArrayList<String>();
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
	public AstRoot parse(String code) {
		Parser p = new Parser(compilerEnvirons, errorReporter);

		//System.out.println(code);
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
		} else if (tt == org.mozilla.javascript.Token.CALL 
				&& node.toSource().indexOf("FUNCTION_") == -1 
				&& node.toSource().indexOf("RSW(") == -1 
				&& node.toSource().indexOf("FCW(") == -1) {
			handleFunctionCall((FunctionCall) node);
		} else if (tt == org.mozilla.javascript.Token.RETURN) {
			handleReturn((ReturnStatement) node);
		}



		if (tt == org.mozilla.javascript.Token.CALL 
				&& (node.toSource().indexOf("FUNCTION_") > -1)) {
			return false; // Don't process kids if the function call is part of our instrumentation
		} else {
			return true;  // process kids
		}
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
		String isc = node.toSource().replaceAll("\\)]\\;+\\n+\\(", ")](").replaceAll("\\)\\;\\n+\\(", ")(");
		AstRoot iscNode = rhinoCreateNode(isc);

		/*******************/
		// todo todo todo todo
		StringTokenizer tokenizer = new StringTokenizer(JSModifyProxyPlugin.scopeNameForExternalUse, "?");
		String baseUrl = "";
		
		if (tokenizer.hasMoreElements())
			baseUrl = tokenizer.nextToken();
		
		PrintStream output2;
		try {
			output2 = new PrintStream("finish_func_trace.txt");
			PrintStream oldOut2 = System.out;
			System.setOut(output2);
			System.out.println("new scope: " + JSModifyProxyPlugin.scopeNameForExternalUse + "\n ---");
			System.out.println("new newBaseUrl: " + baseUrl + "\n ---");
			boolean baseUrlExists = false;
			for (String str: JSModifyProxyPlugin.visitedBaseUrls) {
				System.out.print(str);
				if (/*str.startsWith(newBaseUrl) || */str.equals(baseUrl)) {
					System.out.println(" -> exists");
					baseUrlExists = true;
					//return input;
				}
				else {
					System.out.println();
				}
			}
			System.setOut(oldOut2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (String str : JSModifyProxyPlugin.visitedBaseUrls) {
			if (str.equals(baseUrl)) {
				return iscNode;
			}
		}
		/////// TODO JUST A HACK FOR RUNNING THE EXPERIMENT. MUST BE REMOVED AFTER
//		if (baseUrl.equals("http://localhost:8888/files/phorm.js"))
		if (baseUrl.equals("http://localhost:8888/phormer331/"))
			return iscNode;
			
		/*******************/
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

	private void handleFunction(FunctionNode node) {

		// Store information on function declarations
		AstNode parent = node.getParent();
		String name = node.getName();
		String body = node.toSource();
		int[] range = {node.getBody().getAbsolutePosition()+1,node.getEncodedSourceEnd()-1};
		int hash = node.hashCode();	
		int type = node.getType();
		int lineNo = node.getLineno()+1;
		String arguments = new String();



		if(node.getParamCount() > 0){
			List<AstNode> params = node.getParams();
			for (AstNode pp: params) {
				arguments +=  "," + pp.toSource();
			}
			arguments = arguments.replaceFirst(",", "");
		} else {
			arguments = "";
		}

		if (node.getFunctionType() == FunctionNode.FUNCTION_EXPRESSION) {
			System.out.println("1111111111111111111111111");
			// Complicated Case
			if (node.getName() == "" && parent.getType() == org.mozilla.javascript.Token.COLON) {
				// Assignment Expression					
				name = node.getParent().toSource().substring(0,node.getParent().toSource().indexOf(node.toSource()));
				name = name.substring(0,name.indexOf(":"));
			} else if (node.getName() == "" && parent.getType() == org.mozilla.javascript.Token.ASSIGN) {
				name = node.getParent().toSource().substring(0,node.getParent().toSource().indexOf(node.toSource()));
				name = name.substring(name.lastIndexOf(".")+1,name.indexOf("="));
			}
		} else {
			System.out.println("22222222222222222222");
			if (node.getFunctionType() == FunctionNode.FUNCTION_STATEMENT) {
				System.out.println("* " + node.getName());
			}
			// unrecognized;
			System.out.println("Unrecognized function name at " + lineNo);
		}

		// Add code at beginning of function declaration
		PointOfInterest beginningPOI = new PointOfInterest(new Object[]{name,
				type,
				range[0],
				-1,
				lineNo,
				body,
				hash,
				getScopeName(),
				arguments});

		// Add code before end of function declaration
		PointOfInterest endingPOI = new PointOfInterest(new Object[]{name,
				type,
				range[1],
				-2,
				lineNo,
				body,
				hash,
				getScopeName(),
				arguments});		

		AstNode beginningNode = parse(beginningPOI.toString());

		AstNode endingNode = parse(endingPOI.toString());
		node.getBody().addChildToFront(beginningNode);
		node.getBody().addChildToBack(endingNode);

	}

	private void updateAllLineNo(AstNode body) {

		AstNode lastChild = (AstNode) body.getLastChild();

		if (lastChild == null) {
			// No children
			return;
		}

		while (true) {
			// Update line number of immediate children
			lastChild.setLineno(lastChild.getLineno()+body.getLineno());

			// Call recursively for grandchildren, greatgrandchildren, etc.
			updateAllLineNo(lastChild);

			if (body.getChildBefore(lastChild) != null) {
				lastChild = (AstNode) body.getChildBefore(lastChild);
			} else {
				break;
			}

		} 

	}

	private void handleFunctionCall(FunctionCall node) {

		// Store information on function calls
		AstNode target = node.getTarget();
		String targetBody = target.toSource();
		int[] range = {0,0};
		int lineNo = -1;
		if (node.getParent().toSource().indexOf("FCW(") > -1) {
			lineNo = node.getParent().getParent().getParent().getLineno() +1;
		} else {
			lineNo = node.getLineno()+1;
		}
		AstNode newTarget = null;

		range[0] = node.getAbsolutePosition();
		range[1] = node.getAbsolutePosition()+node.getLength();

		if (target.toSource().indexOf("FCW") == 0) {
			// We don't want to instrument out code (dirty way)
			return;
		}


		int tt = target.getType();
		if (tt == org.mozilla.javascript.Token.NAME) {
			// Regular function call, 39
			// E.g. parseInt, print, startClock
			targetBody = target.toSource();
			String newBody = target.toSource().replaceFirst(targetBody, "FCW("+targetBody+",'"+targetBody+"',"+lineNo+")");
			System.out.println("--- NAME: " + newBody);
			newTarget = parse(newBody);

		} else if (tt == org.mozilla.javascript.Token.GETPROP) {
			// Class specific function call, 33
			// E.g. document.getElementById, e.stopPropagation
			String[] methods = targetBody.split("\\.");
			range[0] += targetBody.lastIndexOf(methods[methods.length-1])-1;
			targetBody = methods[methods.length-1];

			String newBody = target.toSource().replaceFirst("."+targetBody, "[FCW(\""+targetBody+"\", "+lineNo+")]");
			System.out.println("--- PROP: " + newBody);
			newTarget = parse(newBody);
		} else {
			if (tt == org.mozilla.javascript.Token.GETELEM) {
				System.out.println("====== " + org.mozilla.javascript.Token.GETELEM + " - " + targetBody);
			}
			else if (tt == org.mozilla.javascript.Token.LP) {
				System.out.println("====== " + org.mozilla.javascript.Token.LP + " - " + targetBody);
			}
			else if (tt == org.mozilla.javascript.Token.THIS) {
				System.out.println("====== " + org.mozilla.javascript.Token.THIS + " - " + targetBody);
			}
			else
				System.out.println("======");
		}
		if (newTarget != null) {
			newTarget.setLineno(node.getTarget().getLineno());
			node.setTarget(newTarget);
		}
		else {
			System.out.println("NEW TARGET NULL +++ " + node.getTarget());
		}
	}

	private void handleReturn(ReturnStatement node) {
		// return statements

		int lineNo = node.getLineno()+1;
		AstNode newRV;

		if (node.getReturnValue() != null) {
			// Wrap return value
//			newRV = parse("RSW("+ node.getReturnValue().toSource() + ", '" + node.getReturnValue().toSource()+ "' ," + lineNo +");");
			newRV = parse("RSW("+ node.getReturnValue().toSource() + ", '" + 'a' + "' ," + lineNo +");");
			newRV.setLineno(node.getReturnValue().getLineno());

		} else {
			// Return value is void
			newRV = parse("RSW(" + lineNo +")");
			newRV.setLineno(node.getLineno());
		}

		updateAllLineNo(newRV);
		node.setReturnValue(newRV);
	}
}
