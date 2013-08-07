window.xhr = new XMLHttpRequest();
window.buffer = new Array();

var traceCounter = 0;

var recordButtonClicked = false;
var stopButtonClicked = false;

var recordingInProgress = false;//true;//////////false; // Can use this for determining if Clematis should be logging or not
///////var recordingInProgress = false; // Can use this for determining if Clematis should be logging or not
var myVar = 0;

var bookmarkPressed = false;

window.onload = function () {
	document.getElementById("recordButton").addEventListener('click', startRecording, false);
	document.getElementById("stopButton").addEventListener('click', stopRecording, false);
	document.getElementById("bookmarkButton").addEventListener('click', bookmark, false);
}


function bookmark(){
    bookmarkPressed = true;
/*    if (recordingInProgress) {
    	var date = Date.now();
    	send(JSON.stringify({messageType: "DOM_EVENT", timeStamp: date, eventType: "_BOOKMARK_", targetElement: "{\"attributes\":{\"id\":\"bookmarkButton\",\"name\":\"capture\",\"value\":\"BookMark\",\"src\":\"images/favoritos.png\",\"type\":\"image\",\"original-title\":\"BookMark\"},\"elementType\":\"INPUT\"}"}));
    }
*/	console.log("bookmarking");
}
function startRecording() {
	console.log("recording");
	if (recordButtonClicked == true)
		return;

    window.buffer = new Array();

	recordButtonClicked = true;
	stopButtonClicked = false;

	//document.getElementById("recordButton").style.opacity = 0.5;
	//document.getElementById("stopButton").style.opacity = 1;

	//document.getElementById("visualizationLinkContainer").innerHTML = "";

	captureButton.setAttribute("src","images/capture_green.png");

	//myVar = setInterval(function(){blink()},1900);
	
	// Recording has started
    sendRecordStart();
    
}

function stopRecording() {
	if (stopButtonClicked == true)
		return;
	
	stopButtonClicked = true;
	recordButtonClicked = false;

	//clearInterval(myVar);
	captureButton.setAttribute("src","images/capture.gif");
	//document.getElementById("recordButton").style.opacity = 1;
	//document.getElementById("stopButton").style.opacity = 0.5;
	
	//document.getElementById("visualizationLinkContainer").innerHTML = "<a href='file:///Users/sheldon/clematis/clematis-output/ftrace/sequence_diagrams/view.html' class='viewLink'>View Story</a>";

	// Recording has stopped
    sendRecordStop();
}

// Function Call Wrapper
function FCW() {
	var date = Date.now();
	
    if (arguments.length == 2) {
//    	console.log("Function call ++++++++++ " + arguments[0]);
        send(JSON.stringify({messageType: "FUNCTION_CALL", timeStamp: date, targetFunction: arguments[0], lineNo: arguments[1], counter: traceCounter++}));
    } else {
//    	console.log("Function call ++++++++++ " + arguments[1]);
        send(JSON.stringify({messageType: "FUNCTION_CALL", timeStamp: date, targetFunction: arguments[1], lineNo: arguments[2], counter: traceCounter++}));
    }
	return arguments[0];
}

// Return Statement Wrapper
function RSW() {

	var date = Date.now();

//	console.log("Function return ++++++++++ " + arguments[1]);

    if (arguments.length > 1) {
    // arguments[0] = value, arguments[1] = name, arguments[2] = lineno
//        send(JSON.stringify({messageType: "RETURN_STATEMENT", timeStamp: date, returnValue: {label: arguments[1], value: arguments[0]}, lineNo: arguments[2], counter: traceCounter++}));
    	// todo
    	/*********************/
        send(JSON.stringify({messageType: "RETURN_STATEMENT", timeStamp: date, label: arguments[1], value: arguments[0].toString(), lineNo: arguments[2], counter: traceCounter++}));
    } else {
    // arguments[0] = lineno
//        send(JSON.stringify({messageType: "RETURN_STATEMENT", timeStamp: date, returnValue: null, lineNo: arguments[0], counter: traceCounter++}));
    	// todo
    	/*********************/
        send(JSON.stringify({messageType: "RETURN_STATEMENT", timeStamp: date, label: null, value: null, lineNo: arguments[0], counter: traceCounter++}));
    }
	return arguments[0];
}

function getTimeStamp(date) {
   return {year: date.getUTCFullYear(),month: date.getUTCMonth(),day: date.getUTCDate(),hour: date.getUTCHours(),minute: date.getUTCMinutes(), second: date.getUTCSeconds(),ms: date.getUTCMilliseconds()};
}

function send(value) {

    // Only record when intended
    if (!recordingInProgress) return; 

	window.buffer.push(value);
}

function sendRecordStart(){
    recordingInProgress = true;
    window.xhr.open('POST', document.location.href + '?beginrecord', false);
    window.xhr.send();
}

function sendRecordStop(){
    sendReally();
    recordingInProgress = false;
    window.xhr.open('POST', document.location.href + '?stoprecord', false);
    window.xhr.send();
}

function sendReally() {
    if (window.buffer.length > 0) { 
    	window.xhr.open('POST', document.location.href + '?thisisafunctiontracingcall', false);
    	window.xhr.send('['+(window.buffer).toString()+']');
    	window.buffer = new Array();
    }
}
setInterval(sendReally, 8000);

function addVariable(name, value) {
	var pattern=/[.]attr[(]/;
	var getAttrPattern=/[.]getAttribute[(]/;
	if(typeof(value) == 'object') {
		if(value instanceof Array) {
				if(value[0] instanceof Array){
					
					if(value[0].length > 0) 
						//return new Array(name, typeof (value[0][0]) + '_array', value);
						return JSON.stringify({name: name, type: typeof (value[0][0]) + '_array', value: value});
				
					else
//						return new Array(name, 'object_array', value);
						return JSON.stringify({name: name, type: 'object_array', value: value});
				}
				else
					if(value.length > 0)
						//return new Array(name, typeof (value[0]) + '_array', value);
						return JSON.stringify({name: name, type: typeof (value[0]) + '_array', value: value});
					else 
						//return new Array(name, 'object_array', value);
						return JSON.stringify({name: name, type: 'object_array', value: value});
		}
	
	} else if(typeof(value) != 'undefined' && typeof(value) != 'function') {
	//	return new Array(name, typeof(value), value);
		return JSON.stringify({name: name, type: typeof(value), value: value});
	}
		else if (pattern.test(name) || getAttrPattern.test(name)){
		//	return new Array(name, 'string', value);//'java.lang.String');
		return JSON.stringify({name: name, type: 'string', value: value});
		}
	else if (name.match(pattern)==".attr("){
		//return new Array(name, 'string', 'java.lang.String');
		return JSON.stringify({name: name, type: 'string', value: 'java.lang.String'});
	}
	//return new Array(name, typeof(value), 'undefined');
	return JSON.stringify({name: name, type: typeof(value), value: 'undefined'});
}
