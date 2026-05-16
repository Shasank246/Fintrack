package com.fintrack.FinTrackV2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FinTrackV2Application {

	public static void main(String[] args) {
		SpringApplication.run(FinTrackV2Application.class, args);
	}

}
