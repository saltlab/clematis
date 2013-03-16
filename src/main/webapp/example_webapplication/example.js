var loggedIn = false;
/*
document.getElementById("submitButton").addEventListener('click', submitForm2);

function submitForm2() {
	var emailAddr = document.getElementById("emailInput").value;
	var pword = document.getElementById("passwordInput").value;
	var loginDiv = document.getElementById("loginDiv");
	loginDiv.innerHTML = "";
	
	if(emailAddr.indexOf("@") == -1) {
		loginDiv.innerHTML = "Incorrect email format!&nbsp;&nbsp;&nbsp;&nbsp;";
		return;
	}
	
	document.getElementById("welcomeDiv").innerHTML = "Welcome " + emailAddr + "!";
	document.getElementById("emailInput").style.display = "none"; //="block" to display
	document.getElementById("passwordInput").style.display = "none";
	document.getElementById("submitButton").style.display = "none";
	
	loggedIn = true;
}
*/
document.getElementById("changeContentButton").addEventListener('click', changeContentHandler);

function changeContentHandler() {
	var xmlhttp = new XMLHttpRequest();

	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState==4/* && xmlhttp.status==200*/) {
			document.getElementById("xhrResponse").innerHTML = xmlhttp.responseText + " (used method: GET)";
		}
	}
	xmlhttp.open("GET","local_url.txt",true);
	xmlhttp.send();

}

document.getElementById("changeContentButton2").addEventListener('click', changeContentHandler2);

function changeContentHandler2() {
	var xmlhttp = new XMLHttpRequest();

	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState==4/* && xmlhttp.status==200*/) {
			document.getElementById("xhrResponse2").innerHTML = xmlhttp.responseText + " (used method: POST)";
		}
	}
	xmlhttp.open("POST","local_url.txt",true);
	xmlhttp.send("post method example");

}

document.getElementById("changeContentButton3").addEventListener('click', changeContentHandler3);

function changeContentHandler3() {
	var xmlhttp = new XMLHttpRequest();

	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState==4/* && xmlhttp.status==200*/) {
			document.getElementById("xhrResponse3").innerHTML = xmlhttp.responseText + " (delayed sending)";
		}
	}

	setTimeout(sendRequestByDelay, 7000, xmlhttp);

}

function sendRequestByDelay(xmlhttp) {

	xmlhttp.open("GET","local_url.txt",true);
	xmlhttp.send();
}

document.getElementById("set_timeout").addEventListener('click', setTimeoutHandler);

function setTimeoutHandler() {
	var delay = document.getElementById("timeoutDelay").value;
	var funcName = document.getElementById("timeoutFunc").value;
	if (funcName == "alert")
		setTimeout(alert_timeout, delay);
	else if (funcName == "log")
		setTimeout(log_timeout, delay);
	else if (funcName == "increaseDelay")
		setTimeout(increaseDelay_timeout, delay);

	if (displayCountdown)
		startClock(delay);
}

function startClock(delay) {
	document.getElementById("displayTime").innerHTML = delay;
	setTimeout(updateClock, 1000);
}

function updateClock() {
	if (parseInt(document.getElementById("displayTime").innerHTML) >= 1000) {
		document.getElementById("displayTime").innerHTML = document.getElementById("displayTime").innerHTML - 1000;
		setTimeout(updateClock, 1000);
	}
}

function alert_timeout() {
	alert("Timeout Callback");
	document.getElementById("displayTime").innerHTML = 0;
}

function log_timeout() {
	console.log("Timeout Callback");
	document.getElementById("displayTime").innerHTML = 0;
}

function increaseDelay_timeout() {
	document.getElementById("timeoutDelay").value = parseInt(document.getElementById("timeoutDelay").value) + 10000;
	document.getElementById("displayTime").innerHTML = 0;
}

document.getElementById("selectDegree").addEventListener('change', selectInstMajor);

function selectInstMajor() {
	var val = document.getElementById("selectDegree").value;
	if (val == "highschool") {
		document.getElementById("instName").disabled = true;
		document.getElementById("majorName").disabled = true;
	}
	else if (val == "postsec") {
		document.getElementById("instName").disabled = false;
		document.getElementById("majorName").disabled = false;
	}
}

document.getElementById("submitForm").addEventListener('click', submitForm, false);

function submitForm(e) {
	document.getElementById("submitMsg").innerHTML = "Form submitted";

    if(document.getElementById("selectDegree").value == "highschool") e.stopPropagation();
}

document.getElementById("table1").addEventListener('click', checkForm, false);

function checkForm() {
	var val1 = document.getElementById("instName").value;
	var val2 = document.getElementById("majorName").value;
	
	if (val1.length == 0 || val2.length == 0) {
		document.getElementById("submitMsg").innerHTML = "Form NOT submitted";
	}
}

document.getElementById("countdownSelect").addEventListener('change', toggleCountdownDisplay);

function toggleCountdownDisplay() {
        displayCountdown = !displayCountdown;
}
