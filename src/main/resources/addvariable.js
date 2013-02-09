window.xhr = new XMLHttpRequest();
window.buffer = new Array();

var traceCounter = 0;

// Function Call Wrapper
function FCW() {
	var date = Date.now();

    if (arguments.length == 2) {
        send(JSON.stringify({messageType: "FUNCTION_CALL", timeStamp: date, targetFunction: arguments[0], lineNo: arguments[1], counter: traceCounter++}));
    } else {
        send(JSON.stringify({messageType: "FUNCTION_CALL", timeStamp: date, targetFunction: arguments[1], lineNo: arguments[2], counter: traceCounter++}));
    }
	return arguments[0];
}

// Return Statement Wrapper
function RSW() {

	var date = Date.now();

    if (arguments.length > 1) {
    // arguments[0] = value, arguments[1] = name, arguments[2] = lineno
        send(JSON.stringify({messageType: "RETURN_STATEMENT", timeStamp: date, returnValue: new Array(arguments[1], arguments[0]), lineNo: arguments[2], counter: traceCounter++}));
    } else {
    // arguments[0] = lineno
        send(JSON.stringify({messageType: "RETURN_STATEMENT", timeStamp: date, returnValue: new Array(null, null), lineNo: arguments[0], counter: traceCounter++}));
    }
	return arguments[0];
}

function getTimeStamp(date) {
   return {year: date.getUTCFullYear(),month: date.getUTCMonth(),day: date.getUTCDate(),hour: date.getUTCHours(),minute: date.getUTCMinutes(), second: date.getUTCSeconds(),ms: date.getUTCMilliseconds()};
}

function send(value) {
	window.buffer.push(value);
	if(window.buffer.length >= 50) {
		sendReally();	
	}
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
