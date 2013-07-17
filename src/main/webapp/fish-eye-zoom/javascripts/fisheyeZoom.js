	var tbl = document.createElement("table");
	
	var tblBody = document.createElement("tbody");
	
	var bigDaddy = document.createElement("div");
	bigDaddy.id="dock";
	var lineBreak = document.createElement('br');
	

	var episodeContainer = document.createElement("div");
	var episodeContainer2 = document.createElement("div");
	var episodeContents = document.createElement("div");
	var scaledDiv = document.createElement("div");
	scaledDiv.id="scaledDiv";
	$(scaledDiv).addClass('scaledDiv');
	scaledDiv.style.width="auto";
	scaledDiv.style.float="left";
	//scaledDiv.style.height="150px";


	$(episodeContainer).addClass('dock-container');
	episodeContainer.id="makeMeScrollable";
	episodeContainer.style.height="700px";
	//episodeContainer.style.overflow="scroll"

	var episode1Clicked=false;


	var episodes = new Array();
	var divs=new Array();
	var links=new Array();
	var row = document.createElement("tr");
    var cells=new Array();
    var currentEpisode;
    var temp_data;
    var conn;
    //REST CALL

   
var number_episodes;
	
 var url = 'http://localhost:8080/rest/clematis-api/episodes/';

  $.ajax({
		type: 'GET',
		url: url ,
		dataType: "json",
		async: false,
		success: renderList 
	});
	


function renderList(data) {
	//console.log(data);
	number_episodes=data.length;
	//alert("success");
	console.log(data);
}


	console.log(number_episodes);
	
	//create the list of episodes, ZOOM  level 0
	for (var i=0; i<number_episodes; i++){
		links[i]=document.createElement('a');
		$(links[i]).addClass('dock-item');
		(links[i]).style.top="auto";
		
		
		episodes[i]=document.createTextNode("Episode #"+i);
		divs[i]=document.createElement("div");

		 
		
		//$(divs[i]).addClass('box');
		//divs[i].id="div"+i;
		
		divs[i].appendChild(episodes[i]);
		links[i].appendChild(divs[i]);
		
		cells[i]=document.createElement("td");
    	cells[i].appendChild(divs[i]);
    
    	row.appendChild(cells[i]);
    	//console.log($(cells[i]).html()  );

    	 $.ajax({
		type: 'GET',
		url: 'http://localhost:8080/rest/clematis-api/episodes/'+i+'/source' ,
		dataType: "json",
		async: false,
		success: function show1(data) {


		if(data.id==0){
			$(divs[i]).addClass('box');
			//$(episodeContents).addClass('box');
		
		}
		else{
			$(divs[i]).addClass('box3');
			//$(episodeContents).addClass('box3');

		}

		}

		});
	
	}
	
	tbl.id="tbl";
	
    tblBody.appendChild(row);
    tbl.appendChild(tblBody);

    var newTbl = tbl.cloneNode(true);
    newTbl.id="newTbl";

	episodeContainer.appendChild(tbl);
	

	(scaledDiv).appendChild(newTbl);

	
//	episodeContainer2.appendChild(episodeContainer);

