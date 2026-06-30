package com.genc.Lab_Sample_and_IP_Tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LabSampleAndIpTrackingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LabSampleAndIpTrackingApplication.class, args);
	}

}

