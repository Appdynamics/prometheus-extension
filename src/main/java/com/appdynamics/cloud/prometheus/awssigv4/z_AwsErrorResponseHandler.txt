/**
 * 
 */
package com.appdynamics.cloud.prometheus.awssigv4;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;

/**
 * @author James Schneider
 *
 */
public class AwsErrorResponseHandler implements HttpResponseHandler<AmazonServiceException> {

	private boolean needsConnectionLeftOpen;
	
	/**
	 * 
	 */
	public AwsErrorResponseHandler() {
		
	}

	public AwsErrorResponseHandler(boolean leaveConnectionOpen) {
		this.needsConnectionLeftOpen = leaveConnectionOpen;
	}
	
	@Override
	public AmazonServiceException handle(HttpResponse response) throws Exception {
        AmazonServiceException amse = new AmazonServiceException(response.getStatusText());
        amse.setStatusCode(response.getStatusCode());
        return amse;
	}

	@Override
	public boolean needsConnectionLeftOpen() {
		return this.needsConnectionLeftOpen;
	}

}
