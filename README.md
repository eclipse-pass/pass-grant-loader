# PASS Grant Loader
This repo contains code that, when executed, will query the JHU institutional grant database for desired data and/or load
it into local persistent storage

## Description of Operation
The tool is invoked from the command line to query several views in 
the JHU COEUS grant management system to pull the records of all 
grants or users which have been updated since a certain timestamp. The normal path
is that the timestamp is read from the last line of a file which 
records all such timestamps after successfully completed runs. 
Optionally, a timestamp may be supplied as the `-s` option on the command line.

The ResultSet which is returned from COEUS is transformed into an intermediate Set
of data structures which contain the information we require to update our grants. funders and users.
We finally use our java Pass client to merge this update information with existing
information in PASS to have the PASS repository reflect the current information obtained
in the update. Auxiliary objects belonging to the Grant (Users and Funders)
will also receive updates when Grants are updated.

Because the application is intended to run unattended in slack hours, we
send email notification indicating success, or reporting errors which prevented the 
application from performing an update.

## Configuration
The user running this application must supply a full path to a home
or base directory for the application. This directory will contain any 
configuration files, and will also contain the log file and the file(s) containing the
update timestamps. Necessary configuration files are as follows.

### Connection properties file (`connection.properties`)
This file must contain the values for the URL, user, and password needed to
attach to COEUS's Oracle database, and the local directory lookup service. The URL for a database used with the Oracle
driver typically looks like this: jdbc:oracle:thin:@host.name.institution:1521:service-name

We also put the connection parameters for the directory lookup service which provides us a mapping between a user's
employee id and their Hopkins ID. these values are the service's base URL, and the credentials needed to access the service

`coeus.url =`
`coeus.user =`
`coeus.password =`
`directory.base.url =`
`directory.client.id =`
`directory.client.secret =`

### Mail server properties file (`mail.properties`)
The use of the mail server is enabled by supplying the command line option `-e`.
This configuration file contains values for parameters needed to send mail out from the application.
These values suggest using a gmail server for example.

`mail.transport.protocol=SMTPS`
`mail.smtp.starttls.enable=true`
`mail.smtp.host=smtp.gmail.com`
`mail.smtp.port=587`
`mail.smtp.auth=true`
`mail.smtp.user=`
`mail.smtp.password=`
`mail.from=`
`mail.to=`

### Fedora and Elasticsearch configuration (`system.properties`)
This file contains parameters which must be set as system properties so that  the java PASS client
can configure itself to attach to its storage and its search endpoint - in our case, a Fedora instance and an Elasticsearch instance. The base URL must contain
the port number and path to the base container (for example, `http://localhost:8080/fcrepo/rest/`)

`pass.fedora.user=`
`pass.fedora.password=`
`pass.fedora.baseurl=`
`pass.elasticsearch.url=`  
`pass.elasticsearch.limit=`


### Grants
Our approach is that for each grant record, to see if PASS knows about it yet, and if so, pull back the current version
of the grant. We then look at the hash map and overwrite any information on the existing object with the new
information. Fields which are themselves ids representing PASS objects are also updated. In order to keep processing as efficient
as possible, we do track which PASS objects have been updated in the current session, as some of them may
appear many times (`Funder`s or `User`s for example). We update these only once in the session.

After we have processed each record, we save the state of the Grant objects in a List. After all records
are processed we know that each Grant object on the List is current, and so we update these grants in Pass.

This mode is the default mode, but may ansl be specified using the `-m grant` option.

###User Mode
This mode (`-m user`) is similar to the `grant` mode, but updates existing PASS users only. Grant updates will update associated users
(and funders) automatically. This mode is used to perform data updates related to model changes, for example.

###Actions
The tool can be used to perform a just a pull from COEUS by using the `-a pull` option. This saves a serialized version of
the COEUS data to be applied to PASS at a later time, into a file specified by a path which is the first (only) command
line argument. The subsequent loading into PASS would be accomplished by invoking the tool with the -a "load" option,
specifying the serialized data file as the command line argument.

## Invocation
The application is provided as an executable jar file. The absolute path for the base directory `COEUS_HOME` must be provided as a command line
option to java in order to inject it into the java context. The command line looks like this

`java -DCOEUS_HOME=full-path-to-base-directory -jar path-to-jar-file`

when we are taking the timestamp as the last line of the update timestamps file, or 

`java -DCOEUS_HOME=full-path-to-base-directory -jar path-to-jar-file -s "yyyy-mm-dd hh:mm:ss.0"`

when invoking the application to process all grant updates occurring after the specified timestamps.

For example:

`java -DCOEUS_HOME="/home/luser/coeus" -jar pass-grant-cli-1.0.0-SNAPSHOT-shaded.jar -s "2018-03-29 14:30:00.0"`

For specific actions, we use the `-a option`, and a file path command line argument.

We can perform a pull of grants from COEUS, and store it in a file, by:

`java -DCOEUS_HOME="/home/luser/coeus" -jar pass-grant-cli-1.0.0-SNAPSHOT-shaded.jar -s "2018-03-29 14:30:00.0" -a pull /home/luser/coeus/pulls/thisPull.data`

this data pull can be applied to the PASS repository by a subsequent invocation

`java -DCOEUS_HOME="/home/luser/coeus" -jar pass-grant-cli-1.0.0-SNAPSHOT-shaded.jar -a load /home/luser/coeus/pulls/thisPull.data`

We note that when a load is being done into PASS, the application will figure out the mode that was used for pulling the data on the fly.
So, it isn't necessary to supply a mode or a start date to the application - these will be ignored.

## Email Notification
We may add the command line option -e to enable the use of the email server to send email messages after
each execution which involves a load into PASS. This will report information on the successful run of the application, or information
on what went wrong in the case of an error. If this option is enabled, the email configuration file
must be filled out accordingly. We also have a command line option -m to pass in the mode of operation -
 grant or user - depending on which mode we wish to operate in. If no mode is specified, we default to `grant`

## Implementation Details
The processing of the ResultSet is straightforward - we simply construct a set of hash maps which represent the
column names and the values for each record. We do not assume that the PASS objects in Fedora are updated
only by this application, as there may be some fields on these objects which are not known to COEUS, but
may be populated by other applications eventually (for example ORCID on User, Submissions on Grant, or Policy on Funder).

