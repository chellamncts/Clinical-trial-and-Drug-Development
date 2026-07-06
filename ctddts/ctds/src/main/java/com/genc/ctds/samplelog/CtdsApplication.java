package com.genc.ctds.samplelog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CtdsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CtdsApplication.class, args);
	}

}
