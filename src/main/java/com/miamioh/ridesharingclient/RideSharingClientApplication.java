package com.miamioh.ridesharingclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RideSharingClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(RideSharingClientApplication.class, args);
	}

}

