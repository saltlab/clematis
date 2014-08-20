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
          	<li><a id = "usernamechange">Hi <shiro:guest>Guest</shiro:guest><shiro:user><shiro:principal/></shiro:user>!</a>
		    </li>
	      </ul>
        </div><!--/.nav-collapse -->
       
      </div>
    </div>
    

	<div class = "backbutton" > 
	<div class = "row">
	<div class="col-md-4">
		<a href="/webservice/account.jsp">
		  <span class="glyphicon glyphicon-chevron-left"></span>View All Sessions
		</a>
	</div>
	<div class="col-md-4">
		<h4 id="number" align="center"></h4>
	</div>

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
    
	  var sessionID = getUrlParameter('sessionID');
	  if (sessionID == null){
		  sessionID = 1;
	  }
	  document.getElementById("number").innerHTML = "Session " + sessionID;
	 
	  
	  $.ajax({
	    type: 'GET',
	    url: 'http://localhost:8080/rest/clematis-api/test/' + sessionID ,
	    dataType: "text",
	    async: false,
	    success: function hi(data){
	    }
	  });
	  
	  var url = "http://localhost:8080/fish-eye-zoom/view.html" + "?sessionID=" + sessionID;
</script>    
    
<div class="container">  
<div class="viewSession">
	<iframe id="page" src="" width="100%" height="100%" frameborder="0" ></iframe>
</div>
</div>

<script type="text/javascript">
	var element = document.getElementById("page").setAttribute("src", url);
</script>


</body>
</html>
