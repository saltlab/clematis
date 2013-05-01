Clematis
=====

Monitoring, logging, and understanding JavaScript events.

## Application Dependencies

Currrently, the Clematis project is designed to run from within the Eclipse IDE. Additionally, both [Mozilla Firefox](http://www.mozilla.org/en-US/firefox/new/) and [Apache Maven](http://maven.apache.org/download.cgi) will be needed to successfully run this project. Support for Google Chrome is planned for future iterations of Clematis.

## Installation

In terms of installation, setting up the project is easier than ever. Simply checkout the project from GitHub and import it into Eclipse as an existing Maven project (File > Import... > Maven > Existing Maven Projects). In order to do this you will need the [m2e plugin for Eclipse](http://eclipse.org/m2e/download/). This provides Maven integration for Eclipse and simplifies the handling of project dependencies. Please note that it may take a few minutes to compile Clematis after the first import.

## Configuration

Upon checkout, Clematis contains a simple application for testing the tracing process ([src/main/webapp/example\_webapplication](https://github.com/saltlab/clematis/tree/master/src/main/webapp/example_webapplication)). This example application contains some basic synchronous and asynchronous JavaScript events. In order to test your own web-application using Clematis, place the appropriate application files in the [webapp](https://github.com/saltlab/clematis/tree/master/src/main/webapp/) folder in accordance with the [Jetty guidelines](http://wiki.eclipse.org/Jetty/Howto/Deploy_Web_Applications).

In addition to adding your application to the webapp/ folder, you will also need to set your web-application as the target for Clematis. This is done from the [SimpleExample](https://github.com/saltlab/clematis/blob/master/src/main/java/com/metis/core/SimpleExample.java) class (line 26). 

## Running the Tool 

The Jetty server must be started before running Clematis. First, navigate to the root directory of Clematis (where you checked-out clematis) and execute the following from your command-line (Terminal, etc.):

```
mvn jetty:run
```

If successful, a notification should appear confirming that the server is up-and-running (``[INFO] Started Jetty Server``). Next, run the Clematis project as a Java application from Eclipse by setting [com.metis.core.SimpleExample](https://github.com/saltlab/clematis/blob/master/src/main/java/com/metis/core/SimpleExample.java) as the Main class. Once a new browser session is started by Clematis, feel free to use your application as you normally would. Quitting Firefox (Cmd+Q) notifies Clematis that your user session is over and the generated trace files can be found in the 'metis-output' directory at the root of clematis.

More documentation (and fewer bugs) coming soon.

## Contributing

Your feedback is valued! Please use the [Issue tracker](https://github.com/saltlab/clematis/issues) for reporting bugs or feature requests.

## Notes

The provided demo application [.../example_webapplication/index.html](https://github.com/saltlab/clematis/blob/master/example_webapplication/index.html) is not fully compatible with Google Chrome. Specifically, an attempt to retrieve data from a local file is made by each of the 3 XMLHttpRequests within the example/demo (GET, POST, Delayed). This type of local access operation is not permitted from Chrome as a seurity feature ([See More](http://renard.github.com/o-blog/faq.html)). Therefore, the following output is expected when attempting to load local files through Chrome: 

```
XMLHttpRequest cannot load file://.../metis/example_webapplication/local_url.txt. Cross origin requests are only supported for HTTP.
Uncaught Error: NETWORK_ERR: XMLHttpRequest Exception 101
```

