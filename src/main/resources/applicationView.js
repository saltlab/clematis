/**
* Responsible for enabling recording functionality. The functions declared within this 
* JavaScript file can be called via a browser console (Firebug, etc.) in order to view the 
* the system's response to user actions. Currently, this information is printed to the 
* browser's console.
*   
* @author Saba Alimadadi
*         Sheldon Sequeira
*/

function enableControlPanel() {
// If the control panel is included, add listeners to the
// 'record' and 'stop' buttons
    if (document.getElementById("recordButton") != null) {
        document.getElementById("recordButton").addEventListener('click', startRecord);
    } 

    if (document.getElementById("stopButton") != null) {
        document.getElementById("stopButton").addEventListener('click', stopRecord);
    }
} 
document.addEventListener("DOMContentLoaded", enableControlPanel, false);

// Function which enables logging to console of DOM events, XmlHttpRequests etc.
// Can be called directly from console or by clicking the 'record' button
function startRecord() {
    if (recordStarted == true) return;
	console.log("====================================");
	console.log("RECORD STARTED");
	console.log("====================================");
	recordStarted = true;

	if (document.getElementById("recordButton") != null 
	  && document.getElementById("stopButton") != null) {
	    document.getElementById("recordButton").style.opacity = 0.5;
	    document.getElementById("stopButton").style.opacity = 1;
    }	
	// start the mutation summary observer
	startObserver();
	initializeValues();
}

// Function which halts logging to console of DOM events, XmlHttpRequests etc.
// Can be called directly from console or by clicking the 'stop' button
function stopRecord() {
	if(recordStarted == false) return;
	console.log("====================================");
    console.log("RECORD STOPPED");
	console.log("====================================");
	recordStarted = false;

	if (document.getElementById("recordButton") != null 
	  && document.getElementById("stopButton") != null) {
    	document.getElementById("recordButton").style.opacity = 1;
	    document.getElementById("stopButton").style.opacity = 0.5;	
    }
	// stop the mutation summary observer
	stopObserver();
}



