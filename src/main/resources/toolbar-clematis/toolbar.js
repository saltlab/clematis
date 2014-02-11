function loadToolbar() {	
	
	var capturing = false;
	var myVar = 0;
	var div = document.createElement("div");
	var lineBreak = document.createElement('br');
	var tbl = document.createElement("table");
	var tblBody = document.createElement("tbody");
	
	//div.style.border = "2px solid blue";
	div.style.width = "84px";

	var textBox = document.createTextNode("Clematis");
	

	

	//Create the Capture button and configure its attributes
    var captureButton = document.createElement("input");
    captureButton.setAttribute("type", "image");
   	captureButton.src="/images-clematis/capture.gif";
   	captureButton.setAttribute("id", "recordButton");
    captureButton.setAttribute("value", "Capture");
    captureButton.setAttribute("name", "capture");
    captureButton.setAttribute("title","Start Capturing");
  	captureButton.onclick = capture;
  	captureButton.onmouseover=showtext;
  	captureButton.style.overflow="scroll";
  	$(captureButton).scroll(capture);
  	
  	function showtext(text){
		$(captureButton).tipsy({gravity:'nw'});
  	}
  	 
    //Create the Stop button and configure its attributes
    var stopButton = document.createElement("input");
    stopButton.setAttribute("type", "image");
    stopButton.setAttribute("src","/images-clematis/stop.png");
    stopButton.setAttribute("id", "stopButton");
    stopButton.setAttribute("value", "Stop");
    stopButton.setAttribute("name", "capture");
    stopButton.setAttribute("title","Stop Capturing");
    stopButton.onclick=stop;
    stopButton.onmouseover=showtext2;

     var bookmarkButton = document.createElement("input");
    bookmarkButton.setAttribute("type", "image");
    bookmarkButton.setAttribute("src","/images-clematis/favoritos.png");
    bookmarkButton.setAttribute("id", "bookmarkButton");
    bookmarkButton.setAttribute("value", "BookMark");
    bookmarkButton.setAttribute("name", "capture");
    bookmarkButton.setAttribute("title","BookMark");
    
    // bookmarkButton.onmouseover=showtext2;
    //$(bookmarkButtons).tipsy({gravity:'nw'});



    
    function showtext2(){
  		$(stopButton).tipsy({gravity:'nw'});
  		$(bookmarkButton).tipsy({gravity:'nw'});
  	}


	//Populate the table with the 2 buttons created above
	var row = document.createElement("tr");
	
    var cell = document.createElement("td");
    var cell2= document.createElement("td");
    var cell3= document.createElement("td");
    
    cell.appendChild(captureButton);
    cell2.appendChild(stopButton);
    cell3.appendChild(bookmarkButton);
    
    row.appendChild(cell);
    row.appendChild(cell2);
    row.appendChild(cell3);
    
    tblBody.appendChild(row);
    
    
    tbl.appendChild(tblBody);
    tbl.setAttribute("border","1");
    
	
	//dummy functions to model capturing and stopping

	
	function capture() {
			capturing=true;
			captureButton.setAttribute("src","/images-clematis/capture_green.png");
			myVar = setInterval(function(){blink()},1900);
	
		}
	

	
	function stop() { 
		clearInterval(myVar);
		captureButton.setAttribute("src","/images-clematis/capture.gif");
		capturing=false;

		}
		
	//function to create blinking effect for the captureButton	
	function blink(){
		$(captureButton).fadeIn(350).fadeOut(350).fadeIn(350);
	}
	
	  div.appendChild(tbl);
      $(div).dialog({ height: 100, width: 163, title:"Clematis", show:"slow", minWidth: 110 , minHeight:0  });
    //document.body.appendChild(div);
    
    document.getElementById("recordButton").addEventListener('click', startRecording, false);
document.getElementById("stopButton").addEventListener('click', stopRecording, false);
}

$(document).ready(loadToolbar);

$(window).bind('beforeunload', sendReally);

function resumeRecording(previousCounterLeftOff) {
	traceCounter = previousCounterLeftOff || 0;
    window.buffer = new Array();
	console.log("resuming");
	recordButtonClicked = true;
	stopButtonClicked = false;
	recordingInProgress = true;
	$(document).ready(function(){
		document.getElementById('recordButton').setAttribute("src","/images-clematis/capture_green.png");
	});	
    
    var jml;
	var date = Date.now();

    jml = JsonML.fromHTML(document);
	if (jml) {
		jml = JSON.stringify(jml);
        send(JSON.stringify({messageType: "DOM_EVENT", timeStamp: date, eventType: 'pageload', eventHandler: undefined, targetElement: jml,counter: traceCounter++}));
    }
}
