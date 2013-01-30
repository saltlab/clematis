window.xhr = new XMLHttpRequest();
window.buffer = new Array();

// Function Call Wrapper
function FCW() {
    if ((arguments[0].name == "") || (arguments[0].name == undefined) || (arguments[0].name == null)) {
        if (arguments[0].toString().indexOf("function") == -1) {
            send('<functionCall name='+arguments[0].toString()+' line='+arguments[1]+'>');
              send('<params>');
              send('<param name=\'-\'></param>')
              send('</params>');
            send('</functionCall>'+ 'endofline');
        } else {
            send('<functionCall name=\'anonymous\' line='+arguments[1]+'>');
              send('<params>');
              send('<param name=\'-\'></param>')
              send('</params>');
            send('</functionCall>'+ 'endofline');
        }
    } else {
        send('<functionCall name='+arguments[0].name+' line='+arguments[1]+'>');
          send('<params>');
          send('<param name=\'-\'></param>')
          send('</params>');
        send('</functionCall>'+ 'endofline');
    }
	return arguments[0];
}

// Return Statement Wrapper
function RSW() {
    send('<returnStatement value='+arguments[0]+'>');
      send('<function name='+arguments[2]+'>');
    send('</returnStatement>'+ 'endofline');
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
