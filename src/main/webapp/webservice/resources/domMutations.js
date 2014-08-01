/**
 * This code is responsible for recording mutations to the DOM.
 * The first part of the file sets up a mutation summary observer using the Mutation Summary library
 * The second part of the file handles the summaries in an array so they can be output to the logger
 * The third part of the file watches document elements for changes in values
 * @Author Ryan Randhawa
 */

var observerSummary;
var mutationArray = new Array();

window.addEventListener("DOMContentLoaded", function() {
	// Once the DOM is loaded, start the mutation summary observer and initialize the arrays for ElementValueChange 
	startObserver();
	initializeValues();
}, false);
	
// Start the mutation summary observer (using the mutation summary library)
function startObserver() {
	observerSummary = new MutationSummary({
	  callback: handleSummary,
	  queries: [ { all: true } ]
	});
}

// Disconnect the mutation summary observer
function stopObserver() {
	// When we disconnect the observer, the summaries will be stored here
	var summaries = observerSummary.disconnect();
	if (summaries)
	  handleSummaryLeftover(summaries);
}

/*
 * 
 */
//Add the added and removed summaries to the mutationArray
function handleSummary(summaries) {
	for (var s=0; s<summaries.length; s++){

		var added = new Array();
		var removed = new Array();

		// Loop through all of the element in the removed array
		for (var i=0; i<summaries[s].added.length; i++){
			// Set all variables to be null by default, then assign the real value if it is valid
			var data = "null";
			var nodeType = "null";
			var nodeName = "null";
			var nodeValue = "null";
			var parentNodeValue = "null";

			// If there is data, set the data field.
			if (typeof(summaries[s].added[i].data) !== 'undefined' && summaries[s].added[i].data != null) {
				data = summaries[s].added[i].data;
			}

			if (typeof(summaries[s].added[i].nodeType) !== 'undefined' && summaries[s].added[i].nodeType != null) {
				nodeType = summaries[s].added[i].nodeType;
			}

			if (typeof(summaries[s].added[i].nodeName) !== 'undefined' && summaries[s].added[i].nodeName != null) {
				nodeName = summaries[s].added[i].nodeName;
			}

			if (typeof(summaries[s].added[i].nodeValue) !== 'undefined' && summaries[s].added[i].nodeValue != null) {
				nodeValue = summaries[s].added[i].nodeValue;
			}

			if (typeof(summaries[s].added[i].parentElement) !== 'undefined' && summaries[s].added[i].parentElement != null) {
				var jml = JsonML.fromHTML(summaries[s].added[i].parentElement);
				if (jml) {
					parentNodeValue = jml;
				}
			}
			
			added.push({data: data, nodeType: nodeType, nodeName: nodeName, nodeValue: nodeValue, parentNodeValue: parentNodeValue});	
		}

		// Loop through all of the element in the removed array
		for (var i=0; i<summaries[s].removed.length; i++){
			var data = "null";
			var nodeType = "null";
			var nodeName = "null";
			var nodeValue = "null";
			var parentNodeValue = "null";

			if (typeof(summaries[s].removed[i].data) !== 'undefined' && summaries[s].removed[i].data != null) {
				data = summaries[s].removed[i].data;
			}

			if (typeof(summaries[s].removed[i].nodeType) !== 'undefined' && summaries[s].removed[i].nodeType != null) {
				nodeType = summaries[s].removed[i].nodeType;
			}

			if (typeof(summaries[s].removed[i].nodeName) !== 'undefined' && summaries[s].removed[i].nodeName != null) {
				nodeName = summaries[s].removed[i].nodeName;
			}

			if (typeof(summaries[s].removed[i].nodeValue) !== 'undefined' && summaries[s].removed[i].nodeValue != null) {
				nodeValue = summaries[s].removed[i].nodeValue;
			}

			if (typeof(summaries[s].getOldParentNode(summaries[s].removed[i])) !== 'undefined' && summaries[s].getOldParentNode(summaries[s].removed[i]) != null) {
				var jml = JsonML.fromHTML(summaries[s].getOldParentNode(summaries[s].removed[i]));
				if (jml) {
					parentNodeValue = jml;
				}
			}
			
			removed.push({data: data, nodeType: nodeType, nodeName: nodeName, nodeValue: nodeValue, parentNodeValue: parentNodeValue});	
		}

		// Push the new added and removed summaries to the mutation array and call the logger
		var mutation = { date : Date.now(), added: added, removed: removed };
		mutationArray.push(mutation);
	}
	logger.logDOMMutation();
}

// Functions to call after the Mutation Summary Observer is disconnected
function handleSummaryLeftover(summaries) {
	// call the original summary handler function
	handleSummary(summaries);
	// check for changed element values
	checkValues();
}

var all = []; 
var allElements
var oldValues = new Array();

/*
 * Create an array of each element in the document that has a value attribute
 * Create an array with the initial values of each element (when this method was called)
 */
function initializeValues() {
	allElements = document.getElementsByTagName("*");
	
	for (var i=0, max=allElements.length; i < max; i++) {
		if (allElements[i].id) {
			var tempElem = document.getElementById(allElements[i].id);
			// Only track the elements that have a value attribute
			if (tempElem.value != undefined) {
				all.push(tempElem);
				oldValues.push(tempElem.value);
			}
		}
	}
}

/* 
 * Future functionality could allow us to add listeners to each element and then 
 * only call checkValue() if there was a change to one of the elements. Right now
 * the listeners will not work for all value changes (won't work on text boxes)
 */
/*function addValueListeners() {
	for (var i=0; i < all.length; i+=1) { 
		 
	    (function (i) { 
	 
	        all[i].onchange = function () { 
	 
	             //checkValue(i); 
	             checkValues();
	        }; 
	    }) (i); 
	}
}

//Check elements for a change in value. Log all changes.
function checkValue(i) {
		logger.logElementValueChange(all[i], oldValues[i], all[i].value);
		oldValues[i] = all[i].value;
	
}*/

/*
 * Checks all elements with a value attribute for a change in value.
 * Compares all.value (the new values) with oldValues. Logs a change if there is one.
 */
function checkValues() {
	for (var i=0, max=all.length; i < max; i++) {
		if (all[i].value != oldValues[i]) {
			var element;
			var parent;
			var jml = JsonML.fromHTML(all[i]);
			if (jml) {
				element = jml;
			} else {
				element = "null";
			}
			
			var jml2 = JsonML.fromHTML(all[i].parentElement);
			if (jml2) {
				parent = jml2;
			} else {
				parent = "null";
			}

			logger.logElementValueChange(element, oldValues[i], all[i].value, parent);
			oldValues[i] = all[i].value;
		}	
	}
}
