package com.example.Automated.Application.Mangament;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class AutomatedApplicationMangamentApplication {

	static {
		// Đặt múi giờ mặc định của Java Application là UTC
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(AutomatedApplicationMangamentApplication.class, args);
	}

}
