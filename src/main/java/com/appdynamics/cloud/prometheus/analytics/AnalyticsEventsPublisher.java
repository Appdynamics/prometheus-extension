/**
 * 
 */
package com.appdynamics.cloud.prometheus.analytics;

/**
 * @author James Schneider
 *
 */
public interface AnalyticsEventsPublisher {

	// ....
	public abstract void publishEvents(String jsonPayload) throws Throwable;
}
