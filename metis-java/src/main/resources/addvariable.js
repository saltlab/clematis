window.xhr = new XMLHttpRequest();
window.buffer = new Array();

// Function Call Wrapper
function FCW() {
    if (arguments.length == 2) {
        send(new Array(document.location.href, ":::FUNCTION_CALL", arguments[0], arguments[1]));
    } else {
        send(new Array(document.location.href, ":::FUNCTION_CALL", arguments[1], arguments[2]));
    }
	return arguments[0];
}

// Return Statement Wrapper
function RSW() {
    if (arguments.length > 1) {
    // arguments[0] = value, arguments[1] = name, arguments[2] = lineno
        send(new Array(document.location.href, ":::RETURN_STATEMENT", new Array(addVariable(arguments[1], arguments[0])),arguments[2]));
    } else {
    // arguments[0] = lineno
        send(new Array(document.location.href, ":::RETURN_STATEMENT", new Array(addVariable("void", undefined)),arguments[0]));
    }
	return arguments[0];
}

function send(value) {
	window.buffer.push(value);
	if(window.buffer.length >= 50) {
		sendReally();	
	}
}

function sendReally() {
	window.xhr.open('POST', document.location.href + '?thisisafunctiontracingcall', false);
	window.xhr.send(JSON.stringify(window.buffer));
	window.buffer = new Array();
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
