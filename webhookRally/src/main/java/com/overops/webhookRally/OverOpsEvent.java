package com.overops.webhookRally;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;






@JsonIgnoreProperties(ignoreUnknown = true)

public class OverOpsEvent {
	public String api_version;
	public String date;
    public String type;  
    public String username;
    public String service_id;
    public String service_name;
    public Object data;

   
}

