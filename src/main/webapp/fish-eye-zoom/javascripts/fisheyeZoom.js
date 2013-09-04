// todo
var globalEpisodeContainer;
// todo

	var tbl = document.createElement("table");
	var newTbl=document.createElement("table");

	var tblBody = document.createElement("tbody");
	var tblBody_map = document.createElement("tbody");

	var lineBreak = document.createElement('br');
	

	var episodeContainer = document.createElement("div");
	var episodeContainer2 = document.createElement("div");
	//var episodeContents = document.createElement("div");
	var episodeContents = new Array;
	var scaledDiv = document.createElement("div");
	scaledDiv.id="scaledDiv";
	$(scaledDiv).addClass('scaledDiv');
	//scaledDiv.style.width="auto";
	//scaledDiv.style.float="left";
	scaledDiv.style.height="50px";


	//$(episodeContainer).addClass('dock-container');
	episodeContainer.id="makeMeScrollable";
	episodeContainer.style.height="1500px";
	//episodeContainer.style.overflow="scroll"

	var episode1Clicked=false;


	var episodes = new Array();
	var divs=new Array();
	var links=new Array();
	var row = document.createElement("tr");
    var cells=new Array();

    var episodes_map = new Array();
	var divs_map=new Array();
	var links_map=new Array();
	var row_map = document.createElement("tr");
    var cells_map=new Array();

    var currentEpisode;
    var temp_data;
    var conn;
    //REST CALL

   
var number_episodes;
var timeStamps=new Array;
var date=new Array;
var hours=new Array;
var minutes=new Array;
var seconds=new Array;
var formattedTime=new Array;	
 var url = 'http://localhost:8080/rest/clematis-api/'+fileName+'/episodes/';

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
	//console.log(data);
	for (var i = 0; i < data.length; i++) {
		//timeStamps[i]=data[i].source.timeStamp;
		//console.log("timeStamps"+(timeStamps[i]));
		 date[i] = new Date((data[i].source.timeStamp));
		// hours part from the timestamp
		 hours[i] = date[i].getUTCHours()
		// minutes part from the timestamp
		 minutes[i] = date[i].getUTCMinutes();
		// seconds part from the timestamp
		 seconds[i] = date[i].getUTCSeconds();

		// will display time in 10:30:23 format
		 formattedTime[i] =minutes[i] + ':' + seconds[i];

	};
	
	// todo
	globalEpisodeContainer = data;
