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
	//addValueListeners();
}, false);
	
// Start the mutation summary observer
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
	
//Add the new summary to the mutationArray
function handleSummary(summaries) {

	var added, removed;
	if (summaries[0].added.length>0) {
		var data = "null";
		var nodeType = "null";
		var nodeName = "null";
		var nodeValue = "null";
		var parentNodeValue = "null";

		if (typeof(summaries[0].added[0].data) !== 'undefined' && summaries[0].added[0].data != null) {
			data = summaries[0].added[0].data;
		}

		if (typeof(summaries[0].added[0].nodeType) !== 'undefined' && summaries[0].added[0].nodeType != null) {
			nodeType = summaries[0].added[0].nodeType;
		}

		if (typeof(summaries[0].added[0].nodeName) !== 'undefined' && summaries[0].added[0].nodeName != null) {
			nodeName = summaries[0].added[0].nodeName;
		}

		if (typeof(summaries[0].added[0].nodeValue) !== 'undefined' && summaries[0].added[0].nodeValue != null) {
			nodeValue = summaries[0].added[0].nodeValue;
		}

		if (typeof(summaries[0].added[0].parentElement) !== 'undefined' && summaries[0].added[0].parentElement != null) {
			if (summaries[0].added[0].parentElement.attributes.length > 0){
				if (typeof(summaries[0].added[0].parentElement.attributes[0].nodeValue) !== 'undefined' && summaries[0].added[0].parentElement.attributes[0].nodeValue != null) {
					parentNodeValue = summaries[0].added[0].parentElement.attributes[0].nodeValue;
				}
			}
		}
		added = {data: data, nodeType: nodeType, nodeName: nodeName, nodeValue: nodeValue, parentNodeValue: parentNodeValue};	
	}

	if (summaries[0].removed.length>0) {
		var data = "null";
		var nodeType = "null";
		var nodeName = "null";
		var nodeValue = "null";
		var parentNodeValue = "null";

		if (typeof(summaries[0].removed[0].data) !== 'undefined' && summaries[0].removed[0].data != null) {
			data = summaries[0].removed[0].data;
		}

		if (typeof(summaries[0].removed[0].nodeType) !== 'undefined' && summaries[0].removed[0].nodeType != null) {
			nodeType = summaries[0].removed[0].nodeType;
		}

		if (typeof(summaries[0].removed[0].nodeName) !== 'undefined' && summaries[0].removed[0].nodeName != null) {
			nodeName = summaries[0].removed[0].nodeName;
		}

		if (typeof(summaries[0].removed[0].nodeValue) !== 'undefined' && summaries[0].removed[0].nodeValue != null) {
			nodeValue = summaries[0].removed[0].nodeValue;
		}

		if (typeof(summaries[0].removed[0].parentElement) !== 'undefined' && summaries[0].removed[0].parentElement != null) {
			if (summaries[0].removed[0].parentElement.attributes.length > 0){
				if (typeof(summaries[0].removed[0].parentElement.attributes[0].nodeValue) !== 'undefined' && summaries[0].removed[0].parentElement.attributes[0].nodeValue != null) {
					parentNodeValue = summaries[0].removed[0].parentElement.attributes[0].nodeValue;
				}
			}
		}
		removed = {data: data, nodeType: nodeType, nodeName: nodeName, nodeValue: nodeValue, parentNodeValue: parentNodeValue};	
	}

	var mutation = { date : Date.now(), added: added, removed: removed };
	mutationArray.push(mutation);
	logger.logDOMMutation();

}

