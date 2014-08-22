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
          
          <a class="navbar-brand" href="#">Clematis</a>
        </div>
        <div class="navbar-collapse collapse">
           
          <ul class="nav navbar-nav">
            <li class="active"><a href="#" class="scroll-link" data-id="newSession">New Session</a></li>
            <li><a href="#" class="scroll-link" data-id="about">About</a></li>
           <shiro:user> 
           
            <li><a href="/webservice/account.jsp">View Sessions</a></li>
            
            </shiro:user>
            <shiro:guest><li><a href="#" class="scroll-link" data-id="login">Login</a></li></shiro:guest>
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

function guestRefresh(){
	$.ajax({
		type: 'GET',
		url: 'http://localhost:8080/rest/clematis-api/account/signInAsGuest',
		dataType: "text",
		contentType: "application/json",
		async: true,
		success: function successfulGuestSignIn(data) {
			location.reload();
		}
	});
};

</script>
<shiro:user>
    <div class="container">
    
  
	    <div class="main" id = "newSession">
            <h1 class="cover-heading">Welcome to Clematis</h1>
            <p class="lead">Enter a URL to begin:</p>
            <shiro:guest>Or <a href="#" class="scroll-link" data-id="login">Login</a> to save your session to your account!</shiro:guest>
            <div class="row">
	            <div class="col-sm-12">
		            
		            <div class="input-group">
				          <input id="URL" type="text" class="form-control" placeholder="www.example.com">
				           <span class="input-group-btn">
				            <button id="Start" class="btn btn-default" type="button">Go!</button>
				          </span>
				        </div>
				        
				      </div>
            </div> 
            <div class = "row">
            	<!-- p id="demo"--><!-- /p-->
				      <p id="urlsuccess"> </p>
            </div>
          </div>

    </div>
 </shiro:user>   
    
    <shiro:guest>
    <div class = "container">
    <div class = "main">
      <!-- Main component for a primary marketing message or call to action -->

	     <div class="container">
			  <div class="row">
			
			    <div class="main-login">
			
			      <h3>Please Log In, <a href="#" data-toggle="modal" data-target="#signup">Sign Up</a></h3>
			      <h4><a href="javascript:guestRefresh();"> or Sign In as Guest </a></h3>
			      <div class="login-or">
			        <hr class="hr-or">
			        <span class="span-or"></span>
			      </div>
			
			      <form name="loginform" action="" method="POST" accept-charset="UTF-8" role="form">
			        <div class="form-group" id = "login">
			          <label for="inputUsernameEmail">Username or email</label>
			          <!--  input type="text" class="form-control" id="inputUsernameEmail"-->
			          <input type="text" class="form-control" name="username" value ="">
			        </div>
			        <div class="form-group">
			          <a class="pull-right" href="#">Forgot password?</a>
			          <label for="inputPassword">Password</label>
			          <input type="password" class="form-control" name="password" id="inputPassword">
			        </div>
			        <button type="submit" class="btn btn btn-primary">
			          Log In
			        </button>
			      </form>
			      
			    
			    </div>
			    
			  </div>
			</div>
	     
		   <a href="#" class="scroll-top back-to-top">&uarr;</a>

		  <!-- /div-->
    </div>
    </div> <!-- /container -->

<!-- Modal -->
<div class="modal fade" id="signup" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
        <h4 class="modal-title" id="myModalLabel">Sign Up</h4>
      </div>
      <form name="createAccount" id="createAccount" action="" method="POST">
      <div class="modal-body">
				
					<div class="container">
					    <div class="row">
					        <div class="col-sm-12">
					        <div class="signup-form">
					            <input class="form-control" name="username" placeholder="Username or Email" type="email" id="username" />
					        </div>
					        <div class="signup-form">
					            <input class="form-control" name="password" placeholder="New Password" type="password" id="password"/>
					        </div>
					        <div class="signup-form">
					            <input class="form-control" name="reenterpassword" placeholder="Re-Enter Password" type="password" id="reenterpassword"/> 
					        </div>  
					        </div>
					    </div>
					</div>
				<p id="signupsuccess"> </p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary" type="submit" id="newUser">Sign Up</button>
      </div>
 	 </form>
    </div>
  </div>
