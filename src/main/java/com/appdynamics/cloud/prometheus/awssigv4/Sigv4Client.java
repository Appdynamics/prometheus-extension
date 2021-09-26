package com.appdynamics.cloud.prometheus.awssigv4;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.appdynamics.cloud.prometheus.AppdPrometheusAppListener;
import com.appdynamics.cloud.prometheus.Logger;

/**
 * 
 * Client used to send GET request to a service that requires Signature V4 authorization 
 * and return the response body as a String.
 * 
 */
public class Sigv4Client {
    
	private static Logger logr = new Logger(Sigv4Client.class.getSimpleName(), AppdPrometheusAppListener.LOGGING_LEVEL);
	
    public static String processRequest(String endpointUrlWithParms, String regionName, String awsAccessKey, String awsSecretKey, String awsSessionToken, Map<String, String> queryParameters) {
        
        // the region-specific endpoint to the target object expressed in path style
        URL endpointUrl;
        try {
            endpointUrl = new URL(endpointUrlWithParms);
            // endpointUrl = new URL("https://" + bucketName + ".s3.amazonaws.com/ExampleObject.txt");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
        }
        
        // for a simple GET, we have no body so supply the precomputed 'empty' hash
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-amz-content-sha256", Sigv4SignerBase.EMPTY_BODY_SHA256);
        
        Sigv4SignerForAuth signer = new Sigv4SignerForAuth(
                endpointUrl, "GET", "aps", regionName);
        String authorization = signer.computeSignature(headers, 
        											   queryParameters, // no query parameters
                                                       Sigv4SignerBase.EMPTY_BODY_SHA256, 
                                                       awsAccessKey, 
                                                       awsSecretKey,
                                                       awsSessionToken);
                
        // place the computed signature into a formatted 'Authorization' header
        // and call the service
        headers.put("Authorization", authorization);
        String response = Sigv4HttpUtils.invokeHttpRequest(endpointUrl, "GET", headers, null);
        logr.carriageReturnTrace();
        logr.trace("--------------------------------------------------------------------------------- Response content begin ---------");
        logr.trace(response);
        logr.trace("--------------------------------------------------------------------------------- Response content end -----------");
        
        return response;
    }
    
    
}
