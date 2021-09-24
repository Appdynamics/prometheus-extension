/**
 * 
 */
package com.appdynamics.cloud.prometheus.awssigv4;

import com.amazonaws.transform.Unmarshaller;

/**
 * @author James Schneider
 *
 */
public class StringTransformer implements Unmarshaller<String, String> {

	/**
	 * 
	 */
	public StringTransformer() {
		
	}

	@Override
	public String unmarshall(String in) throws Exception {
		return in;
	}

}