</div>
    
   </shiro:guest>

		<script type="text/javascript">
		$(document).ready(function() {
			// navigation click actions	
			$('.scroll-link').on('click', function(event){
				event.preventDefault();
				var sectionID = $(this).attr("data-id");
				scrollToID('#' + sectionID, 750);
			});
			// scroll to top action
			$('.scroll-top').on('click', function(event) {
				event.preventDefault();
				$('html, body').animate({scrollTop:0}, 'slow'); 		
			});
			// mobile nav toggle
			$('#nav-toggle').on('click', function (event) {
				event.preventDefault();
				$('#main-nav').toggleClass("open");
			});
		});
		// scroll function
		function scrollToID(id, speed){
			var offSet = 50;
			var targetOffset = $(id).offset().top - offSet;
			var mainNav = $('#main-nav');
			$('html,body').animate({scrollTop:targetOffset}, speed);
			if (mainNav.hasClass("open")) {
				mainNav.css("height", "1px").removeClass("in").addClass("collapse");
				mainNav.removeClass("open");
			}
		}
		/*if (typeof console === "undefined") {
		    console = {
		        log: function() { }
		    };
		}*/
		</script>
    
    <script>
    document.getElementById("Start").onclick = function(){startClematisSession();};
    document.getElementById("newUser").onclick = function(){userSignUp();};
    //document.getElementById("LoginButton").onclick = function(){userLogin()};
    
    function startClematisSession(){
      var URLstring = document.getElementById("URL").value;
	    //document.getElementById("demo").innerHTML = URLstring;

	    /*$.ajax({
		    type: 'POST',
		    url: 'http://localhost:8080/rest/clematis-api/startSessionPOST/',
		    dataType: "text",
		    data: JSON.stringify({urlSENT : URLstring}),
		    contentType: "application/json",
		    async: false,
		    success: function successfulSessionStarted(data) {
		      var result=data;
		      document.getElementById("urlsuccess").innerHTML = data;
		    }
	    });*/
	   window.open("http://localhost:8080/webservice/session.jsp?url="+URLstring,"_self");
 
    }
    
    
    function userSignUp(){
    	if (document.getElementById("password").value != document.getElementById("reenterpassword").value){
    		document.getElementById("signupsuccess").innerHTML = "Passwords do not match!";
    	}
    	else{
    		document.getElementById("signupsuccess").innerHTML = " ";
	    	var signupData = $('#createAccount').serializeArray();
	    	
	    	//var signupData = "hi";
	    	$.ajax({
	    		type: 'POST',
	    		url: 'http://localhost:8080/rest/clematis-api/account/create/',
	    		dataType: "text",
	    		//data: JSON.stringify(signupData),
	    		data:signupData,
	    		contentType: "application/json",
	 		    async: false,
	 		    success: function successfulUserSignup(data) {
	 		    	$('#signup').modal('hide');
	 		    }
	    	});
    	}
    }
    
    //callback handler for form submit
    $("#ajaxform").submit(function(e)
		{
		    var postData = $(this).serializeArray();
		    var formURL = $(this).attr("action");
		    $.ajax(
		    {
		        url : formURL,
		        type: "POST",
		        dataType:"text",
		        data : JSON.stringify(postData),
		        contentType:"application/json",
		        success:function(data) 
		        {
		            var result = data;
		            document.getElementById("loginsuccess").innerHTML = data;
		        },
		        error: function(jqXHR, textStatus, errorThrown) 
		        {
		            //if fails      
		        }
		    });
		    e.preventDefault(); //STOP default action
		    e.unbind(); //unbind. to stop multiple form submit.
		});
 
		$("#ajaxform").submit(); //Submit  the FORM
    
 
    
   </script>

  </body>
</html>