// Functions to call after the Mutation Summary Observer is disconnected
function handleSummaryLeftover(summaries) {
	var added, removed;
	if (summaries[0].added.length>0) {
		var data = "null";
		var nodeType = "null";
		var nodeName = "null";
		var nodeValue = "null";
		var parentNodeValue = "null";

		if (typeof(summaries[0].added[0].data) !== 'undefined' && summaries[0].added[0].data != null) {
			data = summaries[0].added[0].data;
		}

		if (typeof(summaries[0].added[0].nodeType) !== 'undefined' && summaries[0].added[0].nodeType != null) {
			nodeType = summaries[0].added[0].nodeType;
		}

		if (typeof(summaries[0].added[0].nodeName) !== 'undefined' && summaries[0].added[0].nodeName != null) {
			nodeName = summaries[0].added[0].nodeName;
		}

		if (typeof(summaries[0].added[0].nodeValue) !== 'undefined' && summaries[0].added[0].nodeValue != null) {
			nodeValue = summaries[0].added[0].nodeValue;
		}

		if (typeof(summaries[0].added[0].parentElement) !== 'undefined' && summaries[0].added[0].parentElement != null) {
			if (summaries[0].added[0].parentElement.attributes.length > 0){
				if (typeof(summaries[0].added[0].parentElement.attributes[0].nodeValue) !== 'undefined' && summaries[0].added[0].parentElement.attributes[0].nodeValue != null) {
					parentNodeValue = summaries[0].added[0].parentElement.attributes[0].nodeValue;
				}
			}
		}
		added = {data: data, nodeType: nodeType, nodeName: nodeName, nodeValue: nodeValue, parentNodeValue: parentNodeValue};	
	}

	if (summaries[0].removed.length>0) {
		var data = "null";
		var nodeType = "null";
		var nodeName = "null";
		var nodeValue = "null";
		var parentNodeValue = "null";

		if (typeof(summaries[0].removed[0].data) !== 'undefined' && summaries[0].removed[0].data != null) {
			data = summaries[0].removed[0].data;
		}

		if (typeof(summaries[0].removed[0].nodeType) !== 'undefined' && summaries[0].removed[0].nodeType != null) {
			nodeType = summaries[0].removed[0].nodeType;
		}

		if (typeof(summaries[0].removed[0].nodeName) !== 'undefined' && summaries[0].removed[0].nodeName != null) {
			nodeName = summaries[0].removed[0].nodeName;
		}

		if (typeof(summaries[0].removed[0].nodeValue) !== 'undefined' && summaries[0].removed[0].nodeValue != null) {
			nodeValue = summaries[0].removed[0].nodeValue;
		}

		if (typeof(summaries[0].removed[0].parentElement) !== 'undefined' && summaries[0].removed[0].parentElement != null) {
			if (summaries[0].removed[0].parentElement.attributes.length > 0){
				if (typeof(summaries[0].removed[0].parentElement.attributes[0].nodeValue) !== 'undefined' && summaries[0].removed[0].parentElement.attributes[0].nodeValue != null) {
					parentNodeValue = summaries[0].removed[0].parentElement.attributes[0].nodeValue;
				}
			}
		}
		removed = {data: data, nodeType: nodeType, nodeName: nodeName, nodeValue: nodeValue, parentNodeValue: parentNodeValue};	
	}

	var mutation = { date : Date.now(), added: added, removed: removed };
	mutationArray.push(mutation);
	
	
	// call the mutations
	logger.logDOMMutation();
	// check for changed element values
	checkValues();
}

var all = []; 
var allElements
var oldValues = new Array();

// Get a list of elements in the doc, and their initial values
function initializeValues() {
	allElements = document.getElementsByTagName("*");
	
	for (var i=0, max=allElements.length; i < max; i++) {
		if (allElements[i].id) {
			var tempElem = document.getElementById(allElements[i].id);
			// Only watch the elements that have a value attribute
			if (tempElem.value != undefined) {
				all.push(tempElem);
				oldValues.push(tempElem.value);
			}
		}
	}
}

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

// Check elements for a change in value. Log all changes.
function checkValues() {
	for (var i=0, max=all.length; i < max; i++) {
		if (all[i].value != oldValues[i]) {
			logger.logElementValueChange(all[i], oldValues[i], all[i].value);
			oldValues[i] = all[i].value;
		}	
	}
	
}
