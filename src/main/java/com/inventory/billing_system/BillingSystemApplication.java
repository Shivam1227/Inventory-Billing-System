package com.inventory.billing_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// @SpringBootApplication = enables autoconfiguration, component scanning,
// and configuration. This is what starts the entire Spring Boot application.
@SpringBootApplication
public class BillingSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(BillingSystemApplication.class, args);
		System.out.println("\n====================================================");
		System.out.println(" Inventory & Billing System is running!");
		System.out.println(" API Base URL: http://localhost:8080/api");
		System.out.println(" Open Postman and start testing.");
		System.out.println("====================================================\n");
	}
}