/*
	console.log("=======================");
	console.log(globalEpisodeContainer);

	var marchedIDs;	
//	var matchedIDs = searchByDomEventType("click");

	matchedIDs = searchByDomElementKeyword("record");
	
	var key = "Send";
	matchedIDs = searchTraceByKeyword(key.toLowerCase());

	for (var i = 0; i < matchedIDs.length; i ++) {
		console.log(matchedIDs[i], " - ", globalEpisodeContainer[matchedIDs[i]].trace.trace);
	}

	console.log("=======================");
	
	matchedIDs = searchTraceByKeyword("request");

	for (i = 0; i < matchedIDs.length; i ++) {
		console.log(matchedIDs[i], " - ", globalEpisodeContainer[matchedIDs[i]].trace.trace);
	}
*/	
	console.log("=======================");
	// todo
}


	
	var zoomLevel1=new Array;
	var bookmarkButton=new Array;
	//create the list of episodes, ZOOM  level 0
	for (var i=0; i<number_episodes; i++){

		
		zoomLevel1[i]=false;
		episodeContents[i]=document.createElement("div");
		links[i]=document.createElement('a');
		$(links[i]).addClass('dock-item');
		(links[i]).style.top="auto";
		

		links_map[i]=document.createElement('a');
		$(links_map[i]).addClass('dock-item');
		(links_map[i]).style.top="auto";
		
		//episodes[i]=document.createTextNode("Episode #"+i); 
		divs[i]=document.createElement("div");

		episodes_map[i]=document.createTextNode(i); 
		divs_map[i]=document.createElement("div");
		divs_map[i].style.width="4px";
		divs_map[i].style.height="6px";

		//divs[i].id="div"+i;
		
		   bookmarkButton[i] = document.createElement("input");
    		bookmarkButton[i].setAttribute("type", "image");
   		 bookmarkButton[i].setAttribute("src","images/star.png");
   		
   		 //divs[2].appendChild(bookmarkButton[0]);
		 //divs[0].appendChild(bookmarkButton[0]);

		 $.ajax({
		type: 'GET',
		url: 'http://localhost:8080/rest/clematis-api/'+fileName+'/episodes/'+i ,
		dataType: "json",
		async: false,
		success: function show1(data) {

		if(data.isBookmarked==true){
			 divs[i].appendChild(bookmarkButton[i]);

		}

	
			}
		});

    	 $.ajax({
		type: 'GET',
		url: 'http://localhost:8080/rest/clematis-api/'+fileName+'/episodes/'+i+'/source' ,
		dataType: "json",
		async: false,
		success: function show1(data) {



		if(data.id==0){
			episodes[i]=document.createTextNode("Episode #"+i+"\n"+"Event"); 
			$(divs[i]).addClass('cell_dom');
			$(divs_map[i]).addClass('box');

		
		}
		else if(data.id!=0 && data.callbackFunction.length>0){
			episodes[i]=document.createTextNode("Episode #"+i+"\n"+"Timeout"); 
			$(divs[i]).addClass('cell_to');
			$(divs_map[i]).addClass('box');

		}

		else
		{
			episodes[i]=document.createTextNode("Episode #"+i+"\n"+"  XHR"); 
			$(divs[i]).addClass('cell_xhr');
			$(divs_map[i]).addClass('box');

		}


		}

		});

    	 divs[i].appendChild(episodes[i]);

		links[i].appendChild(divs[i]);
		
		cells[i]=document.createElement("td");
		divs[i].style.height="65px";
		divs[i].style.width="85px";
		cells[i].setAttribute("title","time: "+formattedTime[i]);
		$(cells[i]).tipsy({gravity:'nw'});
    	cells[i].appendChild(divs[i]);
    
    	row.appendChild(cells[i]);

    	//divs_map[i].appendChild(episodes_map[i]);
		links_map[i].appendChild(divs_map[i]);
		
		cells_map[i]=document.createElement("td");
    	cells_map[i].appendChild(divs_map[i]);
    
    	row_map.appendChild(cells_map[i]);



	
	}
	
	tbl.id="tbl";
	
    tblBody.appendChild(row);
    tbl.appendChild(tblBody);
    //tbl.style.borderSpacing="6px";

   // var newTbl = tbl.cloneNode(true);

    tblBody_map.appendChild(row_map);
    newTbl.appendChild(tblBody_map);
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
	//menuContainer.style.position="fixed";
	menuContainer.style.left="13%";

	var menuContainerSlide=document.createElement("div");
	menuContainerSlide.id="slide"
	$(menuContainer).addClass('menu');
	var menuList = document.createElement("ul");
	menuList.id="menu";
	var menuElem1 = document.createElement("li");
	var menuElem2 = document.createElement("li");
	var menuElem3 = document.createElement("li");
	var menuElem4 = document.createElement("li");
	var menuElem_map = document.createElement("li");
	var menuElem_XHR = document.createElement("li");
	var menuElem_DOM = document.createElement("li");
	var menuElem_TO = document.createElement("li");

	var div_dom=document.createElement('div');
	$(div_dom).addClass('cell_dom_menu');
	div_dom.appendChild(document.createTextNode('Event'));
	menuElem_DOM.appendChild(div_dom);

	var div_xhr=document.createElement('div');
	$(div_xhr).addClass('cell_xhr_menu');
	div_xhr.appendChild(document.createTextNode('XHR'));
	menuElem_XHR.appendChild(div_xhr);

	var div_to=document.createElement('div');
	$(div_to).addClass('cell_to_menu');
	div_to.appendChild(document.createTextNode('TO'));
	menuElem_TO.appendChild(div_to);
	
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

    	//if(zoomLevel1==true){
    		expandCurrentEpisode(currentEpisode);
  
	});


    function reloadPage(){
    	window.location.reload();

    }

	menuAnchor1.appendChild(homeButton);
	menuElem1.value=1;
	menuElem1.appendChild(menuAnchor1);
	menuElem2.appendChild(menuAnchor2);
	menuElem3.appendChild(menuAnchor3);
	menuElem4.appendChild(menuAnchor4);

	menuList.appendChild(menuElem1);
	menuList.appendChild(menuElem4);
	menuList.appendChild(menuElem2);
	menuList.appendChild(menuElem3);
	menuList.appendChild(menuElem_DOM);
	menuList.appendChild(menuElem_XHR);
	menuList.appendChild(menuElem_TO);

	var menuElemMap=new Array;

	
	

	
	/***************************************/
	/*** Begin: Search By DOM Event Type ***/
	/***************************************/
	// todo ADD SELECT TAG FOR SEARCHING BY DOM EVENT TYPE
	
	var selectDomEventType = document.createElement('select');
	selectDomEventType.id = "selectDomEventType";
	//selectDomEventType.name='herolist';
	//selectDomEventType.value="Search by Event";
	//selectDomEventType.value="Default";
	selectDomEventType.style.background="#16a085";
	 	selectDomEventType.style.marginRight="10px"

	//selectDomEventType.name = "selectDomEventType";
	$(selectDomEventType).addClass('select-block span3');

	var domEventOption = document.createElement('option');
	domEventOption.value = "Search by Event";
	domEventOption.textContent = "Search by Event";
	selectDomEventType.appendChild(domEventOption);

	domEventOption = document.createElement('option');
	domEventOption.value = "Default";
	domEventOption.textContent = "Default";
	//domEventOption.selected="selected";
	selectDomEventType.appendChild(domEventOption);
	
	domEventOption = document.createElement('option');
	domEventOption.value = "click";
	domEventOption.textContent = "click";
	selectDomEventType.appendChild(domEventOption);

	domEventOption = document.createElement('option');
	domEventOption.value = "dblclick";
	domEventOption.textContent = "dblclick";
	selectDomEventType.appendChild(domEventOption);

	domEventOption = document.createElement('option');
	domEventOption.value = "mousedown";
	domEventOption.textContent = "mousedown";
	selectDomEventType.appendChild(domEventOption);

	domEventOption = document.createElement('option');
	domEventOption.value = "mousemove";
	domEventOption.textContent = "mousemove";
	selectDomEventType.appendChild(domEventOption);

	domEventOption = document.createElement('option');
	domEventOption.value = "mouseover";	
	domEventOption.textContent = "mouseover";
	selectDomEventType.appendChild(domEventOption);

	domEventOption = document.createElement('option');
	domEventOption.value = "mouseout";	
	domEventOption.textContent = "mouseout";
	selectDomEventType.appendChild(domEventOption);

	domEventOption = document.createElement('option');
	domEventOption.value = "mouseup";	
	domEventOption.textContent = "mouseup";
	selectDomEventType.appendChild(domEventOption);

	domEventOption = document.createElement('option');
	domEventOption.value = "keydown";	
	domEventOption.textContent = "keydown";
	selectDomEventType.appendChild(domEventOption);

	domEventOption = document.createElement('option');
	domEventOption.value = "keypress";	
	domEventOption.textContent = "keypress";
	selectDomEventType.appendChild(domEventOption);

	domEventOption = document.createElement('option');
	domEventOption.value = "keyup";	
	domEventOption.textContent = "keyup";
	selectDomEventType.appendChild(domEventOption);
	
	var menuElem_searchDomEl = document.createElement("li");

	

	
	//var divSearch=document.createElement('div');
	//divSearch.appendChild(selectDomEventType);
	//$(divSearch).addClass('span3');
	//document.body.appendChild(divSearch);

	menuElem_searchDomEl.appendChild(selectDomEventType);
	menuList.appendChild(menuElem_searchDomEl);
	
	/*************************************/
	/*** End: Search By DOM Event Type ***/
	/*************************************/
	
	/********** Begin: Search by Text **********/
	
	var searchTextInput = document.createElement('input');
	searchTextInput.type = 'text';
	searchTextInput.id = 'searchTextInput';
	searchTextInput.value = 'Search';
	searchTextInput.style.width = '60px';
	searchTextInput.style.height = '15px';
	searchTextInput.style.marginRight="10px";

	var menuElem_searchText = document.createElement("li");

	menuElem_searchText.appendChild(searchTextInput);
	menuList.appendChild(menuElem_searchText);

	for (var i=0; i<number_episodes; i++){
		 menuElemMap[i] = document.createElement("li");
		 menuElemMap[i].style.width="5px";
		 menuElemMap[i].style.width="10px";
 		 menuElemMap[i].appendChild(divs_map[i]);
		 menuList.appendChild(menuElemMap[i]);

	}
	
	/********** End: Search by Text **********/

	
	menuContainer.appendChild(menuList);
	menuContainer.appendChild(menuContainerSlide);


	//Add the menu and list of episodes to the page
	//document.body.style.backgroundColor = '#000';
	var code_div=document.createElement('div');
	document.body.appendChild(menuContainer);
	document.body.appendChild(episodeContainer);
	document.body.appendChild(code_div);

	
	/******** Add the listener for search elements *******/
	// Search by dom event select tag
	document.getElementById('selectDomEventType').addEventListener('change', searchByDomEventClicked);
	
	function clear(){
		for(var x=0;x<number_episodes;x++){
			$(divs[x]).removeClass('cell_xhr2');
			//console.log(x);
		}
	}
	function searchByDomEventClicked() {
		
		clear();
		var matchingEpisodes = searchByDomEventType(this.options[this.selectedIndex].value);
		if(this.options[this.selectedIndex].value=="Default"){
			console.log("Deafult");
			for(var j=0;j<number_episodes;j++){
			$(divs[j]).removeClass('cell_xhr2');

			console.log(j);
		}
			
		}
		console.log("++++\n", matchingEpisodes);
		for(var i=0;i<matchingEpisodes.length;i++){
			$(divs[(matchingEpisodes[i])]).addClass('cell_xhr2');

		}
	}
	
	// Search by text
	document.getElementById('searchTextInput').addEventListener('change', searchByTextValueChanged);
	
	function searchByTextValueChanged() {
		clear();
		var matchingSourceEpisodes = searchByDomEventType(this.value);
		var matchingTraceEpisodes = searchTraceByKeyword(this.value);
		console.log("----\n", matchingSourceEpisodes);
		for(var i=0;i<matchingSourceEpisodes.length;i++){
			$(divs[(matchingSourceEpisodes[i])]).addClass('cell_xhr2');	

		}

		for(var x=0;x<matchingTraceEpisodes.length;x++){
			$(divs[(matchingTraceEpisodes[x])]).addClass('cell_xhr2');	

		}
		console.log("****\n", matchingTraceEpisodes);
	}
	

	/*********************************/

