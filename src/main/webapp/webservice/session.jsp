<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="include.jsp"/>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Clematis</title>
	<script src="../fish-eye-zoom/javascripts/jquery-1.9.1.js"></script>
	<script src="resources/jquery.ba-replacetext.js"></script>
    <!--script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script-->
    <script src="http://code.jquery.com/jquery-1.11.1.js"></script>
    <script src="js/bootstrap.min.js"></script>


    <!-- Bootstrap core CSS -->
    <!--link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css"-->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/cover.css" rel="stylesheet">
    
  </head>

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
            <shiro:guest><li><a href="/webservice/view.jsp">View Trace</a></li></shiro:guest>
            <shiro:user><li><a href="<c:url value="/logout"/>">Log out</a></li></shiro:user>
          </ul>
          
          <ul class="nav navbar-nav navbar-right">
          	<li><a id = "usernamechange">Hi <shiro:guest>Guest</shiro:guest><shiro:user><shiro:principal/></shiro:user>!</a>
		    </li>
	      </ul>
        </div><!--/.nav-collapse -->
       
      </div>
    </div>
    
<script type="text/javascript">

var name = document.getElementById('usernamechange').innerHTML;
console.log(name);
if(name.indexOf("Guest") > -1){
	if (name.length > 9){
		console.log(name.substring(0,8)+"!");
		document.getElementById('usernamechange').innerHTML = name.substring(0,8)+"!";
	}
}

</script>

<script type="text/javascript">
	  //var sessionID = <?php echo json_encode($_GET["sessionID"]); ?>;
	  function getUrlParameter(sParam)
	  {
	    var sPageURL = window.location.search.substring(1);
	    var sURLVariables = sPageURL.split('&');
	    for (var i = 0; i < sURLVariables.length; i++) 
	    {
	        var sParameterName = sURLVariables[i].split('=');
	        if (sParameterName[0] == sParam) 
	        {
	            return sParameterName[1];
	        }
	    }
    }
    
	  var url = getUrlParameter('url');
	  /*$.ajax({
	    type: 'GET',
	    url: 'http://localhost:8080/rest/clematis-api/test/' + sessionID ,
	    dataType: "text",
	    async: false,
	    success: function hi(data){
	    }
	  });*/
	  
</script>   

<div id="testLoad"></div>

<div class="container">  
<div class="view">

	<iframe id="page" src="about:blank" width="100%" height="110%" frameborder="1"></iframe>

</div>
</div>

<div id="INPUT"></div>


<script type="text/javascript">
	//var element = document.getElementById("page").setAttribute("src", "http://localhost:8080/rest/clematis-api/redirectHTML?url=" + url + "&null");
	//var a ="<script"+" src"+"=\"../fish-eye-zoom/javascripts/jquery.rest.min.js\">"+"<"+"/"+"script>"; 
	
	var b = "<script"+" src"+"=\"../fish-eye-zoom/javascripts/jquery-ui-1.10.3.custom.js\">"+"<"+"/"+"script>"; 
	var a="<script"+" src"+"=\"resources/jquery.rest.min.js\">"+"<"+"/"+"script>";
	var c = "<script"+" src"+"=\"http://code.jquery.com/jquery-1.11.1.js\">" + "<"+"/"+"script>"; 
 	var d = "<script"+" src"+"=\"resources/jquery.ba-replacetext.js\">"+"<"+"/"+"script>";
 	var e = "<script"+" src"+"=\"../fish-eye-zoom/javascripts/jquery-1.9.1.js\">"+"<"+"/"+"script>"; 
	
	var l = "<script"+" src"+"=\"resources/toolbar-clematis/toolbar.js\">"+"<"+"/"+"script>"; 
	var m = "<script"+" src"+"=\"resources/esprima.js\">"+"<"+"/"+"script>";
	var n = "<script"+" src"+"=\"resources/esmorph.js\">"+"<"+"/"+"script>";
	var o = "<script"+" src"+"=\"resources/jsonml-dom.js\">"+"<"+"/"+"script>";
	var p = "<script"+" src"+"=\"resources/addvariable.js\">"+"<"+"/"+"script>";
	var q = "<script"+" src"+"=\"resources/asyncLogger.js\">"+"<"+"/"+"script>";
	var r = "<script"+" src"+"=\"resources/applicationView.js\">"+"<"+"/"+"script>";
	var s = "<script"+" src"+"=\"resources/instrumentDOMEvents.js\">"+"<"+"/"+"script>";
	var t = "<script"+" src"+"=\"resources/domMutations.js\">"+"<"+"/"+"script>";
	var u = "<script"+" src"+"=\"resources/mutation_summary.js\">"+"<"+"/"+"script>";
	var v = "<script"+" src"+"=\"resources/toolbar-clematis/jquery-ui-1.10.2.custom.js\">"+"<"+"/"+"script>";
	var w = "<script"+" src"+"=\"resources/toolbar-clematis/jquery.tipsy.js\">"+"<"+"/"+"script>";
	var x = "<script"+" src"+"=\"resources/domMutations.js\">"+"<"+"/"+"script>";
	var y = "<link"+" href"+"=\"resources/toolbar-clematis/css/jquery-ui-1.10.2.custom.css\" rel=\"stylesheet\">";
	var z = "<link"+" href"+"=\"resources/toolbar-clematis/css/tipsy.css\" rel=\"stylesheet\">";
	
	//send request to server for url
	$.ajax({
		    type: 'GET',
		    url: 'http://localhost:8080/rest/clematis-api/redirectHTML?url=' + url + '&null/',
		    dataType: "text",
		    contentType: "text/html",
		    async: false,
		    success: function successfulSessionStarted(data) {
		  		var doc = document.getElementById('page').contentWindow.document;
				doc.open();

				data = data + e + c + d + b + a + y + z   + m+n+o+p+q+r+s+t+u+v+w+x+l ;
				
				var doc = document.getElementById('page').contentWindow.document;
				doc.write(data);
				doc.close();

		    }
	    });
	
	//on iframe load - inject javascript with timestamp		
	$('#page').load(function() {
		//if areWeRecording is true, then inject timestamp and toolbar again?
		$.ajax({
		    type: 'GET',
		    url: 'http://localhost:8080/rest/clematis-api/areWeRecording',
		    dataType: "text",
		    contentType: "text/plain",
		    async: false,
		    success: function successfulSessionStarted(data) {
				console.log("Are We Recording? " + data);
				
				if (data == "true" ){
					var counter = 0; 
					
					$.ajax({
					    type: 'GET',
					    url: 'http://localhost:8080/rest/clematis-api/areWeRecordingCounter',
					    dataType: "text",
					    contentType: "text/plain",
					    async: false,
					    success: function successfulSessionStarted(data) {
							console.log("Counter: " + data);
							counter = data;
					    }
				    });
					
					var doc = document.getElementById('page').contentWindow.document;
					var html = doc.body.innerHTML;
					var resume = "<script>"+ "resumeRecording(" + counter + ");" + "<"+"/"+"script>"; 
						
					doc.write(html  +e + c + d + b + a + y + z   + m+n+o+p+q+r+s+t+u+v+w+x+l + resume);
					doc.close();
				}
				
		    }
	    });
	});
	
</script>


</body>
</html>
