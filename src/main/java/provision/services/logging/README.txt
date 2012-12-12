                                   README

                      Logging Component
                         

Contents

   * Introduction
   * Design
   * Features
   * Software
   * Build Instructions
   * Installation
   * Running
   * Requirements
   * Limitations


Introduction

	The purpose of Maintenance and Diagnostics Subsystem is to provide common API 
	for event logging. Events can have following logging levels: debug, info, warning, 
	error, fatal. Events can be logged to different target outputs: file, console, database, 
	Unix Syslog ... Logging levels and target output can be set up dynamically. 


Design

	Jakarta's Log4j logging, open source package written in Java is used as a base for the 
	Maintenance and Diagnostics component. After evaluating several logging packages, 
	Log4j was chosen based on its features:
	- Log4j is optimized for speed
	- Log4j is fail-stop but not reliable
	- Log4j is not restricted to a predefined set of facilities. 
	- Logging behaviour can be set at runtime using a configuration file. Configuration files 
		can be property files or in XML format. 
	- Log4j is designed to handle Java Exceptions from the start. 
	- Log4j can direct its output to a file, the console, an java.io.OutputStream, java.io.Writer, 
		a remote server using TCP, a remote Unix Syslog daemon, to a remote listener using JMS, 
		to the NT EventLog or even send e-mail. 
	- Log4j uses 5 priority levels. 
	- The format of the log output can be easily changed
	- Log4j supports internationalization.

	This mature logging package covers majority of our requirements. Also, Log4j incorporation within 
	our design saves our development time. 

	The Maintenance and Diagnostics Subsystem provides a wrapper class for Log4j logging package. 
	Its interface has five methods: debug, info, warning, error, fatal. Message Driven EJB design pattern 
	is used to support asynchronous logging requests. 


Features

	The following features apply to this release:
 
	- Multi server distributed operation
	- Support of asynchronous logging requests
	- Dynamically configurable logging behaviour


	Multi Server Distributed Operation

	The Logging functionality can be distributed among n clients that run within 
	the cluster. These clients can be on separate machines (most likely scenario for 
	production) or on the same machine (for testing). 


	Support of Asynchronous Logging Requests

	Message Driven EJB design pattern is used in Logging design which
	allows for asynchronous logging requests.


	Dynamically Configurable Logging Behaviour

	Logging behaviour can be set at runtime using a configuration file. Configuration files 
	can be property files or in XML format. The format of the log output, logging severity and
	the output where the logging is directed to can be easily changed at runtime.


Software

	Interfaces:

		(provision.services.logging package)

		Logger.java
		The Logger class is an interface class for logging messages
		with five severity levels: debug, info, warning, error and fatal.

		LoggingMessage.java
		The LoggingMessage class contains all the fields required
		for sending a logging message object to the MessageLoggerBean
		logging message listener.

		LoggerConstants.java
		Logger constants interface contains logging severity and WebLogic 
		related constants.
		

	Server (Message Driven Bean): 

		(provision.services.logging package)

		MessageLoggerBean.java
		The MessageLoggerBean class is a Message Driven Bean 
		which listens for logging messages, receives them in an object form 
		and logs them using Jakarta's Log4j logging package.


	XML files:

		(Deployment) 
		ejb-jar.xml 
		weblogic-ejb-jar.xml
		web.xml
		
		(ANT)
		build.xml
		ANT build script for the Logging component.

		build.bat
		This is a batch file for running build.xml script.


	Configuration file:

		configFile.lcf
		This is a configuration file for the Logging system 
		which enables dynamic changes of the logging behaviour.

	Servlet:

		(provision.services.logging.test package)

		LoggingServlet.java
		This is a test driver for the Logging component.


	Sequence diagrams:

		LoggingSequence.dfSequence
		LoggingSequence.dfSequence.wmf
		These files represent the sequence diagram for the Logging 
		component (generated from Together 5.0).


	Javadoc:

		doc.zip
		Javadoc ZIP file for the Credit component.


Build Instructions

	1.	Run the ANT build script. 
	
	2.	This script will build the component and place the 
		files in the correct locations in your WebLogic Server distribution: 

		EJBean: e.g. in /config/mydomain/applications/logging-ejb.jar
		WEB application: e.g. in /config/mydomain/applications/logging.war

		Running the build script places the EJB in /config/mydomain/applications, where it 
		automatically deploys once the server is started. The servlet will be also automatically
		deployed.


Installation

	1.	Install BEA Weblogic Server 6.0. The home directory for this server is important because 
		it needs to be entered into the ANT script. Recommend to install BEA into C:\bea. 
		The domain name you supply will control where you deploy JAR and WAR files. (Assumed 
		domain name in step 2 in Build Instructions section is mydomain).

	2.	Extract the configuration file, configFile.lcf, from logging-ejb.jar file explained in step 2 
		in Build Instructions section. Extract it to the directory where the WebLogic is installed
		(e.g. C:\bea\wlserver6.0sp1). 

	3.	Start WebLogic application server.

	4.	Create a JMS Server and its topic destination with a particular name. To do that,
		open WebLogic console by entering http://localhost:7001/console URL in your browser.
		Find JMS on left side of the console window and right mouse click on Servers sub-item. 
		Choose "Create a new JMSServer ..." option. Name the JMS server and click Create button.
		After creating the JMS server, click on "Target" tab and move the target server (e.g. myserver)
		from Available to Chosen list. Then, right mouse click on Destinations sub-item under the
		newly created JMS server. Choose "Create a new JMSTopic ..." option. Put any name for the
		topic, but JNDIName field must be named PSLoggingTopic.

	5.	JAR and WAR files are automatically deployed (see step 2 in Build Instructions section).


Running

	Logging classes are packaged in logging-ejb.jar file. Test driver is a WEB application.

	Steps to run the servlet:

	1.	LoggingServlet is automatically deployed in the WEB server (see
		step 2 in the Build Instructions section). 

	2.	Call the WEB application from a Web browser with the following URL
		http://localhost:7001/logging/LoggingServlet

		where: 
		localhost is the host name of the machine running
		WebLogic Server 

		port is the port number where WebLogic Server is listening
		for requests. 

	3.	Verify that test messages are really logged by checking the WebLogic
		server console and test log output file, myTest.log, created on the same 
		directory where the configuration file (configFile.lcf) is (e.g. C:\bea\wlserver6.0sp1).
		On Unix systems, check the Syslog for logged messages.
  

Requirements

	BEA WebLogic Server 6.0 (includes Java 2 SDK v1.3)
	EJB 2.0 Upgrade for BEA WebLogic Server 6.0


Limitations

	Currently, creation of the JMS server is a manual step (see Step 4 in Installation section).
	We are planning to automate it in near future. One way to do that would be to modify WebLogic's
	config.xml file to include the JMS server related info. config.xml file is usually located
	at C:\bea\wlserver6.0sp1\config\mydomain directory.