//Create the tabbed view that will be used for zoom level2
var tabs_div=document.createElement("div");
tabs_div.id="tabs_div";
tabs_div.style.top="50px";
//tabs_div.style.position="absolute";
tabs_div.style.width="100%";




var list = document.createElement("ul");
var elem1 = document.createElement("li");
elem1.setAttribute('id','elem1EventType');
var elem2 = document.createElement("li");
elem2.setAttribute('id','elem2Mutation');
var elem3 = document.createElement("li");
var elem4 = document.createElement("li");

var anchor1 = document.createElement("a");
var anchor2 = document.createElement("a");
var anchor3 = document.createElement("a");
anchor3.setAttribute('id','anchor3');
var anchor4 = document.createElement("a");

anchor1.href="#tabs1";
anchor2.href="#tabs2";
anchor3.href="#tabs3";
anchor4.href="#tabs4";

anchor1.text = "Event Type";
anchor2.text = "DOM mutations";
//anchor3.text = "Trace";
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






////////////////////////////////////////////////////////////////////ZOOM LEVEL 1 /////////////////
var tblLevel1=new Array;
var tblBodyLevel1=new Array;
var rowLevel1=new Array;
var row2Level1=new Array;
var cellLevel1=new Array;
var cell2Level1=new Array;
var row3Level1=new Array;
var cell3Level1=new Array;
var cell1SZ=new Array;
var cell2SZ=new Array;
var cell3SZ=new Array;
for (var i=0; i<number_episodes; i++){
	episodeContents[i].id=("episode-Contents"+i);
	//episodeContents[i].style.border = " solid black";
	episodeContents[i].style.display="table";
	episodeContents[i].style.width="120px";

	tblLevel1[i] = document.createElement("table");
	tblBodyLevel1[i] = document.createElement("tbody");
	
	rowLevel1[i] = document.createElement("tr");
 	row2Level1[i] = document.createElement("tr");
    cellLevel1[i]= document.createElement("td");
    cellLevel1[i].id="CELL1"+i;
    cell2Level1[i]= document.createElement("td");
    cell2Level1[i].id="CELL2"+i;

    rowLevel1[i].appendChild(cellLevel1[i]);
    row2Level1[i].appendChild(cell2Level1[i]);
    
    tblBodyLevel1[i].appendChild(rowLevel1[i]);
    tblBodyLevel1[i].appendChild(row2Level1[i]);
    
    row3Level1[i] = document.createElement("tr");
    cell3Level1[i]= document.createElement("td");
    cell3Level1[i].id="CELL3"+i;

    row3Level1[i].appendChild(cell3Level1[i]);
    tblBodyLevel1[i].appendChild(row3Level1[i]); 
    tblLevel1[i].appendChild(tblBodyLevel1[i]);
    tblLevel1[i].setAttribute("border","0");

    episodeContents[i].appendChild(tblLevel1[i]);


	 cell1SZ[i]=cellLevel1[i];	
	 cell2SZ[i]=cell2Level1[i];		
	 cell3SZ[i]=cell3Level1[i];		

}
for (var i = 0, n = cells.length; i<n; i++) {
  var el = cells[i];

  
  el.addEventListener('click', (function(i, el) { 
    return function() {
    	//jsPlumb.detachEveryConnection();
    

    	menuAnchor4.appendChild(fullScreen);
    	if(zoomLevel1[i]==false){
			zoomLevel1[i]=true;
			currentEpisode=i;

	//Get the source for  zoom level 1, specifically we want to get the eventType and eventHandler from the the source
	var url = 'http://localhost:8080/rest/clematis-api/'+fileName+'/episodes/'+i+'/trace';
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
	 	if(data.trace[0].id !=0 && (data.trace[0].xhrId>=0)){

			cells_source[1].appendChild(document.createTextNode("eventType"));
	 		cells_source[2].appendChild(document.createTextNode("targetElement"));
			cells_source[3].appendChild(document.createTextNode("XHR:"+JSON.stringify(data.trace[0].xhrId)));
			cells_source[3].setAttribute('class','cell_source');
			//cells_source[4].appendChild(document.createTextNode(JSON.stringify(data.trace[0].xhrId)));
			$(episodeContents[i]).addClass('cell_xhr').removeClass('cell_to', 'cell_dom');


		}
		else if(data.trace[0].id !=0 && (data.trace[0].timeoutId>=0)){

			cells_source[1].appendChild(document.createTextNode("eventType"));
	 		cells_source[2].appendChild(document.createTextNode("targetElement"));
			cells_source[3].appendChild(document.createTextNode("TO:"+JSON.stringify(data.trace[0].timeoutId)));
			cells_source[3].setAttribute('class','cell_source');
			//cells_source[4].appendChild(document.createTextNode(JSON.stringify(data.trace[0].timeoutId)));
			$(episodeContents[i]).addClass('cell_to').removeClass('cell_xhr', 'cell_dom');
		}


		else{
			cells_source[1].appendChild(document.createTextNode("eventType"));
	 		cells_source[2].appendChild(document.createTextNode("targetElement id"));
			cells_source[3].appendChild(document.createTextNode(JSON.stringify((data.trace[0].eventType))));
			cells_source[3].setAttribute('class','cell_source');
			//cells_source[4].appendChild(document.createTextNode(JSON.stringify(data.targetElement.id)));
			$(episodeContents[i]).addClass('cell_dom').removeClass('cell_to' ,'cell_xhr');

		
		}
		
		
		

		rows_source[1].appendChild(cells_source[1]);
		rows_source[1].appendChild(cells_source[2]);
		rows_source[2].appendChild(cells_source[3]);
		//rows_source[2].appendChild(cells_source[4]);

		tblBody_source.appendChild(rows_source[0]);
	 	//tblBody_source.appendChild(rows_source[1]);
		tblBody_source.appendChild(rows_source[2]);


	}

		});

  	tbl_source.setAttribute("border","0");
  	tbl_source.appendChild(tblBody_source);
  	tempDiv_source.appendChild(tbl_source);
	cell1SZ[i].appendChild(tempDiv_source);



//Get the Dom for  zoom level 1

	 var tempDiv_mutation=document.createElement("div");
	 var tbl_mutation = document.createElement("table");
	 var tblBody_mutation = document.createElement("tbody");
	 var rows_mutation=new Array;
	 rows_mutation[0] = document.createElement("tr");
	 rows_mutation[1] = document.createElement("tr");
	 var cells_mutation=new Array;

	 cells_mutation[0]=document.createElement("td");	 
	 cells_mutation[0].style.fontSize="20px";
	 cells_mutation[0].style.color="black";
	 cells_mutation[0].style.fontFamily="TAHOMA";


	 cells_mutation[0].colSpan=3;
	 cells_mutation[0].appendChild(document.createTextNode("Dom Mutations"));


	 rows_mutation[0].appendChild(cells_mutation[0]);

	 tblBody_mutation.appendChild(rows_mutation[0]);
	
			var counter_mutation=0;
			var num_rows_mutation=1;
			rows_mutation[num_rows_mutation+2]=document.createElement("tr");
			for (var h=0; h<allEpisodes[i].getMutations().length; h++){

				if(counter_mutation==3){
					counter_mutation=0;
					num_rows_mutation++;
					rows_mutation[num_rows_mutation+2]=document.createElement("tr");

				}
				counter_mutation++;

				cells_mutation[h+3]=document.createElement("td");
				 var jjson = mutationsByEpisode[i][h];
				 var str=JSON.stringify(jjson);
				 var n=str.split(",");
				 var one=n[1].split(":")[1].replace("}"," ");
				 var two=n[3].split(":")[1].replace("}"," "); 

    			cells_mutation[h+3].appendChild(document.createTextNode(one+" "+two));
				cells_mutation[h+3].setAttribute('class','cell_source');


				rows_mutation[num_rows_mutation+2].appendChild(cells_mutation[h+3]);
				tblBody_mutation.appendChild(rows_mutation[num_rows_mutation+2]);
			}
			//tbl.setAttribute("border","3");
			tbl_mutation.appendChild(tblBody_mutation);
			tempDiv_mutation.appendChild(tbl_mutation);
			cell3SZ[i].appendChild(tempDiv_mutation);


  	//Get the trace for  zoom level 1.
  	 var url3 = 'http://localhost:8080/rest/clematis-api/'+fileName+'/episodes/'+i+'/trace';
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
	 //tblBody.appendChild(rows[1]);

	$.ajax({
		type: 'GET',
		url: url3 ,
		dataType: "json",
		async: false,
		success: function renderList4(data) {
	
			var counter=0;
			var num_rows=1;
			rows[num_rows+2]=document.createElement("tr");
			for (var h = 0; h<allEpisodes[currentEpisode].internalComponents.length; h++){

				if(counter==3){
					counter=0;
					num_rows++;
					rows[num_rows+2]=document.createElement("tr");

				}
				counter++;

				cells[h+3]=document.createElement("td");

			if (allEpisodes[currentEpisode].internalComponents[h] instanceof DOMEventTrace) {
			// DOM event, Actor should be created for sequence diagram

    			cells[h+3].appendChild(document.createTextNode('Event type:' + allEpisodes[currentEpisode].internalComponents[h].getEventType() ));

			} else if (allEpisodes[currentEpisode].internalComponents[h] instanceof XHREvent) {

			    cells[h+3].appendChild(document.createTextNode('XHR ID: ' + allEpisodes[currentEpisode].internalComponents[h].getXHRId().toString()));
	
			} else if (allEpisodes[currentEpisode].internalComponents[h] instanceof TimingTrace) {

    			cells[h+3].appendChild(document.createTextNode('TID: ' + allEpisodes[currentEpisode].internalComponents[h].getTimeoutId().toString()));
			} else {
			// Function trace, create lifeline

			    cells[h+3].appendChild(document.createTextNode(allEpisodes[currentEpisode].internalComponents[h].getName()));
	
			}


	      	
	    	

				cells[h+3].setAttribute('class','cell_source');


				rows[num_rows+2].appendChild(cells[h+3]);
				tblBody.appendChild(rows[num_rows+2]);
			}
			//tbl.setAttribute("border","3");
			tbl.appendChild(tblBody);
			tempDiv.appendChild(tbl);
	}
		});

  	

		cell2SZ[i].appendChild(tempDiv);
		jsPlumb.detachEveryConnection();
		$(divs[i]).replaceWith(episodeContents[i]);
		//$(divs[i]).replaceWith(episodeTable[i]);
		//tabs3.appendChild(episodeTable[i]);
		
	
	
			
		}
		
		
		else{
			zoomLevel1[i]=false;
			

			$(episodeContents[i]).replaceWith(divs[i]);
			cell1SZ[i].removeChild(cell1SZ[i].lastChild);
			cell2SZ[i].removeChild(cell2SZ[i].lastChild);
			cell3SZ[i].removeChild(cell3SZ[i].lastChild);
			//redrawLinks(temp_data);
			var counterForLinks=0;
			for (var x = 0; x < zoomLevel1.length; x++) {
				if(zoomLevel1[x]==false){
					counterForLinks++;
				}
			}
			if(counterForLinks==zoomLevel1.length){
				renderListLinks(temp_data);
			}
			
			//jsPlumb.repaintEverything();
			
		}

    }
  })(i, el), false);
}


