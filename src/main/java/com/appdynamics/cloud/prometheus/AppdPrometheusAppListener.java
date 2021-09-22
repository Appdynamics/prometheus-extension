/**
 * 
 */
package com.appdynamics.cloud.prometheus;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.appdynamics.cloud.prometheus.analytics.AnalyticsEventsDriver;
import com.appdynamics.cloud.prometheus.analytics.AnalyticsEventsSource;
import com.appdynamics.cloud.prometheus.config.AnalyticsEventsSourceConfig;
import com.appdynamics.cloud.prometheus.config.ServiceConfig;
import com.appdynamics.cloud.prometheus.utils.StringUtils;


/**
 * @author James Schneider
 *
 */
public class AppdPrometheusAppListener implements ApplicationConstants, ApplicationListener<ApplicationEvent> {

	private static Logger logr;
	private static ServiceConfig SRVCS_CONF;
	private static List<Thread> ANALYTICS_DRIVER_THREADS;
	
	public static String LOGGING_LEVEL = "info";
	
	/**
	 * 
	 */
	public AppdPrometheusAppListener() {
		
//		try {
//
//			String confPath = System.getProperty(SERVICE_CONF_KEY);
//			
//			
//			Yaml yaml = new Yaml(new Constructor(ServiceConfig.class));
//			InputStream inputStream = StringUtils.getFileAsStream(confPath);
//			
//			SRVCS_CONF = yaml.load(inputStream);
//			
//			logr = new Logger(AppdPrometheusAppListener.class.getSimpleName(), SRVCS_CONF.isDebugLogging());
//			
//		} catch (Throwable ex) {
//			ex.printStackTrace();
//			System.exit(1);
//		}
		
		
	}

	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof AvailabilityChangeEvent) {
			
			AvailabilityChangeEvent<?> ace = (AvailabilityChangeEvent<?>)event;
			
			if (ace.getState().equals(ReadinessState.ACCEPTING_TRAFFIC)) {
		
				try {

					Logger l = new Logger("");
					
					l.printBanner(true);
					
					l.log("##########################################################################################    STARTING APPDYNAMICS PROMETHEUS SERVICE    ################################################################################");
					l.carriageReturn();
					
					String confPath = System.getProperty(SERVICE_CONF_KEY);
					
					if (confPath == null || confPath.equals("")) {
						l.error("Missing JVM startup property -D" + SERVICE_CONF_KEY);
						l.error("Please set this property -D" + SERVICE_CONF_KEY + " with the full path to the configuration Yaml file like this | -D" + SERVICE_CONF_KEY + "=/opt/appdynamics/appd-prometheus/conf/config.yaml");
						System.exit(1);
					}
					
					l = null;
					
					Yaml yaml = new Yaml(new Constructor(ServiceConfig.class));
					InputStream inputStream = StringUtils.getFileAsStream(confPath);
					
					SRVCS_CONF = yaml.load(inputStream);
					
					logr = new Logger(AppdPrometheusAppListener.class.getSimpleName(), SRVCS_CONF.getLoggingLevel());
					
					LOGGING_LEVEL = SRVCS_CONF.getLoggingLevel();
					
					this.initializeServices();
					
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
				
				
			}
			
		}		
		
	}
	
	
	private void initializeServices() throws Throwable {
		
        List<AnalyticsEventsSourceConfig> aescList = SRVCS_CONF.getAnalyticsEventsSources();
        
        if (aescList != null) {
        	
        	ANALYTICS_DRIVER_THREADS = new ArrayList<Thread>();
        	
        	for (AnalyticsEventsSourceConfig aesConf : aescList) {
        
        		//logr.info("*****************************  Initializing Events Source for Schema '" + aesConf.getSchemaName() + "'  *****************************");
        		
            	AnalyticsEventsDriver driver;
            	AnalyticsEventsSource source;
    			
    			Class<?> clazz = Class.forName(aesConf.getEventsSourceClass());
    			Object object = clazz.newInstance();
    			source = (AnalyticsEventsSource)object;
    			
    			source.initialize(SRVCS_CONF, aesConf);
    			driver = new AnalyticsEventsDriver(SRVCS_CONF, source);
    			Thread driverThread = new Thread(driver);
    			driverThread.start();
    			ANALYTICS_DRIVER_THREADS.add(driverThread);
    			
    			Thread.currentThread().sleep(5000);
        		
        	}
        }		
		
	}
	

}
