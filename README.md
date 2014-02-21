Clematis
=====

Clematis allows developers to easily understand the complex dynamic event-driven behaviour of their web application. It automatically captures traces of JavaScript code execution and  generates an interactive  visualization at three different  levels of granularity, representing episodes of triggered causal and temporal events, related JavaScript code executions, and their impact on the DOM.

For more technical documentation see the [wiki](https://github.com/saltlab/clematis/wiki).

## Paper

The technique behind Clematis is published as a research paper at ICSE 2014. It is titled ["Understanding JavaScript Event-based Interactions"](http://salt.ece.ubc.ca/publications/saba_clematis.html) and is available as a PDF.


## Using Clematis

Currrently, the Clematis project is designed to run from within the Eclipse IDE. Additionally, both [Mozilla Firefox](http://www.mozilla.org/en-US/firefox/new/) and [Apache Maven](http://maven.apache.org/download.cgi) will be needed to successfully run this project. Support for Google Chrome is planned for future iterations of Clematis.

### Installation

In terms of installation, setting up the project is easier than ever. Simply checkout the project from GitHub and import it into Eclipse as an existing Maven project (File > Import... > Maven > Existing Maven Projects). In order to do this you will need the [m2e plugin for Eclipse](http://eclipse.org/m2e/download/). This provides Maven integration for Eclipse and simplifies the handling of project dependencies. Please note that it may take a few minutes to compile Clematis after the first import.

### Configuration

Clematis has been tested with the photo gallery application [Phormer](http://p.horm.org/er/). This example application contains some basic synchronous and asynchronous JavaScript events. To use Clematis with the Phormer gallery application, download Phormer and deploy it locally using a personal webserver such as [MAMP](http://www.mamp.info/en/index.html). The URL to Phormer will then need to be passed into Clematis as an argument, for example:

```
--url http://localhost:8888/phormer331/index.php
```
To better utilize Clematis with Phormer, please add a few photos to the application before running our tool. 

In order to test your own web-application using Clematis, deploy the application and provide its URL to Clematis in a similar fashion as shown above.

### Running the Tool 

The Jetty server must be started before running Clematis. First, navigate to the root directory of Clematis (where you checked-out clematis) and execute the following from your command-line (Terminal, etc.):

```
mvn jetty:run
```

If successful, a notification should appear confirming that the server is up-and-running  
(``[INFO] Started Jetty Server``). Next, run the Clematis project as a Java application from Eclipse by setting [com.clematis.core.SimpleExample](https://github.com/saltlab/clematis/blob/master/src/main/java/com/clematis/core/SimpleExample.java) as the Main class and providing a URL argument. Once a new browser session is started by Clematis, feel free to use your application as you normally would. The injected toolbar can be used to start and stop the recording of an application's behaviour (events and JavaScript execution). Any generated trace files can be found in the 'clematis-output' directory at the root of clematis.

Lastly, the outputted visualization can be viewed at the following address while the Jetty server is running:

```
http://localhost:8080/fish-eye-zoom/view.html
```

More documentation (and fewer bugs) coming soon.

## Contributing

Your feedback is valued! Please use the [Issue tracker](https://github.com/saltlab/clematis/issues) for reporting bugs or feature requests.



