window.xhr = new XMLHttpRequest();
window.buffer = new Array();

// Function Call Wrapper
function FCW() {
    if ((arguments[0].name == "") || (arguments[0].name == undefined) || (arguments[0].name == null)) {
        send('Calling anonymous function from line ' +  arguments[1] + 'endofline');
    } else {
	    send('Calling ' + arguments[0].name + 'from line ' +  arguments[1] + 'endofline');
    }
	return arguments[0];
}

// Return Statement Wrapper
function RSW() {
	send('Returning value: ' + arguments[0] + 'endofline');
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
	window.xhr.send(window.buffer.toString());
	window.buffer = new Array();
}
