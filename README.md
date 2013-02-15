Metis
=====

Monitoring, logging, and understanding JavaScript events.

## Installation

Currrently, the Metis project is designed to run from within the Eclipse IDE. In terms of installation, setting up the project is easier than ever. Simply checkout the project from GitHub and import it into Eclipse as an existing Maven project (File > Import... > Maven > Existing Maven Projects). In order to do this you will need the [m2e plugin for Eclipse](http://eclipse.org/m2e/download/). This provides Maven integration for Eclipse and simplifies the handling of project dependencies. It may take a few minutes to compile Metis after the first import.

## Configuration

Upon checkout, Metis contains a simple application for testing the tracing process ([src/main/webapp/example\_webapplication](https://github.com/saltlab/metis-dev/tree/master/src/main/webapp/example_webapplication)). This example application contains some basic synchronous and asynchronous JavaScript mechanisms. In order to test your own web-application using Metis, place the appropriate application files in the [webapp](https://gthub.com/saltlab/metis-dev/tree/master/src/main/webapp/) folder in accordance with the [Jetty guidelines](http://wiki.eclipse.org/Jetty/Howto/Deploy_Web_Applications).

In addition to adding your application to the webapp/ folder, you will also need to set your web-application as the target for Metis. This done from the [SimpleExample](https://github.com/saltlab/metis-dev/blob/master/src/main/java/com/metis/core/SimpleExample.java) class (line 26). 

```
.../js-instrumentation/eventlistenersMirror.js
.../js-instrumentation/asyncLogger.js
.../js-instrumentation/applicationView.js
```

Include each of the aforementioned scripts within your original HTML file. Specifically, 
the scripts must be imported from within your application's `<head>`. An example of this inclusion can be 
seen in the provided example web page ([.../example_webapplication/index.html](https://github.com/saltlab/metis/blob/master/example_webapplication/index.html), lines 13-15). After saving the updated HTML file, refresh the application within your browser to begin the instrumentation process.

More documentation (and fewer bugs) coming soon.

## Contributing

Your feedback is valued! Please use the [Issue tracker](https://github.com/saltlab/metis/issues) for reporting bugs or feature requests.

## Notes

The provided demo application [.../example_webapplication/index.html](https://github.com/saltlab/metis/blob/master/example_webapplication/index.html) is not fully compatible with Google Chrome. Specifically, an attempt to retrieve data from a local file is made by each of the 3 XMLHttpRequests within the example/demo (GET, POST, Delayed). This type of local access operation is not permitted from Chrome as a seurity feature ([See More](http://renard.github.com/o-blog/faq.html)). Therefore, the following output is expected when attempting to load local files through Chrome: 

```
XMLHttpRequest cannot load file://.../metis/example_webapplication/local_url.txt. Cross origin requests are only supported for HTTP.
Uncaught Error: NETWORK_ERR: XMLHttpRequest Exception 101
```

