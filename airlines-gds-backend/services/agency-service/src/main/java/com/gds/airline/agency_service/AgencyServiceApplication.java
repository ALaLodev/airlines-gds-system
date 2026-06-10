package com.gds.airline.agency_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AgencyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgencyServiceApplication.class, args);
	}

}