function getType(data,i){
	if(data.trace[i].id==0 && data.trace[i].targetfunction.length>0){
		return 1;
	}
	else if(data.trace[i].id !=0 && (data.trace[i].xhrId==0  ||data.trace[i].xhrId!=0)){
		return 2;
	}
	else if(data.trace[i].id !=0 && (data.trace[i].timeoutId==0  ||data.trace[i].timeoutId!=0)){
		return 3;
	}
	else{
		return 4;
	}

}
//Function to zoom into the current episode(ZOOM level 2), the various components of the episode are created in  view.html, and are just being used here as is.
function expandCurrentEpisode(i){

	printDOMMutation2(i);
	viewEventInformation(lifeLinesByEpisode[i][0]);
	while (tabs1.hasChildNodes()) {
    tabs1.removeChild(tabs1.lastChild);
	}
	while (tabs2.hasChildNodes()) {
    tabs2.removeChild(tabs2.lastChild);
	}
	while (tabs3.hasChildNodes()) {
    tabs3.removeChild(tabs3.lastChild);
	}	

	menuAnchor2.appendChild(backButton);
	menuAnchor3.appendChild(forwardButton);

	anchor4.text="Episode  "+i;
	anchor3.text ="Trace of Episode "+i;

		tabs1.appendChild(first_column);
//get the DOM of a speceifc episode

		
		tabs2.appendChild(third_column);

//get the trace of a specefic episode

		var table_sequence=document.createElement('table');
		var row_sequence=document.createElement('tr');
		var row_extras=document.createElement('tr');
	
		var cell1_sequence=document.createElement('td');
		var cell2_code=document.createElement('td');


		row_sequence.appendChild(cell1_sequence);
		row_extras.appendChild(cell2_code);
		table_sequence.appendChild(row_sequence);
		table_sequence.appendChild(row_extras);

		
		cell1_sequence.appendChild(episodeTraceDiv[i]);


		//var div_code=second_column;//document.getElementById('second_column');
		cell2_code.appendChild(second_column);
		//code_div.appendChild(second_column);
		if (allEpisodes[i].getMutations().length > 0) {
    	
       	 var mutationNotification = document.createElement('div');
       	 mutationNotification.style.width="150px";
       	 mutationNotification.className = 'mutationnotification';
         mutationNotification.innerHTML="Mutation Present"
         tabs3.appendChild(mutationNotification);
   	 }
		tabs3.appendChild(table_sequence);
    	$(episodeContainer).replaceWith(tabs_div);

}


