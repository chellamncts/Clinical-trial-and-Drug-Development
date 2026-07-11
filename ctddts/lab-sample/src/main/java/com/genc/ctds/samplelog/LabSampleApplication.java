package com.genc.ctds.samplelog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LabSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(LabSampleApplication.class, args);
	}

}
