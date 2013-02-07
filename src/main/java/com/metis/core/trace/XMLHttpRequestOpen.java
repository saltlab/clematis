package com.metis.core.trace;

public class XMLHttpRequestOpen extends XMLHttpRequestTrace {
	private String methodType;
	private String serverUrl;
	private boolean async;

	public String getMethodType() {
		return methodType;
	}
	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}
	public String getServerUrl() {
		return serverUrl;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	public boolean isAsync() {
		return async;
	}
	public void setAsync(boolean async) {
		this.async = async;
	}	
}
