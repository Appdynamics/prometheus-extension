/**
 * 
 */
package com.appdynamics.cloud.prometheus.awssigv4;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import com.appdynamics.cloud.prometheus.AppdPrometheusAppListener;
import com.appdynamics.cloud.prometheus.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author James Schneider
 *
 */
public class AwsResponseHandler<T> implements HttpResponseHandler<AmazonWebServiceResponse<T>> {

	private static Logger logr = new Logger(AwsResponseHandler.class.getSimpleName(), AppdPrometheusAppListener.LOGGING_LEVEL);
	
	private boolean needsConnectionLeftOpen;
	
	/**
	 * 
	 */
	public AwsResponseHandler() {
		
	}

	public AwsResponseHandler(boolean leaveConnectionOpen) {
		this.needsConnectionLeftOpen = leaveConnectionOpen;
	}
	
	@Override
	public AmazonWebServiceResponse<T> handle(HttpResponse response) throws Exception {
        int status = response.getStatusCode();
        if(status < 200 || status >= 300) {
            String content;
            final StringWriter writer = new StringWriter();
            try {
                IOUtils.copy(response.getContent(), writer, "UTF-8");
                content = writer.toString();
            } catch (final IOException e) {
            	content = "Couldn't get response content!";
            }
            AmazonServiceException ase = new AmazonServiceException(content);
            ase.setStatusCode(status);
            throw ase;
        } else {
        	WebServiceResponse awsResponse = new WebServiceResponse();
            String content;
            final StringWriter writer = new StringWriter();
            try {
                IOUtils.copy(response.getContent(), writer, "UTF-8");
                content = writer.toString();
            } catch (final IOException e) {
            	content = "Couldn't get response content!";
            }
        	
            logr.info("CONTENT = " + content);
            
            awsResponse.setResult(content);
            awsResponse.setContent(content);
            
            Map<String, String> metadata = new HashMap<String, String>();
            
           
            
            metadata.put(ResponseMetadata.AWS_REQUEST_ID, response.getHeaders().get(X_AMZN_REQUEST_ID_HEADER));
            awsResponse.setResponseMetadata(new ResponseMetadata(metadata));
            
            return awsResponse;
        }

        
	}


	
	@Override
	public boolean needsConnectionLeftOpen() {
		return this.needsConnectionLeftOpen;
	}

}
