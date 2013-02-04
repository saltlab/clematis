window.xhr = new XMLHttpRequest();
window.buffer = new Array();

// Function Call Wrapper
function FCW() {
	var date = new Date();

    if (arguments.length == 2) {
        send(JSON.stringify({messageType: "FUNCTION_CALL", timeStamp: getTimeStamp(date), targetFunction: arguments[0], lineNo: arguments[1]}));
    } else {
        send(JSON.stringify({messageType: "FUNCTION_CALL", timeStamp: getTimeStamp(date), targetFunction: arguments[1], lineNo: arguments[2]}));
    }
	return arguments[0];
}

// Return Statement Wrapper
function RSW() {

	var date = new Date();

    if (arguments.length > 1) {
    // arguments[0] = value, arguments[1] = name, arguments[2] = lineno
        send(JSON.stringify({messageType: "RETURN_STATEMENT", timeStamp: getTimeStamp(date), returnValue: new Array(arguments[1], arguments[0]), lineNo: arguments[2]}));
    } else {
    // arguments[0] = lineno
        send(JSON.stringify({messageType: "RETURN_STATEMENT", timeStamp: getTimeStamp(date), returnValue: new Array(null, null), lineNo: arguments[0]}));
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

function addVariable(name, value) {
	var pattern=/[.]attr[(]/;
	var getAttrPattern=/[.]getAttribute[(]/;
	if(typeof(value) == 'object') {
		if(value instanceof Array) {
				if(value[0] instanceof Array){
					
					if(value[0].length > 0) 
						return new Array(name, typeof (value[0][0]) + '_array', value);
				
					else
						return new Array(name, 'object_array', value);
				}
				else
					if(value.length > 0)
						return new Array(name, typeof (value[0]) + '_array', value);
					else 
						return new Array(name, 'object_array', value);
		}
	
	} else if(typeof(value) != 'undefined' && typeof(value) != 'function') {
		return new Array(name, typeof(value), value);
	}
		else if (pattern.test(name) || getAttrPattern.test(name)){
			return new Array(name, 'string', value);//'java.lang.String');
		}
	else if (name.match(pattern)==".attr("){
		return new Array(name, 'string', 'java.lang.String');
	}
	return new Array(name, typeof(value), 'undefined');
}
