/**
 * This code is responsible for recording mutations to the DOM.
 * The first part of the file sets up a mutation summary observer using the Mutation Summary library
 * The second part of the file handles the summaries in an array so they can be output to the logger
 * The third part of the file watches document elements for changes in values
 * @Author Ryan Randhawa
 */

var observerSummary;
var mutationArray = new Array();

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
	
// Add the new summary to the mutationArray
function handleSummary(summaries) {
	var mutation = { date : new Date(), summaries : summaries };
	mutationArray.push(mutation);
}

// Functions to call after the Mutation Summary Observer is disconnected
function handleSummaryLeftover(summaries) {
	var mutation = { date : new Date(), summaries : summaries };
	mutationArray.push(mutation); 
	// call the mutations
	logger.logDOMMutation(false);
	// check for changed element values
	//checkValues();
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

// Check elements for a change in value. Log all changes.
function checkValues() {
	for (var i=0, max=all.length; i < max; i++) {
		if (all[i].value != oldValues[i]) {
			logger.logElementValueChange(all[i], oldValues[i], all[i].value);
		}	
	}
	
}
