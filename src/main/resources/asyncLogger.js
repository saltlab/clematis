/**
 * This code is responsible for logging the asynchronous JavaScript events and
 * the timing events. The first part of the file is dedicated to a logger class
 * that currently logs the events on the console. The screen can be viewed using
 * tools like Firebug. The second part of the file redefines the browser's
 * setTimeout method. The code logs the relevant information both when the
 * timeout is created and when the callback happens. The final part of this file
 * replaces the browser's XMLHTTPRequest object and its relevant functions. This
 * part of the code logs the information when the XHR object is created and
 * sent, as well as when the callback function is executed. Counters for
 * Timeouts and XHRs are included in the code, so that at any time during
 * execution the number of active timeouts and XHRs is known.
 * 
 * @Author Saba Alimadadi
 */

var recordStarted = true;// false;
var displayCountdown = false;

var xhrCounter = 0;
var timeoutCounter = 0;

var totalNumOfXhrs = 0;
var totalNumOfTimeouts = 0;


/**
 * The logger class is responsible for logging the information on the console,
 * based on the type of the event that has happened. This event could be a DOM
 * event, an asynchronous AJAX request, or a timing event. The logging happens
 * at a fine-grained level, meaning that each of the aforementioned events are
 * broken into meaningful atomic actions which are logged individually. E.g. a
 * timeout event consists of smaller actions such as creating the timeout and
 * execution of the callback function; each of those are logged separately.
 */
var logger = {}

var MsgConstants = {
	msgType_domEvent : 'DOM_EVENT',
	msgType_timeoutSet : 'TIMEOUT_SET',
	msgType_timeoutCallback : 'TIMEOUT_CALLBACK',
	msgType_xhrOpen : 'XHR_OPEN',
	msgType_xhrSend : 'XHR_SEND',
	msgType_xhrResponse : 'XHR_RESPONSE',
	msgType_domMutation : 'DOM_MUTATION',
	msgType_domElementValue : 'DOM_ELEMENT_VALUE',
	url : 'URL',
	year : 'YEAR',
	month : 'MONTH',
	day : 'DAY',
	hour : 'HOUR',
	minute : 'MINUTE',
	second : 'SECOND',
	millisecond : 'MILLISECOND',
	eventType : 'EVENT_TYPE',
	targetElement : 'TARGET_ELEMENT',
	handlerFunction : 'HANDLER_FUNCTION',
	id : 'ID',
	callbackFunction : 'CALLBACK_FUNCTION',
	delay : 'DELAY',
	arguments : 'ARGUMENTS',
	xhrMethod : 'XHR_METHOD',
	xhrUrl : 'XHR_URL',
	async : 'ASYNC',
	xhrPostMsg : 'XHR_POST_MSG',
	response : 'RESPONSE',
	mutationType: 'MUTATION_TYPE',
	data : 'DATA',
	nodeName : 'NODE_NAME',
	nodeType : 'NODE_TYPE',
	nodeValue : 'NODE_VALUE',
	parentNodeValue : 'PARENT_NODE_VALUE',
	elementId : 'ELEMENT_ID',
	elementType : 'ELEMENT_TYPE',
	oldValue : 'OLD_VALUE',
	newValue : 'NEW_VALUE'
	
}

/**
 * Prints the information related to creation of a timeout to the console
 */
logger.logSetTimeout = function(func, delay, params) {
	if (!recordingInProgress) return;
	
	if (!recordStarted)
		return;
	
    // todo todo
	if (delay == 0 || delay == null) {
//		console.log("+++++ Ignore timeout with delay = 0 +++++");
		func[totalNumOfTimeouts] = true;
		return;
	}

	console.log("------------------------------------");
	console.log("TIMEOUT: NEW");
	var date = Date.now();
	
	console.log("totalNumOfTimeouts: ", totalNumOfTimeouts);

	func.id = totalNumOfTimeouts;
	console.log(" + Timeout ID:", func.id);
	console.log(" + Callback function: ", func);
	console.log(" + Delay: ", delay);
//	var args = func.toString().match(/function\s+\w*\s*\((.*?)\)/)[1]
//			.split(/\s*,\s*/);
//	console.log(" + Function args: ", args);

	/*
	 * var allArgs = ''; for (int i = 0; i < args.length; i ++) allArgs =
	 * allArgs + '$' + args[i];
	 */
	console.log("Number of active timeouts: ", timeoutCounter);

/*    if (args.length == 0 || args[0] == "") {
    	alert("2");
       // No arguments (thrid parameter of setTimeout()
        send(JSON.stringify({messageType: "TIMEOUT_SET", timeStamp: date, id: func.id, callbackFunction: func.name, delay: delay, counter: traceCounter++}));
    } else {
    	alert("3");
       var argsJSONObject = "{";
        for(var ai=0; ai<args.length; ai++) {
            argsJSONObject += "\""+args[ai]+"\":"+JSON.stringify(params[ai])+","
        }
    	alert("4");

		// remove last comma 
		argsJSONObject = argsJSONObject.substring(0, argsJSONObject.length - 1);
        argsJSONObject += "}";

        send(JSON.stringify({messageType: "TIMEOUT_SET", timeStamp: date, id: func.id, callbackFunction: func.name, delay: delay, args: argsJSONObject, counter: traceCounter++}));
    }
*/
    send(JSON.stringify({messageType: "TIMEOUT_SET", timeStamp: date, id: func.id, callbackFunction: func.name, delay: delay, counter: traceCounter++}));
	checkValues();

};

