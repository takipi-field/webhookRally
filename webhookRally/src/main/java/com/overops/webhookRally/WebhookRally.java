package com.overops.webhookRally;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.util.Ref;



@Path("/rallyDefect")
public class WebhookRally {
		
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
	String ProjectID = JerseyConfig.properties.getProperty("RallyProjectObjectID");
	return ("Success! OverOps Webhook Rally Get Response " + ProjectID);
	}
	
	@POST 
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(final OverOpsEvent input) throws URISyntaxException, IOException {
		//Do something cool with the events from OverOps
		//
		
	    System.out.println("api_version = " + input.api_version);
	    System.out.println("type = " + input.type);
	    System.out.println("username = " + input.username);
	    System.out.println("service_id = " + input.service_id);
	    System.out.println("service_name = " + input.service_name);
	    
	    		
		String RallyAPIkey = JerseyConfig.properties.getProperty("RallyAPIkey");
		String ProjectID = JerseyConfig.properties.getProperty("RallyProjectObjectID");
		Boolean WSlogging = JerseyConfig.properties.getProperty("WSlogging").contains("true");
		
		RallyRestApi restApi = new RallyRestApi(new URI("https://rally1.rallydev.com"), RallyAPIkey);
		//ToDo Set Proxy restApi.setProxy(proxy, userName, password);
		
		//ObjectMapper for json output from OverOps to newDefect
	    ObjectMapper mapper = new ObjectMapper();
	  	String AlertEventAsString=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(input.data);
	    if(WSlogging) 
		System.out.println(AlertEventAsString);
	    
	    StringBuilder stringBuilder = new StringBuilder();
	    
		if (input.type.contains("ALERT"))
		{

			JsonObject newDefect = new JsonObject();
			JsonParser parser = new JsonParser();
			JsonObject OOEventObj = parser.parse(AlertEventAsString).getAsJsonObject();
			System.out.println("OverOpsAlert Type: " + OOEventObj.get("type").getAsString());
			if (OOEventObj.get("type").getAsString().contains("THRESHOLD"))
			{
			// Map OOEventObj data to newDefect for type: Threshold 
			String AlertType = OOEventObj.get("type").getAsString();
			String ViewName = OOEventObj.get("view_name").getAsString();
			newDefect.addProperty("Name", "OverOps Alert " + ViewName + " " + AlertType);
			
			
			String AlertSummary = OOEventObj.get("summary").getAsString();
			stringBuilder.append(AlertSummary + "\n\n");
			
			JsonObject dataObj = (JsonObject) OOEventObj.get("data");		
			
			JsonArray top_events = dataObj.get("top_events").getAsJsonArray();
			for (int i = 0; i <top_events.size(); i++) {

				   JsonObject obj= (JsonObject) top_events.get(i);
				   String title=obj.get("title").getAsString();
				   JsonObject frame=(JsonObject) obj.get("frame");
				   String class_name=frame.get("class_name").getAsString();
				   String times=obj.get("times").getAsString();
				   String link=obj.get("link").getAsString();
		
				   stringBuilder.append(title + " " + class_name + " " +  "times: " + times + "  " + "<a href=\"" + link + "\" >" + link + "</a> \n");
				   
			}
			}
		
			if (OOEventObj.get("type").getAsString().contains("NEW_EVENT"))
			{
			// Map OOEventObj data to newDefect for type: NEW_EVENT 
		
			String AlertSummary = OOEventObj.get("summary").getAsString();
			JsonObject dataObj = (JsonObject) OOEventObj.get("data");	
			String EventType = dataObj.get("type").getAsString();
			JsonObject EventLocation = (JsonObject) dataObj.get("location");
 			JsonObject EntryPoint = (JsonObject) dataObj.get("entry_point");
 			String message = dataObj.get("message").toString();
 			String link=dataObj.get("link").getAsString();
 			 			
 			stringBuilder.append(EventType + " "+  message + "<br>");
 			stringBuilder.append("<br><br>");
 			stringBuilder.append("Location: " + EventLocation.get("class_name").getAsString() + " " + EventLocation.get("method_name").getAsString() + "<br>");
 			stringBuilder.append("Entry Point: " + EntryPoint.get("class_name").getAsString() + " " + EntryPoint.get("method_name").getAsString() + "<br>");
 			stringBuilder.append("<br>");
 			stringBuilder.append("<a href=\"" + link + "\" >" + link + "</a>" );
 			newDefect.addProperty("FoundInBuild", dataObj.get("deployment_name").getAsString());
			newDefect.addProperty("Name", AlertSummary);
			

			
			}
			
			if (OOEventObj.get("type").getAsString().contains("RESURFACED"))
			{
			//OOEventObj data to newDefect for type: RESURFACED
				String AlertSummary = OOEventObj.get("summary").getAsString();
				JsonObject dataObj = (JsonObject) OOEventObj.get("data");	
				String EventType = dataObj.get("type").getAsString();
				JsonObject EventLocation = (JsonObject) dataObj.get("location");
	 			JsonObject EntryPoint = (JsonObject) dataObj.get("entry_point");
	 			String link=dataObj.get("link").getAsString();
	 			 			
	 			stringBuilder.append(EventType + " " + "<br>");
	 			stringBuilder.append("<br><br>");
	 			stringBuilder.append("Location: " + EventLocation.get("class_name").getAsString() + " " + EventLocation.get("method_name").getAsString() + "<br>");
	 			stringBuilder.append("Entry Point: " + EntryPoint.get("class_name").getAsString() + " " + EntryPoint.get("method_name").getAsString() + "<br>");
	 			stringBuilder.append("<br>");
	 			stringBuilder.append("<a href=\"" + link + "\" >" + link + "</a>" );
	 			newDefect.addProperty("FoundInBuild", dataObj.get("deployment_name").getAsString());
				newDefect.addProperty("Name", AlertSummary);
			}
			
			String Description = stringBuilder.toString();
			newDefect.addProperty("Description", Description);
			CreateRequest createRequest = new CreateRequest("defect", newDefect);
		  	
			try {
				CreateResponse createResponse = restApi.create(createRequest);
				if (createResponse.wasSuccessful()) {
					JsonObject defectJsonObject = createResponse.getObject();
					String defectURL = Ref.getRelativeRef(createResponse.getObject().get("_ref").getAsString());
					if (WSlogging)
						System.out.println(defectJsonObject);
					System.out.println(
							"Defect Created https://rally1.rallydev.com/#/" + ProjectID + "/detail" + defectURL);
				} else {
					String[] createDefectErrors;
					createDefectErrors = createResponse.getErrors();
					System.out.println("Error from Rally API");
					for (int i = 0; i < createDefectErrors.length; i++) {
						System.out.println(createDefectErrors[i]);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}
		restApi.close();
		return Response.ok().build();
		
		
		
	}
}