// function to navigate to the next or previous episode in zoom level 2

function nextPreviousEpisode(i){

	while (first_column.hasChildNodes()) {
    first_column.removeChild(first_column.lastChild);
	}
	while (third_column.hasChildNodes()) {
    third_column.removeChild(third_column.lastChild);
	}	

	printDOMMutation2(i);
	viewEventInformation(lifeLinesByEpisode[i][0]);
	console.log(divs_map.length);
	for (var j = 0; j < divs_map.length; j++) {
		 divs_map[j].setAttribute('class','box');
	};
	

	divs_map[i].setAttribute('class','box2');
	currentEpisode=i;
	tabs1.innerHTML = '';
	tabs2.innerHTML = '';
	tabs3.innerHTML = '';

	anchor4.text="Episode  "+i;
	anchor3.text="Trace of Episode "+i;
	var url = 'http://localhost:8080/rest/clematis-api/'+fileName+'/episodes/'+i+'/source';
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
		//tabs1.appendChild(eventType);
		tabs1.appendChild(first_column);
//get the DOM of a speceifc episode
 	var url2 = 'http://localhost:8080/rest/clematis-api/'+fileName+'/episodes/'+i;
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
		tabs2.appendChild(third_column);

//get the trace of a specefic episode
	var url3 = 'http://localhost:8080/rest/clematis-api/'+fileName+'/episodes/'+i+'/trace';
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

		var table_sequence=document.createElement('table');
		var row_sequence=document.createElement('tr');
		var row_extras=document.createElement('tr');

		var cell1_sequence=document.createElement('td');
		var cell2_code=document.createElement('td');


		row_sequence.appendChild(cell1_sequence);
		row_extras.appendChild(cell2_code);
		table_sequence.appendChild(row_sequence);
		table_sequence.appendChild(row_extras);

		
		cell1_sequence.appendChild(episodeTraceDiv[i]);


		//var div_code=second_column;//document.getElementById('second_column');
		cell2_code.appendChild(second_column);
		//code_div.appendChild(second_column);
		if (allEpisodes[i].getMutations().length > 0) {
    	
       	 var mutationNotification = document.createElement('div');
       	 mutationNotification.style.width="150px";
       	 mutationNotification.className = 'mutationnotification';
         mutationNotification.innerHTML="Mutation Present"
         tabs3.appendChild(mutationNotification);
   	 }
		tabs3.appendChild(table_sequence);


}

