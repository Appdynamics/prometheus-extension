package com.appdynamics.cloud.prometheus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppdPrometheusApplication {

	public static void main(String[] args) {
		
		//SpringApplication.run(AppdPrometheusApplication.class, args);
	    SpringApplication springApplication = new SpringApplication(AppdPrometheusApplication.class);
	    springApplication.addListeners(new AppdPrometheusAppListener());
	    springApplication.run(args);
	}

}
