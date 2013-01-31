window.xhr = new XMLHttpRequest();
window.buffer = new Array();

// Function Call Wrapper
function FCW() {
    if ((arguments[0].name == "") || (arguments[0].name == undefined) || (arguments[0].name == null)) {
        if (arguments[0].toString().indexOf("function") == -1) {
            send(new Array('\'' + document.location.href + arguments[1] + '\'', '\''+arguments[0].toString()+'\''));
        } else {
            send(new Array('\'' + document.location.href + arguments[1] + '\'', 'anonymous'));
        }
    } else {
            send(new Array('\'' + document.location.href + arguments[1] + '\'', '\''+arguments[0].name+'\''));
    }
//    send(new Array('\'HELLO\'', '\'' + document.location.href + arguments[1] + '\''));
	return arguments[0];
}

// Return Statement Wrapper
function RSW() {
    send(new Array(document.location.href, arguments[0], arguments[1]));
	return arguments[0];
}

function send(value) {
	window.buffer.push(value);
	if(window.buffer.length == 200) {
		sendReally();	
	}
}

function sendReally() {
	window.xhr.open('POST', document.location.href + '?thisisafunctiontracingcall', false);
	//window.xhr.send(window.buffer.toString());
	window.xhr.send(JSON.stringify(window.buffer));
	window.buffer = new Array();
}
