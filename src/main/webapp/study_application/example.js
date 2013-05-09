//document.write('<script type="text/javascript" src="toolbar/trial.js"></script>');
var submissionFinalized = false;
var numOfClicksOnEntry = 0;
var gradEducationActivated = false;


function addOption(select, label, value) {
	select.options[select.options.length] = new Option(label, value);		
}

document.getElementById("agreementCheckbox").addEventListener('click', agreementHandler, false);

function agreementHandler() {
	var value = document.getElementById("agreementCheckbox").value;
	if (value == "yes") {
		document.getElementById("agreementCheckbox").value = "no";
		deactivateContestEnter();
	}
	else {
		document.getElementById("agreementCheckbox").value = "yes";
		activateContestEnter();
	}
}

document.getElementById("enterContestButton").addEventListener('click', enterContest, false);
document.getElementById("surveyForm").addEventListener('click', handleFormClick, false);

function validContestEnter() {
	updateUserInformation();
	var name = document.getElementById("contestName").value;
	var email = document.getElementById("contestEmail").value;
	sendUserInfoToServer(name, email);
}

function warnInactiveSubmission() {
	if (submissionFinalized == false)
		alert("Please enter all required information!");
	else
		alert("Thanks for your submission!");
}

function invalidContestEnter() {
	alert("You must agree to terms and conditions first.");
}

function activateContestEnter() {
//	document.getElementById("contestName").setAttribute("disabled", false);
	document.getElementById("contestName").disabled = false;
	document.getElementById("contestEmail").disabled = false;
	document.getElementById("enterContestButton").disabled = false;
}

function handleFormClick() {
	var deptName = document.getElementById("deptName").value;
	var majorName = document.getElementById("majorName").value;

	if (deptName == "") {
		document.getElementById("submissionFinalizedMessage").innerHTML = "Submission NOT complete. Please enter department name.";
		submissionFinalized = false;
	}
	if (majorName == "") {
		document.getElementById("submissionFinalizedMessage").innerHTML = "Submission NOT complete. Please enter major name.";
		submissionFinalized = false;
	}
}

function updateWelcomePage() {
	
}

function sendUserInfoToServer(name, email) {
	var xmlhttp = new XMLHttpRequest();

	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 4 /*&& xmlhttp.status == 200*/) {
			//console.log(xmlhttp.responseText);
		}
	}
	xmlhttp.open("POST", "./local_url.txt", true);
	setTimeout(sendRequestForContest, 3000, xmlhttp, name, email);
//	xmlhttp.send("Name: " + name + ";Email: " + email);
}

function sendRequestForContest(xmlhttp, name, email) {
	xmlhttp.send("Name: " + name + ";Email: " + email);
}

document.getElementById("submitButton").addEventListener('click', submitForm, false);

function updateUserInformation() {
	updateWelcomePage();
}

function submitForm() {
	sendSubmissionToServer();
	submissionFinalized = true;
	document.getElementById("submissionFinalizedMessage").innerHTML = "Submission completed.";

	var goodbyeDiv = document.createElement('div');
    goodbyeDiv.innerHTML = "Thank you!";
    document.getElementById("submissionFinalizedMessage").appendChild(goodbyeDiv);
}

function enterContest() {
	var value = document.getElementById("agreementCheckbox").value;
	if (value == "no") {
		invalidContestEnter();
	}
	else {
		validContestEnter();
//		document.getElementById("enteredInContestDiv").innterHTML = "You have entered in the contest!";
	}
}

document.getElementById("submissionDiv").addEventListener('click', handleSubmissionDivClick, false);

function handleSubmissionDivClick() {
	setTimeout(warnInactiveSubmission, 2000);
}

document.getElementById("submissionAndMessage").addEventListener('click', handleClickOnSubmissionAndMessage, true);

/*
document.getElementById("content").addEventListener('click', handleContentClick, true);

function handleContentClick() {
	
}
*/
/*
document.getElementById("gradeSelect").addEventListener('click', chooseGrade, false);

function chooseGrade() {
	var selectedGrade = document.getElementById("gradeSelect");
	alert(selectedGrade);
}
*/

document.getElementById("education").addEventListener('click', educationDivClicked, false);

function deactivateContestEnter() {
	document.getElementById("contestName").disabled = true;
	document.getElementById("contestEmail").disabled = true;
	document.getElementById("enterContestButton").disabled = true;
}

function sendSubmissionToServer() {
	var xmlhttp = new XMLHttpRequest();

	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 4) {
			//console.log(xmlhttp.responseText);
		}
	}
	xmlhttp.open("POST", "./local_url.txt", true);
	xmlhttp.send("Submission completed");
}

function educationDivClicked() {
	var numOfYears = 4;
	var select = document.getElementById("gradeSelect");
	if (document.getElementById("gradRadioB").checked) {
		if (gradEducationActivated == false) {
			gradEducationActivated = true;
			addOption(select, '5', '5');
			addOption(select, '6', '6');
			//select.options[select.options.length] = new Option('6', '6');
		}
		select.disabled = false;		
	}
	else if (document.getElementById("undergradRadioB").checked) {
		if (select.options.length == 6) {
			select.remove(5);
			select.remove(4);
			gradEducationActivated = false;
		}
		
		select.disabled = false;
	}
}

function handleClickOnSubmissionAndMessage() {
	numOfClicksOnEntry ++;
}