//Add the menu to the top of the page
// The menu consists of the following"
//1-a home button
//2-a back button
//3-a forward button
//4-a minimap to show the user where they are in a story

	var lineBreak=document.createElement("br");
	var menuContainer=document.createElement("div");
	menuContainer.style.position="fixed";
	menuContainer.style.left="30%";

	var menuContainerSlide=document.createElement("div");
	menuContainerSlide.id="slide"
	$(menuContainer).addClass('menu');
	var menuList = document.createElement("ul");
	menuList.id="menu";
	var menuElem1 = document.createElement("li");
	var menuElem2 = document.createElement("li");
	var menuElem3 = document.createElement("li");
	var menuElem4 = document.createElement("li");

	var menuAnchor1 = document.createElement("a");
	var menuAnchor2 = document.createElement("a");
	var menuAnchor3 = document.createElement("a");
	var menuAnchor4 = document.createElement("a");

	menuAnchor2.style.width="5px";
	menuAnchor1.href="#anchor1";
	menuAnchor2.href="#anchor2";
	menuAnchor3.href="#anchor3";
	menuAnchor4.href="#anchor4";

	var backButton = document.createElement("input");
    backButton.setAttribute("type", "image");
   	backButton.src="images/arrow_left.gif";
    backButton.setAttribute("value", "Back");
    backButton.setAttribute("name", "Back");
    backButton.setAttribute("title","previous episode");
    backButton.style.height="20px";
    backButton.addEventListener('click', function(){
    	if(currentEpisode==0){
    		currentEpisode=episodes.length;
    	}
    	nextPreviousEpisode(currentEpisode-1);
	});

    var forwardButton = document.createElement("input");
    forwardButton.setAttribute("type", "image");
   	forwardButton.src="images/arrow_right.gif";
    forwardButton.setAttribute("value", "Forward");
    forwardButton.setAttribute("name", "Forward");
    forwardButton.setAttribute("title","next episode");
    forwardButton.style.height="20px";
    forwardButton.addEventListener('click', function(){
    	if(currentEpisode==episodes.length-1){
    		currentEpisode=-1;
    	}
    	nextPreviousEpisode(currentEpisode+1);
	});

    var homeButton = document.createElement("input");
    homeButton.setAttribute("type", "image");
   	homeButton.src="images/Home.png";
    homeButton.setAttribute("value", "Home");
    homeButton.setAttribute("name", "Home");
    homeButton.setAttribute("title","Home");
    homeButton.style.height="20px";
    homeButton.onclick=reloadPage;

    var fullScreen = document.createElement("input");
    fullScreen.setAttribute("type", "image");
   	fullScreen.src="images/fullscreen2.gif";
    fullScreen.setAttribute("value", "fullScreen");
    fullScreen.setAttribute("name", "fullScreen");
    fullScreen.setAttribute("title","expand current episode");
    fullScreen.style.height="20px";
    fullScreen.addEventListener('click', function(){

    	if(zoomLevel1==true){
    		expandCurrentEpisode(currentEpisode);
    	}
    	else
    	{}
	});


    function reloadPage(){
    	window.location.reload();

    }
	
	//menuAnchor1.text = "Item1";
	//menuAnchor2.text = "Item2";
	//menuAnchor3.text = "Item3";

	
	menuAnchor1.appendChild(homeButton);
	//menuAnchor2.appendChild(backButton);
	//menuAnchor3.appendChild(forwardButton);
	//menuAnchor4.appendChild(fullScreen);

	menuElem1.value=1;
	menuElem1.appendChild(menuAnchor1);
	menuElem2.appendChild(menuAnchor2);
	menuElem3.appendChild(menuAnchor3);
	menuElem4.appendChild(menuAnchor4);

	menuList.appendChild(menuElem1);
	menuList.appendChild(menuElem4);
	menuList.appendChild(menuElem2);
	menuList.appendChild(menuElem3);
	
	menuContainer.appendChild(menuList);
	menuContainer.appendChild(menuContainerSlide);


	//Add the menu and list of episodes to the page
	//document.body.style.backgroundColor = '#000';
	document.body.appendChild(menuContainer);
	document.body.appendChild(episodeContainer);
	

//Create the tabbed view that will be used for zoom level2
var tabs_div=document.createElement("div");
tabs_div.id="tabs_div";
tabs_div.style.top="50px";



var list = document.createElement("ul");
var elem1 = document.createElement("li");
var elem2 = document.createElement("li");
var elem3 = document.createElement("li");
var elem4 = document.createElement("li");

var anchor1 = document.createElement("a");
var anchor2 = document.createElement("a");
var anchor3 = document.createElement("a");
var anchor4 = document.createElement("a");

anchor1.href="#tabs1";
anchor2.href="#tabs2";
anchor3.href="#tabs3";
anchor4.href="#tabs4";

anchor1.text = "Event Type";
anchor2.text = "DOM mutations";
anchor3.text = "Trace";
anchor4.text = "Episode";


elem1.appendChild(anchor1);
elem2.appendChild(anchor2);
elem3.appendChild(anchor3);
elem4.appendChild(anchor4);

list.appendChild(elem4);
list.appendChild(elem1);
list.appendChild(elem2);
list.appendChild(elem3);



var tabs1=document.createElement("div");
var tabs2=document.createElement("div");
var tabs3=document.createElement("div");
var tabs4=document.createElement("div");

tabs1.id="tabs1";
tabs2.id="tabs2";
tabs3.id="tabs3";
tabs4.id="tabs4";



