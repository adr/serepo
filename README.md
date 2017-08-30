# SE-Repo
This is the repository of the Software Engineering Repository of my master thesis.

## Prerequisites
You need an up and running instance of Apache Solr http://lucene.apache.org/solr/  
Create a core called `serepo` with the command: `solr create_core -c serepo`

## How to run
`mvn clean install`  

To run the RESTful HTTP API:  
`cd serepo-rest`  
`mvn tomcat7:run`

The RESTful HTTP API will be available under http://localhost:8080/serepo

To run the SE-Repo Webapplication:  
`cd serepo-client-webapp`  
`mvn tomcat7:run`

The application will be available under http://localhost:8081/serepo-webapp

## Configurations
The RESTful HTTP API can be configured within the file `serepo-rest/serepo-config.json`  
The webapplication can be configured whithin the file `serepo-client-webapp/serepo-webapp-config.json`
