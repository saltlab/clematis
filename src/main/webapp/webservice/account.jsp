<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.clematis.database.MongoInterface" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.apache.shiro.subject.Subject" %>

<jsp:include page="/webservice/include.jsp"/>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Clematis</title>

    <!--script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script-->
    <script src="http://code.jquery.com/jquery-1.11.1.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../fish-eye-zoom/javascripts/jquery.rest.min.js"></script>
    <script type="text/javascript" src="../fish-eye-zoom/javascripts/jquery-ui-1.10.3.custom.js"></script>
    <script type="text/javascript" src="../fish-eye-zoom/jquery-live-preview.js"></script>
    
    <!-- Bootstrap core CSS -->
    <!--link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css"-->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/cover.css" rel="stylesheet">
    <link href="css/livepreview-demo.css" rel="stylesheet" type="text/css">
    
  </head>
    <script type="text/javascript">
            $(document).ready(function() {
                $(".livepreview").livePreview();
            });
    </script>
  
  <script type="text/javascript">
  var lastSessionNum;
  $.ajax({
		    type: 'GET',
		    url: 'http://localhost:8080/rest/clematis-api/sessions',
		    dataType: "json",
		    async: false,
		    success: function successfulSessionRetrieval(data) {
		      lastSessionNum=data;
		    }
	    });  
  </script>


  <body data-spy="scroll" data-target=".masthead">
    

      <!-- Fixed navbar -->
    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          
          <a class="navbar-brand" href="/webservice/home.jsp">Clematis</a>
        </div>
        <div class="navbar-collapse collapse">
           
          <ul class="nav navbar-nav">
            <li class="active"><a href="/webservice/home.jsp" class="scroll-link" data-id="newSession">New Session</a></li>
            <li><a href="#" class="scroll-link" data-id="about">About</a></li>
           <shiro:user> <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">Account <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li><a href="/webservice/account.jsp">View Sessions</a></li>
                <li><a href="#">Edit Account</a></li>
              </ul>
            </li>
            </shiro:user>
            <shiro:guest><li><a href="/webservice/home.jsp#login" class="scroll-link" data-id="login">Login</a></li></shiro:guest>
            <shiro:user><li><a href="<c:url value="/logout"/>">Log Out</a></li></shiro:user>
          </ul>
          
          <ul class="nav navbar-nav navbar-right">
          	<li><a>Hi <shiro:guest>Guest</shiro:guest><shiro:user><shiro:principal/></shiro:user>!</a>
		    </li>
	      </ul>
        </div><!--/.nav-collapse -->
       
      </div>
    </div>
    
    
    <div class="container">
    	<div class="account-title">
    		<h3>Recorded Sessions</h1>
    	</div>
    	
   
    	 	 
    	 	 <div id ="sessionT"></div>

    	 	 <table class="table" id="newTable"></table>
	    	<!--div class="col-sm-4"><a href="http://localhost:8080/fish-eye-zoom/view.html?sessionID=1">Session 1</a></div>
	    	<div class="col-sm-8">
		    	<iframe src="http://localhost:8080/fish-eye-zoom/view.html" width="700" height="100" frameborder="0"></iframe>
	    	</div-->

	    	</div>
    	

    
    <script>
		$.getJSON("http://localhost:8080/rest/clematis-api/sessions", function(data) {
	      var jsonString = JSON.stringify(data);
	      //document.getElementById("sessionT").innerHTML = data[17][0];
	      
	      var table = document.getElementById("newTable");
	      //var url = "http://localhost:8080/fish-eye-zoom/view.html";
	      var url = "http://localhost:8080/webservice/view.jsp";
	      
	      for (var i = 1; i <= data.length; i++){
		      var row = table.insertRow(0);
	      	var cell1 = row.insertCell(0);
	      	var cell2 = row.insertCell(1);
	      	var cell3 = row.insertCell(2);
	      	var cell4 = row.insertCell(3);
	      	
	      	if(data[i-1]!=null){
		      	cell1.innerHTML = data[i-1][0];
		      	cell2.innerHTML = data[i-1][1];
		      	cell3.innerHTML = data[i-1][2];
		      	cell4.innerHTML = "<a href="+url+"?sessionID="+i+">View Trace</a>"
	      	}
	      	
        }
        
        var header = table.createTHead();
	      var row = header.insertRow(0);
	      var cellHeader1 = row.insertCell(0);
	      var cellHeader2 = row.insertCell(1);
	      var cellHeader3 = row.insertCell(2);
	      var cellHeader4 = row.insertCell(3);
	      cellHeader1.innerHTML = "<b>Session Number</b>";
	      cellHeader2.innerHTML = "<b>Date Captured</b>";
	      cellHeader3.innerHTML = "<b>Trace URL</b>";
	      cellHeader4.innerHTML = "<b>Trace Link</b>";
	      
    });
    </script>
 


    
  </body>
</html>