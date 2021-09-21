/**
 * 
 */
package com.appdynamics.cloud.prometheus.analytics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.appdynamics.cloud.prometheus.Logger;
import com.appdynamics.cloud.prometheus.config.ServiceConfig;
import com.appdynamics.cloud.prometheus.utils.StringUtils;

/**
 * @author James Schneider
 *
 */
public class AnalyticsEventsDriver implements Runnable, AnalyticsEventsPublisher {

	private ServiceConfig serviceConfig;
	private AnalyticsEventsSource analyticsEventsSource;
	private static Logger logr;
	
	/**
	 * 
	 */
	public AnalyticsEventsDriver(ServiceConfig serviceConfig, AnalyticsEventsSource analyticsEventsSource) {
		
		logr = new Logger(AnalyticsEventsDriver.class.getSimpleName(), serviceConfig.isDebugLogging());
		this.serviceConfig = serviceConfig;
		this.analyticsEventsSource = analyticsEventsSource;
		
	}

	@Override
	public void run() {
		
		while (true) {

			try {
				
				long startTime = Calendar.getInstance().getTimeInMillis();
				
				logr.info("##############################  Publishing Analytics Events for schema '" + this.analyticsEventsSource.getSchemaName() + "'  : BEGIN");
				
				this.createSchemaIfRequired();
				
				this.analyticsEventsSource.timeToPublishEvents(this);
				
				long endTime = Calendar.getInstance().getTimeInMillis();
				long totalTimeSecs = (endTime - startTime) / 1000;
				long totalTimeMins = totalTimeSecs / 60;			
				long minsInSecs = totalTimeMins * 60;
				long remainingSecs = totalTimeSecs - minsInSecs;
				
				logr.info("##############################  Publishing Analytics Events for schema '" + this.analyticsEventsSource.getSchemaName() + "'  : END");
				logr.info("##############################  Total Elapsed Time = " + totalTimeMins + " minutes : " + remainingSecs + " seconds");
				
				
				Thread.currentThread().sleep(this.analyticsEventsSource.getExecutionInterval() * 60000);
				
				logr.carriageReturn();
				logr.carriageReturn();
				//logr.carriageReturnDebug();
				//logr.carriageReturnDebug();
				
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
			
		}
		
	}
	
	
	public void publishEvents(String jsonPayload) throws Throwable {
		
		String accountName = this.serviceConfig.getControllerGlobalAccount();
		String apiKey = this.serviceConfig.getEventsServiceApikey();
		String restEndpoint = this.serviceConfig.getEventsServiceEndpoint() + "/events/publish/" + this.analyticsEventsSource.getSchemaName();

		CloseableHttpClient client = HttpClients.createDefault();
		
		HttpPost request = new HttpPost(restEndpoint);
		request.addHeader("X-Events-API-AccountName", accountName);
		request.addHeader("X-Events-API-Key", apiKey);

		request.addHeader("Content-Type", "application/vnd.appd.events+json;v=2");
		request.addHeader("Accept", "application/vnd.appd.events+json;v=2");

	    StringEntity entity = new StringEntity(jsonPayload);
	    request.setEntity(entity);
	    
	    CloseableHttpResponse response = client.execute(request);
		
	    logr.info("Analytics Publish Response for schema '" + this.analyticsEventsSource.getSchemaName() + "' = HTTP Status: " + response.getStatusLine().getStatusCode());
	    
		String resp = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }			
		
        resp = out.toString();
		reader.close();
		
		logr.debug("Publish Events Response");
		logr.debug(resp);

		HttpClientUtils.closeQuietly(response);
		HttpClientUtils.closeQuietly(client);
		
		
	}
	
	private boolean schemaExists() throws Throwable {
		
		String accountName = this.serviceConfig.getControllerGlobalAccount();
		String apiKey = this.serviceConfig.getEventsServiceApikey();
		String restEndpoint = this.serviceConfig.getEventsServiceEndpoint() + "/events/schema/" + this.analyticsEventsSource.getSchemaName();
		
		CloseableHttpClient client = HttpClients.createDefault();
		
		HttpGet request = new HttpGet(restEndpoint);
		request.addHeader("X-Events-API-AccountName", accountName);
		request.addHeader("X-Events-API-Key", apiKey);

		request.addHeader("Content-Type", "application/vnd.appd.events+json;v=2");
		request.addHeader("Accept", "application/vnd.appd.events+json;v=2");

	    CloseableHttpResponse response = client.execute(request);
		
	    int statusCode = response.getStatusLine().getStatusCode();
	    
	    logr.debug(" - Checking for existing schema");
	    logr.debug(" - Schema: " + this.analyticsEventsSource.getSchemaName() + " : HTTP Status: " + response.getStatusLine().getStatusCode());
	    
	    boolean exists = false;
	    
	    switch (statusCode) {

	    case 200:
	    	exists = true;
			break;

	    case 404:
	    	exists = false;
			break;
			
		default:
			HttpClientUtils.closeQuietly(response);
			HttpClientUtils.closeQuietly(client);			
			throw new Exception("Unable to check if schema exists | schema name = " + this.analyticsEventsSource.getSchemaName() + " | HTTP status = " + statusCode);
		}
		
	    
		HttpClientUtils.closeQuietly(response);
		HttpClientUtils.closeQuietly(client);
		
		return exists;
	}
	
	private String generateCreateSchemaPayload(String filePath) throws Throwable {
		
		StringBuffer buff = new StringBuffer();
		String[] lines = StringUtils.getFileAsArrayOfLines(filePath);
		
		buff.append("{");
		buff.append("\"schema\" : { ");

		for (int cntr = 0; cntr < lines.length; cntr++) {
			
			String[] attrs = lines[cntr].split(",");
			buff.append("\"" + attrs[0] + "\": \"" + attrs[1] + "\"");
			if (cntr < lines.length-1) {
				buff.append(", ");
			}
		}
			
		buff.append(" }");
		buff.append(" }");
		
		return buff.toString();
	}	
	
	private void createSchema() throws Throwable {
		
		String accountName = this.serviceConfig.getControllerGlobalAccount();
		String apiKey = this.serviceConfig.getEventsServiceApikey();
		String restEndpoint = this.serviceConfig.getEventsServiceEndpoint() + "/events/schema/" + this.analyticsEventsSource.getSchemaName();
		String payload = this.generateCreateSchemaPayload(this.analyticsEventsSource.getSchemaDefinitionFilePath());
		
		CloseableHttpClient client = HttpClients.createDefault();
		
		HttpPost request = new HttpPost(restEndpoint);
		request.addHeader("X-Events-API-AccountName", accountName);
		request.addHeader("X-Events-API-Key", apiKey);

		request.addHeader("Content-Type", "application/vnd.appd.events+json;v=2");
		request.addHeader("Accept", "application/vnd.appd.events+json;v=2");

	    StringEntity entity = new StringEntity(payload);
	    request.setEntity(entity);
	    
	    CloseableHttpResponse response = client.execute(request);
		
	    logr.debug(" - Creating schema");
	    logr.debug(" - Schema: " + this.analyticsEventsSource.getSchemaName() + " : HTTP Status: " + response.getStatusLine().getStatusCode());
	    
		String resp = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }			
		
        resp = out.toString();
		reader.close();
		
		logr.debug("Create Schema Response");
		logr.debug(resp);

		HttpClientUtils.closeQuietly(response);
		HttpClientUtils.closeQuietly(client);
		
		
	}
	
	
	private void createSchemaIfRequired() throws Throwable {
		if (!this.schemaExists()) {
			this.createSchema();
		}
	}
	
}