//function to redraw the causal links after returning to zoom level 0
function renderListLinks(data) {

		temp_data=data;
		for (var i = 0; i < data.length; i++) {
			var color_link=get_random_color();
			jsPlumb.connect({
                source: (cells[data[i].target]), //flipped these so arrows point in right direction
                target: (cells[data[i].source]),
                connector: ["Bezier",{ curviness:30 }],
                cssClass: "c1",
                endpoint: "Blank",
                endpointClass: "c1Endpoint",
                paintStyle: {
                    lineWidth: 1,
                    strokeStyle: color_link,
                    outlineWidth: 1,
                    outlineColor: "#666"
                },
                endpointStyle: {
                    fillStyle: "#a7b04b"
                },
            overlays:[ 
               ["Arrow", {location:.955, width:15, length:10}], 
   
           ]
                
            });
		};

	}
function redrawLinks(data) {
		for (var i = 0; i < data.length; i++) {
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
			//manualContinuousScrolling: true,
			autoScrollingMode: "onStart"
});
	

//variables to retrieve the tables that contain the episodes, these will be used to create the overview bar that is displayed in the menu.	
var tableNew;
var cellsNew;
var table2;
var cells2;


function get_random_color() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.round(Math.random() * 15)];
    }
    return color;
}


//Draw causal Links, first made a call to the REST api, then used a library called jsPlumb to draw lines between the episodes.
jsPlumb.bind("ready", function() {

	var url = 'http://localhost:8080/rest/clematis-api/'+fileName+'/story/causalLinks';
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
			var color_link=get_random_color();
			jsPlumb.connect({
                source: (divs[data[i].target]), //flipped these so arrows point in right direction
                target: (divs[data[i].source]),
                connector: ["Bezier",{ curviness:30 }],
                cssClass: "c1",
                endpoint: "Blank",
                endpointClass: "c1Endpoint",
                paintStyle: {
                    lineWidth: 1,
                    strokeStyle: color_link,
                    outlineWidth: 1,
                    outlineColor: "#666"
                },
                endpointStyle: {
                    fillStyle: "#a7b04b"
                },
            overlays:[ 
               ["Arrow", {location:.955, width:15, length:10}], 
   
           ]
                
            });
		};

	}

     });

