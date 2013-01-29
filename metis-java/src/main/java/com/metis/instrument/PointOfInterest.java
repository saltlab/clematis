package com.metis.instrument;

import org.mozilla.javascript.Context;

public class PointOfInterest {

	private String name = "";
	private String body = "";
	private int type = -1;
	private int[] range = {0,0};
	private int hashCode = -1;
    private int lineNo = -1;
	
	// The zero-argument constructor used by Rhino runtime to create instances
	public PointOfInterest(Object[] args) { 
		for (int i=0; i<args.length; i++) {
			switch(i){
			// Name
			case 0:
				this.setName(Context.toString(args[i]));
				break;
			// Type
			case 1:
				this.setType(Integer.parseInt(Context.toString(args[i])));
				break;
			// Range
			case 2:
				this.setBegin(Integer.parseInt(Context.toString(args[i])));
				break;
			// 
			case 3:
				this.setEnd(Integer.parseInt(Context.toString(args[i])));
				break;	
			case 4:
				this.setLineNo(Integer.parseInt(Context.toString(args[i])));
				break;
			case 5:
				this.setBody(Context.toString(args[i]));
				break;
			case 6:
				this.setHash(Integer.parseInt(Context.toString(args[i])));
			default:

			}
		}
	}

	public String getClassName() { return "Entry"; }

	public String toString() {
		switch (this.type) {
		case org.mozilla.javascript.Token.FUNCTION: 
			if (this.getEnd() == -1) {
				// Function Beginning
				return "send(" + "\"Executing function: " + getName() + "endofline\");";
			} else if (this.getEnd() == -2) {
				// Function End
				return ";send(" + "\"Exiting function: " + getName() + "endofline\");";
			} else {
				// General Function
				return "";
			}
		case org.mozilla.javascript.Token.CALL: 
			if (this.getEnd() == -1) {
				// Function Call Beginning				
				return "functionCallWrapper(";
			} else if (this.getEnd() == -2) {
				// Function Call End
				return ",\"Calling \" + \"" + this.getName() +"\""+")";
			}
		case org.mozilla.javascript.Token.RETURN: 
			if (this.getEnd() == -1) {
				// Function Beginning
				return " RSW(";
			} else if (this.getEnd() == -2) {
				// Function End
				return ")";
			} else {
				// General Function
				return "";
			}		default: 
			return "";
		}

	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setHash(int hashCode) {
		this.hashCode = hashCode;
	}

	public int getHash() {
		return hashCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int[] getRange() {
		return range;
	}	

	public int getLineNo() {
		return lineNo;
	}	
	
	public void setLineNo(int lineNumber) {
		this.lineNo = lineNumber;
	}

	
	public void setBegin(int begin) {
		this.range[0] = begin;
	}

	public void setEnd(int end) {
		this.range[1] = end;
	}

	public int getBegin() {
		return range[0];
	}
	
	public int getEnd() {
		return range[1];
	}

}
