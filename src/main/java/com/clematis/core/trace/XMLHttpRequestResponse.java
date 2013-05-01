package com.clematis.core.trace;

public class XMLHttpRequestResponse extends XMLHttpRequestTrace/* implements EpisodeSource */{
	private String callbackFunction; // Function type ?
	private String response;

	public XMLHttpRequestResponse() {
		super();
		setEpisodeSource(true);
	}
	public String getCallbackFunction() {
		return callbackFunction;
	}
	public void setCallbackFunction(String callbackFunction) {
		this.callbackFunction = callbackFunction;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response.replace("\n", "").replace("\r", "");
	}	
}
