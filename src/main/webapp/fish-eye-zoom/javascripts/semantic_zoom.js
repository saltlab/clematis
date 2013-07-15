	
	
	var episodeContents = document.createElement("div");
	episodeContents.id=("episode-Contents");

	
	episodeContents.style.border = " solid black";
	episodeContents.style.display="table";
	
	episodeContents.style.left="390px";
	episodeContents.style.top="120px";
	

	var tbl = document.createElement("table");
	var tblBody = document.createElement("tbody");
	
	var row = document.createElement("tr");
	var row2 = document.createElement("tr");
    var cell = document.createElement("td");
    cell.id="CELL1";
    
    var cell2= document.createElement("td");
    cell2.id="CELL2";

    
    row.appendChild(cell);
    row2.appendChild(cell2);
    
    tblBody.appendChild(row);
    tblBody.appendChild(row2);
    
    var row3 = document.createElement("tr");
    var cell3 = document.createElement("td");
    cell3.id="CELL3";

    row3.appendChild(cell3);
    tblBody.appendChild(row3); 
    tbl.appendChild(tblBody);
    tbl.setAttribute("border","6");

   
    
    
    episodeContents.appendChild(tbl);


	$(episodeContents).appendTo("body");