/**
 * Prints the information related to execution of the callback function of a
 * timeout to the console.
 */
logger.logTimeoutCallback = function(func) {
	if (!recordingInProgress) return;

    if (!recordStarted)
		return;
    // todo todo
    if (func[totalNumOfTimeouts] == true) {
//    	console.log("++--- should ignore timeout callback ---++");
		func[totalNumOfTimeouts] = false;
    	return;
    }
    
    if (func.id == null) { // if settimeout was not captured but the callback is in the trace. assign an id to the callback
    	totalNumOfTimeouts ++;
    	func.id = totalNumOfTimeouts;
    }
    
	console.log("------------------------------------");
	console.log("TIMEOUT: CALLBACK");
	var date = Date.now();

	console.log(" + Timeout ID:", func.id);
	console.log(" + Callback function: ", func);
	console.log("Number of active timeouts: ", timeoutCounter);
	if (timeoutCounter == 0) {
		console.log("No more active timeouts. Total number of Timeouts: ",
				totalNumOfTimeouts);
		// The execution of all registered Timeouts is finished. Notify the
		// responsible unit.
	}

    send(JSON.stringify({messageType: "TIMEOUT_CALLBACK", timeStamp: date, id: func.id, callbackFunction: func.name, counter: traceCounter++}));
    checkValues();
    
    
    console.log(func.apply(null));
    
    console.log("end of timeout callback");
    
    
};

/**
 * Prints the information related to creation of a XMLHTTPRequest object to the
 * console
 */
logger.logXHROpen = function(xhr, method, url, async) {
	if (!recordingInProgress) return;

	if (!recordStarted)
		return;
	console.log("------------------------------------");
	console.log("XMLHTTPREQUEST: OPEN");
	var date = Date.now();

	console.log(" + XHR ID: ", xhr.id);
	console.log(" + Method: ", method);
	console.log(" + URL: ", url);
	console.log(" + Async: ", async);

    send(JSON.stringify({messageType: "XHR_OPEN", timeStamp: date, id: xhr.id, methodType: method, url: url, async: async, counter: traceCounter++}));

	checkValues();
};

/**
 * Prints the information related to sending a XHR object to server on the
 * console
 */
logger.logXHRSend = function(xhr, str) {
	if (!recordingInProgress) return;
//	else console.log("logXHRSend");

	if (!recordStarted)
		return;
	console.log("------------------------------------");
	console.log("XMLHTTPREQUEST: SEND");
	console.log(" + XHR ID: ", xhr.id);
	console.log(" + Message (POST):", str);
	var date = Date.now();

    //alert("xhr send");
    send(JSON.stringify({messageType: "XHR_SEND", timeStamp: date, id: xhr.id, message: str, counter: traceCounter++}));

	checkValues();
};

/**
 * Prints the information related to getting the response of a XHR object and
 * executing the callback function on the console
 */
logger.logXHRResponse = function(xhr) {
	if (!recordingInProgress) return;
//	else console.log("logXHRResponse");

	if (!recordStarted)
		return;
	console.log("------------------------------------");
	console.log("XMLHTTPREQUEST: RESPONSE");

	var date = Date.now();

	console.log(" + XHR ID: ", xhr.id);

	console.log(" + XHR callback function: ", xhr.onreadystatechange);
	console.log(" + XHR response headers: ", xhr.getAllResponseHeaders());
	console.log(" + XHR response: ", xhr.response);

	if (xhrCounter == 0) {
		var date = Date.now();

		// The execution of all registered XHRs is finished. Notify the
		// responsible unit.
	}
	
    //alert("xhr response");

    if (xhr.onreadystatechange != null) {
        send(JSON.stringify({messageType: "XHR_RESPONSE", timeStamp: date, id: xhr.id, callbackFunction: xhr.onreadystatechange.name, response: xhr.response, counter: traceCounter++}));
    } else if (xhr.onload != null) {
        send(JSON.stringify({messageType: "XHR_RESPONSE", timeStamp: date, id: xhr.id, callbackFunction: xhr.onload.name, response: xhr.response, counter: traceCounter++}));
    } else {
        send(JSON.stringify({messageType: "XHR_RESPONSE", timeStamp: date, id: xhr.id, callbackFunction: "", response: xhr.response, counter: traceCounter++}));
	}
    checkValues();
};

