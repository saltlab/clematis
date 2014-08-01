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
    <!--script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script-->
    <script src="http://code.jquery.com/jquery-1.11.1.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../fish-eye-zoom/javascripts/jquery.rest.min.js"></script>
    <script type="text/javascript" src="../fish-eye-zoom/javascripts/jquery-ui-1.10.3.custom.js"></script>
    <script type="text/javascript" src="resources/esprima.js"></script>
    <script type="text/javascript" src="resources/esmorph.js"></script>
    <script type="text/javascript" src="resources/jsonml-dom.js"></script>
    <script type="text/javascript" src="resources/addvariable.js"></script>
    <script type="text/javascript" src="resources/asyncLogger.js"></script>
    <script type="text/javascript" src="resources/applicationView.js"></script>
    <script type="text/javascript" src="resources/instrumentDOMEvents.js"></script>
    <script type="text/javascript" src="resources/domMutations.js"></script>
    <script type="text/javascript" src="resources/mutation_summary.js"></script>
    <script type="text/javascript" src="resources/toolbar-clematis/jquery-ui-1.10.2.custom.js"></script>
    <script type="text/javascript" src="resources/toolbar-clematis/jquery.tipsy.js"></script>
    <link href="resources/toolbar-clematis/css/jquery-ui-1.10.2.custom.css" rel="stylesheet">
    <link href="resources/toolbar-clematis/css/tipsy.css" rel="stylesheet">
    <script type="text/javascript" src="resources/toolbar-clematis/toolbar.js"></script>
    
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
            <shiro:user><li><a href="<c:url value="/logout"/>">Log out</a></li></shiro:user>
          </ul>
          
          <ul class="nav navbar-nav navbar-right">
          	<li><a>Hi <shiro:guest>Guest</shiro:guest><shiro:user><shiro:principal/></shiro:user>!</a>
		    </li>
	      </ul>
        </div><!--/.nav-collapse -->
       
      </div>
    </div>
    
    

<script>
function myFunction() {
    var person = prompt("Please enter your name", "Harry Potter");
    
    if (person != null) {
        document.getElementById("demo").innerHTML =
        "Hello " + person + "! How are you today?";
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
    
<div class="container">  
<div class="view">

	<!--  iframe id="page" src="" width="100%" height="100%" frameborder="0" ></iframe>-->
	<div id="testLoad"></div>
	<button onclick="myFunction()">Try it</button>

	<p id="demo"></p>
</div>
</div>

<script type="text/javascript">
	var element = document.getElementById("page").setAttribute("src", url);
	
	//$("#testLoad").load("http://www.themaninblue.com/experiment/BunnyHunt/");

	/*var l="<script"+" src"+"=\"resources/toolbar-clematis/toolbar.js\">"+"<"+"/"+"script>"; 
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
	var y = "<link"+" href"+"=\"resources/toolbar-clematis/css/jquery-ui-1.10.2.custom.css\">";
	var z = "<link"+" href"+"=\"resources/toolbar-clematis/css/tipsy.css\">";

	
	$('#page').contents().find('head').append(l);
	$('#page').contents().find('head').append(m);
	$('#page').contents().find('head').append(n);
	$('#page').contents().find('head').append(o);
	$('#page').contents().find('head').append(p);
	$('#page').contents().find('head').append(q);
	$('#page').contents().find('head').append(r);
	$('#page').contents().find('head').append(s);
	$('#page').contents().find('head').append(t);
	$('#page').contents().find('head').append(u);
	
	$('#page').contents().find('head').append(l);
	
	$('#page').contents().find('head').append(v);
	$('#page').contents().find('head').append(w);
	$('#page').contents().find('head').append(x);
	$('#page').contents().find('head').append(y);
	$('#page').contents().find('head').append(z);
	*/

	
</script>


</body>
</html>