$(document).ready(function(){

	menuSlider.init('menu','slide');
   	
});

//Create the overview bar on top of the series of episodes
//shade the current episode you are on.



    table2 = document.getElementById('tbl');
    cells2 = table2.getElementsByTagName('div');


for (var i = 0, n = cells2.length; i<n; i++) {
  var el = cells2[i];
  el.addEventListener('mouseover', (function(i, el) { 
    return function() {
      divs_map[i].setAttribute('class','box2');
      //cellsNew[i+1].setAttribute('class','box2');
      //cellsNew[i-1].setAttribute('class','box2');
    }
  })(i, el), false);
}

for (var i = 0, n = cells2.length; i<n; i++) {
  var el = cells2[i];
  el.addEventListener('mouseout', (function(i, el) { 
    return function() {
      divs_map[i].setAttribute('class','box');
      //cellsNew[i+1].setAttribute('class','box');
      //cellsNew[i-1].setAttribute('class','box');
    }
  })(i, el), false);
}



	
/************ Search on DOM event and its content ************/

function searchByDomEventType(eventType) {
	var matchedEpisodeIDs = [];
	
	for (var i = 0; i < globalEpisodeContainer.length; i ++) {
		if (globalEpisodeContainer[i].source.eventType != null) {
			if (globalEpisodeContainer[i].source.eventType == eventType) {
//				console.log("found");
				matchedEpisodeIDs.push(i);
			}
		}
	}

	return matchedEpisodeIDs;
}

