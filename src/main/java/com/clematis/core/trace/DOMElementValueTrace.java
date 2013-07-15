package com.clematis.core.trace;

import org.json.JSONException;
import org.json.JSONObject;

public class DOMElementValueTrace extends TraceObject/* implements EpisodeSource */{
	private String elementId;
	private String elementType;
	private String nodeType;
	private String nodeName;
	private String oldValue;
	private String newValue;
	private String parentNodeValue;

	public DOMElementValueTrace() {
		super();
		setEpisodeSource(true);
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getParentNodeValue() {
		return parentNodeValue;
	}

	public void setParentNodeValue(String parentNodeValue) {
		this.parentNodeValue = parentNodeValue;
	}

	public JSONObject getValueChangeAsJSON() {
		JSONObject returnObject = new JSONObject();

		try {
			returnObject.put("elementId", this.elementId);
			// returnObject.put("elementType", this.elementType);
			// returnObject.put("nodeType", this.nodeType);
			// returnObject.put("nodeName", this.nodeName);
			returnObject.put("oldValue", this.oldValue);
			returnObject.put("newValue", this.newValue);
			returnObject.put("parentNodeValue", this.parentNodeValue);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnObject;
	}

}
