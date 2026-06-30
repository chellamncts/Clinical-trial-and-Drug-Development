package com.genc.visit_scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VisitSchedulingApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisitSchedulingApplication.class, args);
	}

}