/**
 * Prints the information related to a DOM event on the console
 */
logger.logDOMEvent = function(type, targetEl, callback) {
	if (!recordingInProgress) return;
//	else console.log("logDOMEvent");

	var jml;
	console.log("------------------------------------");
	console.log("DOM EVENT HANDLED");

	//if (!recordStarted || arguments[0].toString().indexOf("webdriver-evaluate") >= 0)
	if (!recordStarted)
		return;
	
	// todo /******************/
	/*
	var eventType = arguments[0];
	if (eventType == "mouseover" || eventType == "mousemove" || eventType == "mouseout") {
		console.log(eventType, " not logged");
		return;
	}
	*/
	// todo /******************/
	/*
	console.log("------------------------------------");
	console.log("DOM EVENT HANDLED");
*/	var date = Date.now();

	console.log(" + Event type: ", arguments[0]);
	console.log(" + Target DOM element: ", arguments[1]);
	console.log(" + Handler function: ", arguments[2]);

    jml = JsonML.fromHTML(arguments[1]);
	//if (jml && recordingInProgress == true) {
	if (jml) {
		jml = JSON.stringify(jml);
	    //alert("dom event");
    	send(JSON.stringify({messageType: "DOM_EVENT", timeStamp: date, eventType: arguments[0], eventHandler: callback.name, targetElement: jml,counter: traceCounter++}));
    	console.log("-----------------------------------");
    	console.log(JSON.stringify({messageType: "DOM_EVENT", timeStamp: date, eventType: arguments[0], eventHandler: callback.name, targetElement: jml,counter: traceCounter++}));
    	console.log("-----------------------------------");
      
	}
	checkValues();
};

/**
 * Prints the contents of the DOM Mutation array and empties the array
 */
logger.logDOMMutation = function() {
	if (!recordingInProgress) return;
//	else console.log("logDOMMutation");
	
	// Loop through the array of summaries
	for (var i=0; i<mutationArray.length; i++) {
		var removed, added;
		var date = mutationArray[i].date;

		// Loop through the array of removed nodes in this summary
		for (var j=0; j<mutationArray[i].removed.length; j++){
			removed = mutationArray[i].removed[j];
			if (typeof(removed) !== 'undefined' && removed != null) {
				if (removed.nodeName == "#text"){
					// TODO APRIL2013 - add other cases to this to cover types other than #text
					// The following line will set the parent node value to be only the first two elements of the JSON parent node array (this should be the type and the ID)
					removed.parentNodeValue = JSON.stringify(removed.parentNodeValue[0]) + JSON.stringify(removed.parentNodeValue[1]);
					send(JSON.stringify({messageType: "DOM_MUTATION", timeStamp: date, mutationType: "removed", data: removed.data, nodeName: removed.nodeName, nodeType: removed.nodeType, nodeValue: removed.nodeValue, parentNodeValue: removed.parentNodeValue, counter: traceCounter++}));
				} else {
					// To stringify the entire parentNodeValue (this will be the FULL JSON_ML string)
					removed.parentNodeValue = JSON.stringify(removed.parentNodeValue);
					send(JSON.stringify({messageType: "DOM_MUTATION", timeStamp: date, mutationType: "removed", data: removed.data, nodeName: removed.nodeName, nodeType: removed.nodeType, nodeValue: removed.nodeValue, parentNodeValue: removed.parentNodeValue, counter: traceCounter++}));
				}
				}
		}

		// Loop through the array of added nodes in this summary
		for (var k=0; k<mutationArray[i].added.length; k++){
			added = mutationArray[i].added[k];
			if (typeof(added) !== 'undefined' && added != null) {
				if (added.nodeName == "#text"){
					// TODO APRIL2013 - add other cases to this to cover types other than #text
					// The following line will set the parent node value to be only the first two elements of the JSON parent node array (this should be the type and the ID)
					added.parentNodeValue = JSON.stringify(added.parentNodeValue[0]) + JSON.stringify(added.parentNodeValue[1]);
					send(JSON.stringify({messageType: "DOM_MUTATION", timeStamp: date, mutationType: "added", data: added.data, nodeName: added.nodeName, nodeType: added.nodeType, nodeValue: added.nodeValue, parentNodeValue: added.parentNodeValue, counter: traceCounter++}));
				} else {
					// To stringify the entire parentNodeValue (this will be the FULL JSON_ML string)
					added.parentNodeValue = JSON.stringify(added.parentNodeValue);
					send(JSON.stringify({messageType: "DOM_MUTATION", timeStamp: date, mutationType: "added", data: added.data, nodeName: added.nodeName, nodeType: added.nodeType, nodeValue: added.nodeValue, parentNodeValue: added.parentNodeValue, counter: traceCounter++}));
				}

			}
		}
	    //alert("dom mutation");


	}		
	//	Reset the array of summaries
	mutationArray.length = 0;
	// Check for element value changes
	checkValues();
};