tabs_div.appendChild(list);
tabs_div.appendChild(tabs1);
tabs_div.appendChild(tabs2);
tabs_div.appendChild(tabs3);
tabs_div.appendChild(tabs4);

$(tabs_div).tabs();

var zoomLevel1=false;




////////////////////////////////////////////////////////////////////ZOOM LEVEL 1 /////////////////

	//var episodeContents = document.createElement("div"); // MOVED UP
	episodeContents.id=("episode-Contents");

	
	episodeContents.style.border = " solid black";
	episodeContents.style.display="table";
	
	episodeContents.style.left="390px";
	episodeContents.style.top="120px";

	var tblLevel1 = document.createElement("table");
	var tblBodyLevel1 = document.createElement("tbody");
	
	var rowLevel1 = document.createElement("tr");
	var row2Level1 = document.createElement("tr");
    var cellLevel1= document.createElement("td");
    cellLevel1.id="CELL1";
    
    var cell2Level1= document.createElement("td");
    cell2Level1.id="CELL2";

    
    rowLevel1.appendChild(cellLevel1);
    row2Level1.appendChild(cell2Level1);
    
    tblBodyLevel1.appendChild(rowLevel1);
    tblBodyLevel1.appendChild(row2Level1);
    
    var row3Level1 = document.createElement("tr");
    var cell3Level1= document.createElement("td");
    cell3Level1.id="CELL3";

    row3Level1.appendChild(cell3Level1);
    tblBodyLevel1.appendChild(row3Level1); 
    tblLevel1.appendChild(tblBodyLevel1);
    tblLevel1.setAttribute("border","6");

    episodeContents.appendChild(tblLevel1);

    episodeContents.style.overflow="auto";


var zoomLevel1Container_DOM=document.createElement("div");
var zoomLevel1Container_source=document.createElement("div");
var zoomLevel1Container_trace=document.createElement("div");

var cell1SZ=cellLevel1;	
var cell2SZ=cell2Level1;		
var cell3SZ=cell3Level1		

