/**
 * 
 */
package com.appdynamics.cloud.prometheus.awssigv4;

import com.amazonaws.AmazonWebServiceResponse;

/**
 * @author James Schneider
 *
 */
public class WebServiceResponse extends AmazonWebServiceResponse {

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}
