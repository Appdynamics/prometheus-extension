/**
 * 
 */
package com.appdynamics.cloud.prometheus.awssigv4;

import java.net.URI;

import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpMethodName;
import com.appdynamics.cloud.prometheus.AppdPrometheusAppListener;
import com.appdynamics.cloud.prometheus.Logger;

/**
 * @author James Schneider
 *
 */
public class TestClient {

	private static Logger logr = new Logger(TestClient.class.getSimpleName(), AppdPrometheusAppListener.LOGGING_LEVEL);
	
	/**
	 * 
	 */
	public TestClient() {
		
	}

	public static String processRequest(String endpointUrlWithParms, String regionName, AWSCredentials awsCredentials) throws Throwable {
		
		Request<Void> request = new DefaultRequest<Void>("aps");
		
		request.setHttpMethod(HttpMethodName.GET);
		request.setEndpoint(URI.create(endpointUrlWithParms));
		
		AWS4Signer signer = new AWS4Signer();
		signer.setRegionName(regionName);
		signer.setServiceName(request.getServiceName());
		signer.sign(request, awsCredentials);
		
		//Execute it and get the response...
		Response<AmazonWebServiceResponse<String>> rsp = new AmazonHttpClient(new ClientConfiguration())
		    .requestExecutionBuilder()
		    .executionContext(new ExecutionContext(true))
		    .request(request)
		    .errorResponseHandler(new AwsErrorResponseHandler(true))
		    .execute(new AwsResponseHandler<String>(true));		
		
		logr.info("RESULT = " + rsp.getAwsResponse().getResult());
		
		
		return rsp.getAwsResponse().getResult();
	}
	
}