for (var i = 0, n = cells.length; i<n; i++) {
  var el = cells[i];

  
  el.addEventListener('click', (function(i, el) { 
    return function() {
    	menuAnchor4.appendChild(fullScreen);
    	if(zoomLevel1==false){
			zoomLevel1=true;
			currentEpisode=i;

	//Get the source for  zoom level 1, specifically we want to get the eventType and eventHandler from the the source
	var url = 'http://localhost:8080/rest/clematis-api/episodes/'+i+'/source';
    var tempDiv_source=document.createElement("div");
   	var tbl_source = document.createElement("table");
	var tblBody_source = document.createElement("tbody");
	var rows_source=new Array;
	var cells_source=new Array;
	rows_source[0] = document.createElement("tr");
	rows_source[1] = document.createElement("tr");
	rows_source[2]=document.createElement("tr");

	 cells_source[0]=document.createElement("td");
	 cells_source[1]=document.createElement("td");
	 cells_source[2]=document.createElement("td");
	 cells_source[3]=document.createElement("td");
	 cells_source[4]=document.createElement("td");


	 //cells_source[0].style.display="block";
	 cells_source[0].style.fontSize="20px";
	 cells_source[1].style.fontSize="15px";
	 cells_source[2].style.fontSize="15px";
	 cells_source[0].style.color="black";
	 cells_source[1].style.color="black";
	 cells_source[2].style.color="black";

	  cells_source[0].style.fontFamily="TAHOMA";
	  cells_source[1].style.fontFamily="TAHOMA";
	  cells_source[2].style.fontFamily="TAHOMA";

	 cells_source[0].colSpan=2;

	 cells_source[0].appendChild(document.createTextNode("Source"));

	 
	 rows_source[0].appendChild(cells_source[0]);
	 

	 
	 
	
  	$.ajax({
		type: 'GET',
		url: url ,
		dataType: "json",
		async: false,
		success: function show1(data) {

		

	 	//if id != 0 then its an XHR or Timeout event
		if(data.id==0){
			cells_source[1].appendChild(document.createTextNode("eventType"));
	 		cells_source[2].appendChild(document.createTextNode("targetElement"));
			cells_source[3].appendChild(document.createTextNode(JSON.stringify(data.eventType)));
			cells_source[4].appendChild(document.createTextNode(JSON.stringify(data.targetElement)));
			$(episodeContents).addClass('box').removeClass('box3');

		
		}
		else{

			cells_source[1].appendChild(document.createTextNode("eventType"));
	 		cells_source[2].appendChild(document.createTextNode("targetElement"));
			cells_source[3].appendChild(document.createTextNode("XHR/TO"));
			cells_source[4].appendChild(document.createTextNode(JSON.stringify(data.id)));
			$(episodeContents).addClass('box3').removeClass('box');
		}
		

		rows_source[1].appendChild(cells_source[1]);
		rows_source[1].appendChild(cells_source[2]);
		rows_source[2].appendChild(cells_source[3]);
		rows_source[2].appendChild(cells_source[4]);

		tblBody_source.appendChild(rows_source[0]);
	 	tblBody_source.appendChild(rows_source[1]);
		tblBody_source.appendChild(rows_source[2]);


	}

		});

  	tbl_source.setAttribute("border","3");
  	tbl_source.appendChild(tblBody_source);
  	tempDiv_source.appendChild(tbl_source);
	cell1SZ.appendChild(tempDiv_source);



//Get the Dom for  zoom level 1
	 var url2 = 'http://localhost:8080/rest/clematis-api/episodes/'+i;
	 var dom;
	$.ajax({
		type: 'GET',
		url: url2 ,
		dataType: "json",
		async: false,
		success: function renderList3(data) {

		console.log(data);
		dom=document.createTextNode(JSON.stringify(data.dom));
	}
		});

  		cell3SZ.appendChild(dom);



  	//Get the trace for  zoom level 1, specifically we want to get the trace and lineNo and targetfunction and scopeName from the the trace.	
  	 var url3 = 'http://localhost:8080/rest/clematis-api/episodes/'+i+'/trace';
	 var trace=new Array;
	 var tempDiv=document.createElement("div");
	 var myBr = document.createElement('br');
	 var tbl = document.createElement("table");
	 var tblBody = document.createElement("tbody");
	 var rows=new Array;
	 rows[0] = document.createElement("tr");
	 rows[1] = document.createElement("tr");
	 var cells=new Array;

	 cells[0]=document.createElement("td");
	 cells[1]=document.createElement("td");
	 cells[2]=document.createElement("td");
	 cells[3]=document.createElement("td");
	 
	 cells[0].style.fontSize="20px";
	 cells[1].style.fontSize="15px";
	 cells[2].style.fontSize="15px";
	 cells[3].style.fontSize="15px";

	 cells[0].style.color="black";
	 cells[1].style.color="black";
	 cells[2].style.color="black";
	 cells[3].style.color="black";
	 
	 cells[0].style.fontFamily="TAHOMA";
	 cells[1].style.fontFamily="TAHOMA";
	 cells[2].style.fontFamily="TAHOMA";
	 cells[3].style.fontFamily="TAHOMA";

	 cells[0].colSpan=3;
	 cells[0].appendChild(document.createTextNode("Trace"));
	 cells[1].appendChild(document.createTextNode("lineNo"));
	 cells[2].appendChild(document.createTextNode("targetFunction"));
	 cells[3].appendChild(document.createTextNode("scopeName"));

	 rows[0].appendChild(cells[0]);
	 rows[1].appendChild(cells[1]);
	 rows[1].appendChild(cells[2]);
	 rows[1].appendChild(cells[3]);
	 tblBody.appendChild(rows[0]);
	 tblBody.appendChild(rows[1]);

	$.ajax({
		type: 'GET',
		url: url3 ,
		dataType: "json",
		async: false,
		success: function renderList4(data) {
	
	
			
			for(var i=0;i<data.trace.length;i++){
				rows[i+2]=document.createElement("tr");

				cells[i+4]=document.createElement("td");
				cells[i+5]=document.createElement("td");
				cells[i+6]=document.createElement("td");
				cells[i+4].appendChild(document.createTextNode(JSON.stringify(data.trace[i].lineNo)));
				cells[i+5].appendChild(document.createTextNode(JSON.stringify(data.trace[i].targetFunction)));
				cells[i+6].appendChild(document.createTextNode(JSON.stringify(data.trace[i].scopeName)));

				rows[i+2].appendChild(cells[i+4]);
				rows[i+2].appendChild(cells[i+5]);
				rows[i+2].appendChild(cells[i+6]);
				tblBody.appendChild(rows[i+2]);
				//tempDiv.appendChild(trace[i]);
			}
			
			tbl.setAttribute("border","3");
			tbl.appendChild(tblBody);
			tempDiv.appendChild(tbl);
	}
		});

  	

		cell2SZ.appendChild(tempDiv);
		$(divs[i]).replaceWith(episodeContents);
		jsPlumb.detachEveryConnection();
	
	
			
		}
		
		
		else{
			zoomLevel1=false;
			$(episodeContents).replaceWith(divs[i]);
			cell1SZ.removeChild(cell1SZ.lastChild);
			cell2SZ.removeChild(cell2SZ.lastChild);
			cell3SZ.removeChild(cell3SZ.lastChild);
			redrawLinks(temp_data);
			
		}

    }
  })(i, el), false);
}

