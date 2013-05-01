package com.clematis.core.trace;

public class XMLHttpRequestOpen extends XMLHttpRequestTrace {
	private String methodType;
	private String url;
	private boolean async;

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMethodType() {
		return methodType;
	}
	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}
	public boolean isAsync() {
		return async;
	}
	public void setAsync(boolean async) {
		this.async = async;
	}	
}
