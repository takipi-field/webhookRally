# webhookRally
Webhook for creating defects in Rally (CA-Agile Central).  Utilizes https://github.com/takipi-field/webhook-example as the base.  Maps incoming OverOps webhook to Rally RESTful API.  https://help.rallydev.com/for-developers.

Retrieve webhookRally.war from /bin.  Deploy to tomcat/webapps directory.  
Update WEB-INF/classes/rally.properties

RallyProjectObjectID=\<Rally Project ID easiky obtained from Rally URL>

RallyAPIkey=\<Rally API Key>

WSlogging=true //Enable to see Web Service calls in catalina.out



Tested with Tomcat 8.x and Jetty 8.  

Running in AWS Beanstalk at 
http://webhook-env.us-east-2.elasticbeanstalk.com/rallyDefect