//Function to zoom into the current episode(ZOOM level 2)
function expandCurrentEpisode(i){

	menuAnchor2.appendChild(backButton);
	menuAnchor3.appendChild(forwardButton);

	anchor4.text="Episode  "+i;
	var url = 'http://localhost:8080/rest/clematis-api/episodes/'+i+'/source';
    var eventType;
   

  	$.ajax({
		type: 'GET',
		url: url ,
		dataType: "json",
		async: false,
		success: renderList2
		});

  	function renderList2(data) {

		console.log(data.eventType);
		eventType=document.createTextNode(data.eventType);
	}
		tabs1.appendChild(eventType);
//get the DOM of a speceifc episode
 	var url2 = 'http://localhost:8080/rest/clematis-api/episodes/'+i;
	 var dom;
	$.ajax({
		type: 'GET',
		url: url2 ,
		dataType: "json",
		async: false,
		success: renderList3
		});

  	function renderList3(data) {
		console.log(data);
		dom=document.createTextNode(JSON.stringify(data.dom));
	}
		tabs2.appendChild(dom);

//get the trace of a specefic episode
	var url3 = 'http://localhost:8080/rest/clematis-api/episodes/'+i+'/trace';
	 var trace=new Array;
	 var tempDiv=document.createElement("div");
	 var myBr = document.createElement('br');
	$.ajax({
		type: 'GET',
		url: url3 ,
		dataType: "json",
		async: false,
		success: function renderList4(data) {
				
			
			for(var i=0;i<data.trace.length;i++){
				trace[i]=document.createTextNode(JSON.stringify(data.trace)+"\n");
				tempDiv.appendChild(trace[i]);
			}

	
	}
		});

		tabs3.appendChild(trace[0]);

    	$(episodeContainer).replaceWith(tabs_div);

}


// function to navigate to the next or previous episode in zoom level 2
function nextPreviousEpisode(i){

	currentEpisode=i;
	tabs1.innerHTML = '';
	tabs2.innerHTML = '';
	tabs3.innerHTML = '';

	anchor4.text="Episode  "+i;
	var url = 'http://localhost:8080/rest/clematis-api/episodes/'+i+'/source';
    var eventType;
   

  	$.ajax({
		type: 'GET',
		url: url ,
		dataType: "json",
		async: false,
		success: renderList2
		});

  	function renderList2(data) {

		console.log(data.eventType);
		eventType=document.createTextNode(data.eventType);
	}
		tabs1.appendChild(eventType);
//get the DOM of a speceifc episode
 	var url2 = 'http://localhost:8080/rest/clematis-api/episodes/'+i;
	 var dom;
	$.ajax({
		type: 'GET',
		url: url2 ,
		dataType: "json",
		async: false,
		success: renderList3
		});

  	function renderList3(data) {
		console.log(data);
		dom=document.createTextNode(JSON.stringify(data.dom));
	}
		tabs2.appendChild(dom);

//get the trace of a specefic episode
	var url3 = 'http://localhost:8080/rest/clematis-api/episodes/'+i+'/trace';
	 var trace=new Array;
	 var tempDiv=document.createElement("div");
	 var myBr = document.createElement('br');
	$.ajax({
		type: 'GET',
		url: url3 ,
		dataType: "json",
		async: false,
		success: function renderList4(data) {
				
			
			for(var i=0;i<data.trace.length;i++){
				trace[i]=document.createTextNode(JSON.stringify(data.trace)+"\n");
				tempDiv.appendChild(trace[i]);
			}

	
	}
		});

		tabs3.appendChild(trace[0]);


}