/**
 * Prints a summary of an element with a changed value
 */
	logger.logElementValueChange = function(changedElem, oldVal, newVal, parent) {
		if (!recordingInProgress) return;
//		else console.log("logElementValueChange");
	
		var date = Date.now();	
		var id = "null";
		var type = "null";
		var nodeType = "null";
		var nodeName = "null";
		
		/*if (typeof(changedElem.id) !== 'undefined' && changedElem.id != null) {
			id = changedElem.id;
		}
		
		if (typeof(changedElem.type) !== 'undefined' && changedElem.type != null) {
			type = changedElem.type;
		}
		
		if (typeof(changedElem.nodeType) !== 'undefined' && changedElem.nodeType != null) {
			nodeType = changedElem.nodeType;
		}
		
		if (typeof(changedElem.nodeName) !== 'undefined' && changedElem.nodeName != null) {
			nodeName = changedElem.nodeName;
		}*/
		
		// TODO APRIL2013 - If INPUT, parse the first 3 elements of changedElem, if SUBMIT type take the first 2 elements, etc.
		// If we only want PART of the element, here we would only stringify certain parts of changedElem i.e. stringify(changeElem[0]) + stringify(changedElem[1])
		changedElem = JSON.stringify(changedElem);
		
		if (type != "text") {
			parent = "N/A";
		} else {
			// If we only want PART of the parent, here we would only stringify certain parts of parent i.e. stringify(parent[0]) + stringify(parent[1])
			parent - JSON.stringify(parent)
		}
		
    	//send(JSON.stringify({messageType: "DOM_ELEMENT_VALUE", timeStamp: date, elementId: id, elementType: type, nodeType: nodeType, nodeName: nodeName, oldValue: oldVal, newValue: newVal, counter: traceCounter++}));
    	send(JSON.stringify({messageType: "DOM_ELEMENT_VALUE", timeStamp: date, elementId: changedElem, oldValue: oldVal, newValue: newVal, parentNodeValue: parent, counter: traceCounter++}));
	
};

/*******************************************************************************
 * ** *** ** TIMEOUTS *** ** ***
 ******************************************************************************/


// Keep the current setTimeout function
window.oldSetTimeout = window.setTimeout;

// Redefine setTimeout
window.setTimeout = function(func, delay, params) {
	// Increase the number of active timeouts
	timeoutCounter++;
	totalNumOfTimeouts++;

///////////////////////////////	var timeoutArgs = Array.prototype.slice.call(arguments, 2);
	var timeoutArgs = null;

	// Log the creation of the timeout
	logger.logSetTimeout(func, delay, timeoutArgs);


	// Call the original timeout after logging
	window.oldSetTimeout(function(/* params */) {
		try {
			logger.logTimeoutCallback(func);

//////////////////////////			func.apply(null, timeoutArgs);
			func.apply(null);
//////////////////////////			
			timeoutCounter--;

		} catch (exception) {
//			alert("Timeout exception");
		}
	}, delay);
};

/*******************************************************************************
 * ** *** ** XMLHTTPREQUEST *** ** ***
 ******************************************************************************/

// Store the original XMLHTTPRequest object
var _XMLHttpRequest = XMLHttpRequest;
// Redefine XHR
XMLHttpRequest = function() {
	xhrCounter++;
	totalNumOfXhrs++;

	var xhr = new _XMLHttpRequest();
	// var xhr = new _XMLHttpRequest();

	xhr.id = totalNumOfXhrs;

	// Replace the function for opening a new request
	var _open = xhr.open;
	xhr.open = function(method, url, async) {
		logger.logXHROpen(xhr, method, url, async);
		return _open.apply(this, [ method, url, async ]);

	}

	// Replace the function for sending the previously created request
	var _send = xhr.send;
	xhr.send = function(str) {
		logger.logXHRSend(xhr, str);
		// The value of OnLoad declares when the response is back. Call the
		// logger function when the response is ready which cause the callback
		// function to be executed as well.
		return _send.apply(this, [ str ]);
	}
	var onreadystatechange = function() {
    
		if (this.readyState==4) {
			xhrCounter--;
			logger.logXHRResponse(this);
		}
	}
	var onload = function() {
		xhrCounter--;

		logger.logXHRResponse(this);
	}

	//xhr.addEventListener("load", onload, false);
	xhr.addEventListener("readystatechange", onreadystatechange, false);

	return xhr;
}
