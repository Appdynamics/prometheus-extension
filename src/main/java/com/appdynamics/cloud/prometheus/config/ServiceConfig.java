/**
 * 
 */
package com.appdynamics.cloud.prometheus.config;

import java.util.List;

/**
 * @author James Schneider
 *
 */
public class ServiceConfig {

	private String loggingLevel = "info";
    private String eventsServiceEndpoint;
    private String eventsServiceApikey;
    private String controllerGlobalAccount;
    private String prometheusUrl;
    private String authenticationMode;
    private String awsRegion;
    //private String awsAccessKey;
    //private String awsSecretKey;
	
	private List<AnalyticsEventsSourceConfig> analyticsEventsSources;
	
	
	/**
	 * 
	 */
	public ServiceConfig() {
		
	}

	
	
	public String getLoggingLevel() {
		return this.loggingLevel;
	}



	public void setLoggingLevel(String logLevel) {
		this.loggingLevel = logLevel;
	}



	public String getEventsServiceEndpoint() {
		return eventsServiceEndpoint;
	}


	public void setEventsServiceEndpoint(String eventsServiceEndpoint) {
		this.eventsServiceEndpoint = eventsServiceEndpoint;
	}


	public String getEventsServiceApikey() {
		return eventsServiceApikey;
	}


	public void setEventsServiceApikey(String eventsServiceApikey) {
		this.eventsServiceApikey = eventsServiceApikey;
	}


	public String getControllerGlobalAccount() {
		return controllerGlobalAccount;
	}


	public void setControllerGlobalAccount(String controllerGlobalAccount) {
		this.controllerGlobalAccount = controllerGlobalAccount;
	}


	public String getPrometheusUrl() {
		return prometheusUrl;
	}


	public void setPrometheusUrl(String awsAmpWorkspaceUrl) {
		this.prometheusUrl = awsAmpWorkspaceUrl;
	}


	public String getAuthenticationMode() {
		return authenticationMode;
	}


	public void setAuthenticationMode(String authenticationMode) {
		this.authenticationMode = authenticationMode;
	}


	public String getAwsRegion() {
		return awsRegion;
	}


	public void setAwsRegion(String awsRegion) {
		this.awsRegion = awsRegion;
	}


//	public String getAwsAccessKey() {
//		return awsAccessKey;
//	}
//
//
//	public void setAwsAccessKey(String awsAccessKey) {
//		this.awsAccessKey = awsAccessKey;
//	}
//
//
//	public String getAwsSecretKey() {
//		return awsSecretKey;
//	}
//
//
//	public void setAwsSecretKey(String awsSecretKey) {
//		this.awsSecretKey = awsSecretKey;
//	}


	public List<AnalyticsEventsSourceConfig> getAnalyticsEventsSources() {
		return analyticsEventsSources;
	}

	public void setAnalyticsEventsSources(List<AnalyticsEventsSourceConfig> analyticsEventsSources) {
		this.analyticsEventsSources = analyticsEventsSources;
	}



	
	
}
