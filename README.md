Metis-Dev
=====

Monitoring, logging, and understanding JavaScript events.

## Installation

In order to integrate metis with your own web-application, you will need to import 3 JavaScript files:

```
.../js-instrumentation/eventlistenersMirror.js
.../js-instrumentation/asyncLogger.js
.../js-instrumentation/applicationView.js
```

Include each of the aforementioned scripts within your original HTML file. Specifically, 
the scripts must be imported from within your application's `<head>`. An example of this inclusion can be 
seen in the provided example web page ([.../example_webapplication/index.html](https://github.com/saltlab/metis/blob/master/example_webapplication/index.html), lines 13-15). After saving the updated HTML file, refresh the application within your browser to begin the instrumentation process.

### Enabling Logging

In order to view captured DOM events, XmlHttpRequests, and timing events, the active browser's console must 
be enabled. Users of Mozilla Firefox will need to install the Firebug plug-in to meet this requirement. 
Google Chrome users may make use of the browser's provided console. Once the console is enabled, 
output on events is enabled by calling the ``startRecord()`` method from the console command-line. Similarly, 
calling the ``stopRecord()`` method halts all output. 

Expected output upon successfully starting recording/logging:
```
====================================
RECORD STARTED
====================================
```

Expected output upon successfully pausing logging:
```
====================================
RECORD STOPPED
==================================== 
```

Metis has been tested successfully with both Firefox and Chrome with the following example output for a captured DOM event: 

```
DOM EVENT HANDLED
Time: ( 2013 - 0 - 4   7 : 58 : 6 : 629 )
 + Event type:  click 
 + Target DOM element:  <table id="table1" border="1">�</table>
 + Handler function:  function checkForm() {
	var val1 = document.getElementById("instName").value;
	var val2 = document.getElementById("majorName").value;
	
	if (val1.length == 0 || val2.length == 0) {
		document.getElementById("submitMsg").innerHTML = "Form NOT submitted";
	}
}
```

More documentation (and fewer bugs) coming soon.

## Contributing

Your feedback is valued! Please use the [Issue tracker](https://github.com/saltlab/metis/issues) for reporting bugs or feature requests.

## Notes

The provided demo application [.../example_webapplication/index.html](https://github.com/saltlab/metis/blob/master/example_webapplication/index.html) is not fully compatible with Google Chrome. Specifically, an attempt to retrieve data from a local file is made by each of the 3 XMLHttpRequests within the example/demo (GET, POST, Delayed). These type of local access operation is not permitted from Chrome as a seurity feature ([See More](http://renard.github.com/o-blog/faq.html)). Therefore, the following output is expected when attempting to load local files through Chrome: 

```
XMLHttpRequest cannot load file://.../metis/example_webapplication/local_url.txt. Cross origin requests are only supported for HTTP.
Uncaught Error: NETWORK_ERR: XMLHttpRequest Exception 101
```