function redrawLinks(data) {

		//temp_data=data;
		//alert("REDRAW");
		for (var i = 0; i < data.length; i++) {
			console.log("redrawring");
			console.log(data[i].source);
			console.log(data[i].target);
			//links[i]=jsPlumb.addEndpoint(cells[data[i].source]);
			//links[i+1]=jsPlumb.addEndpoint(cells[data[i].target]);
			//jsPlumb.connect({ source:links[i], target:links[i+1],endpoint:["rectangle"],connector:["Bezier", { curviness:30 }] });
			jsPlumb.connect({
                source: (cells[data[i].source]),
                target: (cells[data[i].target]),
                connector: ["Bezier",{ curviness:30 }],
                cssClass: "c1",
                endpoint: "Blank",
                endpointClass: "c1Endpoint",
                paintStyle: {
                    lineWidth: 6,
                    strokeStyle: "#a7b04b",
                    outlineWidth: 1,
                    outlineColor: "#666"
                },
                endpointStyle: {
                    fillStyle: "#a7b04b"
                },
                
            });
		};

	}

//create the scrollable effect for a series of episodes
$("#makeMeScrollable").smoothDivScroll({
			mousewheelScrolling: "allDirections",
			manualContinuousScrolling: true,
			//autoScrollingMode: "onStart"
});
	

//variables to retrieve the tables that contain the episodes, these will be used to create the overview bar that is displayed in the menu.	
var tableNew;
var cellsNew;
var table2;
var cells2;

jsPlumb.bind("ready", function() {

	var url = 'http://localhost:8080/rest/clematis-api/story/causalLinks';
	var links=new Array;
 	 $.ajax({
		type: 'GET',
		url: url ,
		dataType: "json",
		async: false,
		success: renderListLinks 
	});
	


	function renderListLinks(data) {

		temp_data=data;
		for (var i = 0; i < data.length; i++) {
			//links[i]=jsPlumb.addEndpoint(cells[data[i].source]);
			//links[i+1]=jsPlumb.addEndpoint(cells[data[i].target]);
			//jsPlumb.connect({ source:links[i], target:links[i+1],endpoint:["rectangle"],connector:["Bezier", { curviness:30 }] });
			jsPlumb.connect({
                source: (divs[data[i].source]),
                target: (divs[data[i].target]),
                connector: ["Bezier",{ curviness:30 }],
                cssClass: "c1",
                endpoint: "Blank",
                endpointClass: "c1Endpoint",
                paintStyle: {
                    lineWidth: 4,
                    strokeStyle: "#a7b04b",
                    outlineWidth: 1,
                    outlineColor: "#666"
                },
                endpointStyle: {
                    fillStyle: "#a7b04b"
                },
                
            });
		};

	}





       // var e0 = jsPlumb.addEndpoint(divs[0]),
        //e1 = jsPlumb.addEndpoint(divs[3]);

        //jsPlumb.connect({ source:e0, target:e1,connector:["Bezier", { curviness:70 }],endpoint:"Blank" });
     });

$(document).ready(function(){

	menuContainer.appendChild(scaledDiv);
	menuSlider.init('menu','slide');


   	
});

//Create the overview bar on top of the series of episodes
//shade the current episode you are on.
window.onload=function() {
	
    tableNew = document.getElementById('newTbl');
    cellsNew = tableNew.getElementsByTagName('div');

    var width=cellsNew[0].offsetWidth;
    var height=cellsNew[0].offsetHeight;
    var fontSize=cellsNew[0].style.fontSize;
    console.log(width);
    console.log(height);

    table2 = document.getElementById('tbl');
    cells2 = table2.getElementsByTagName('div');


for (var i = 0, n = cells2.length; i<n; i++) {
  var el = cells2[i];
  el.addEventListener('mouseover', (function(i, el) { 
    return function() {
      cellsNew[i].setAttribute('class','box2');
      cellsNew[i+1].setAttribute('class','box2');
      cellsNew[i-1].setAttribute('class','box2');
    }
  })(i, el), false);
}

for (var i = 0, n = cells2.length; i<n; i++) {
  var el = cells2[i];
  el.addEventListener('mouseout', (function(i, el) { 
    return function() {
      cellsNew[i].setAttribute('class','box');
      cellsNew[i+1].setAttribute('class','box');
      cellsNew[i-1].setAttribute('class','box');
    }
  })(i, el), false);
}



};	


	