function searchByDomElementKeyword(key) {
	var matchedEpisodeIDs = [];
	
	for (var i = 0; i < globalEpisodeContainer.length; i ++) {
		var eventType = globalEpisodeContainer[i].source.eventType;
		var eventHandler = globalEpisodeContainer[i].source.eventHandler;
		var targetElementAttributes = globalEpisodeContainer[i].source.targetElementAttributes;
		if (eventType != null && eventType.toLowerCase().indexOf(key) != -1) {
			console.log("eventType: ", eventType);
			matchedEpisodeIDs.push(i);
		}
		else if (eventHandler != null && eventHandler.toLowerCase().indexOf(key) != -1) {
			console.log("eventHandler: ", eventHandler);
			matchedEpisodeIDs.push(i);
		}
		else if (targetElementAttributes != null && targetElementAttributes.toLowerCase().indexOf(key) != -1) {
			console.log("targetElementAttributes: ", targetElementAttributes);
			matchedEpisodeIDs.push(i);
		}
	}

	return matchedEpisodeIDs;	
}

/************ search on all important text values in SOURCE ************/

function searchSourceByKeyword(key) {
	var matchedEpisodeIDs = [];
/*	
	for (var i = 0; i < globalEpisodeContainer.length; i ++) {
		if (type == dom) {
			searchByDomElementKeyword(key);
		}
		else if (type == timeoutcallback) {
			var callbackFunction = globalEpisodeContainer[i].source.callbackFunction; // todo
		}
		else if (type == xhrcallback) {
			var callbackFunction = globalEpisodeContainer[i].source.callbackFunction; // todo
			var response = globalEpisodeContainer[i].source.response; // todo
		}
	}
*/

	for (var i = 0; i < globalEpisodeContainer.length; i ++) {
		var source = globalEpisodeContainer[i].source;
		if (source.eventType != null && source.eventType.toLowerCase().indexOf(key) != -1) {
//			console.log("eventType: ", eventType);
			matchedEpisodeIDs.push(i);
		}
		else if (source.eventHandler != null && source.eventHandler.toLowerCase().indexOf(key) != -1) {
//			console.log("eventHandler: ", eventHandler);
			matchedEpisodeIDs.push(i);
		}
		else if (source.targetElementAttributes != null && source.targetElementAttributes.toLowerCase().indexOf(key) != -1) {
//			console.log("targetElementAttributes: ", targetElementAttributes);
			matchedEpisodeIDs.push(i);
		}
		else if (source.callbackFunction != null && source.callbackFunction.toLowerCase().indexOf(key) != -1) {
			matchedEpisodeIDs.push(i);
		}
		else if (source.response != null && source.response.toLowerCase().indexOf(key) != -1) {
			matchedEpisodeIDs.push(i);
		}
	}

	return matchedEpisodeIDs;	
}

function searchTraceByKeyword(key) {
	var matchedEpisodeIDs = [];
/*
	if (type = functionEnter or functionCall or functionExit or functionReturn) {
		var targetFunction
	}
	else if (type == timeoutSet) {
	}
	else if (type == timeoutCallback) {
	}
	else if (type == xhrOpen) {
	}
	else if (type == xhrSend) {
	}
	else if (type == xhrResponse) {
	}
*/	
	// OR JUST CHECK (FOR ALL FIELS) IF THE FIELD WAS NOT EMPTY OR NULL SEARCH IT

	for (var i = 0; i < globalEpisodeContainer.length; i ++) {
		var trace = globalEpisodeContainer[i].trace.trace;
		for (var j = 0; j < trace.length; j ++) {
			if (trace[j].targetFunction != null && trace[j].targetFunction.toLowerCase().indexOf(key) != -1) { // function stuff
//				console.log("found targetFunction ", trace[j].targetFunction);
				matchedEpisodeIDs.push(i);
			}
			else if (trace[j].callbackFunction != null && trace[j].callbackFunction.toLowerCase().indexOf(key) != -1) { // to callback and xhr response
//				console.log("found callbackFunction ", trace[j].callbackFunction);
				matchedEpisodeIDs.push(i);
			}
			else if (trace[j].methodType != null && trace[j].methodType.toLowerCase().indexOf(key) != -1) { // xhr open
//				console.log("found methodType ", trace[j].methodType);
				matchedEpisodeIDs.push(i);
			}
			else if (trace[j].url != null && trace[j].url.toLowerCase().indexOf(key) != -1) { // xhr open
//				console.log("found url ", trace[j].url);
				matchedEpisodeIDs.push(i);
			}
			else if (trace[j].message != null && trace[j].message.toLowerCase().indexOf(key) != -1) { // xhr send
//				console.log("found message ", trace[j].message);
				matchedEpisodeIDs.push(i);
			}
			else if (trace[j].response != null && trace[j].response.toLowerCase().indexOf(key) != -1) { // xhr response
//				console.log("found response ", trace[j].response);
				matchedEpisodeIDs.push(i);
			}
		}
	}

	return matchedEpisodeIDs;
}
