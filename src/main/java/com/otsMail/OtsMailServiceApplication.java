package com.otsMail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OtsMailServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OtsMailServiceApplication.class, args);
	}

}
