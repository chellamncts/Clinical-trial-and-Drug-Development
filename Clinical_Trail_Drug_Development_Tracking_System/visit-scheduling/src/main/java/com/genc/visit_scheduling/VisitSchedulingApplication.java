package com.genc.visit_scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class VisitSchedulingApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisitSchedulingApplication.class, args);
	}

}
