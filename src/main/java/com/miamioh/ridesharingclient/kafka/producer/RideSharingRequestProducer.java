package com.miamioh.ridesharingclient.kafka.producer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.miamioh.ridesharingclient.service.RequestProducerService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RideSharingRequestProducer {
	
	@Autowired
	private RequestProducerService requestProducerService;
	
	@PostMapping(value="/RideSharingClient/requests", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public void createRideSharingRequestsFromFile(@RequestParam("file") MultipartFile file) throws IOException {
		log.info("Inside RideSharing Request Producer");
		BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
		String input = "";
		while((input = reader.readLine()) != null) {
			String line = input;
			log.info("Input String Read: "+line);
			CompletableFuture.runAsync(()-> requestProducerService.createAndSendRequestAsync(line));
		}
	}
}
