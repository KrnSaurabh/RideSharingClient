package com.miamioh.ridesharingclient.response.writter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.miamioh.ridesharingclient.model.response.TaxiResponse;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TaxiResponseWritter implements CommandLineRunner{


	@Value("${props.taxi.response.dir}")
	private String responseDir;

	private static final BlockingQueue<TaxiResponse> responseQueue = new ArrayBlockingQueue<>(10);

	public void writeResponse(TaxiResponse response) {
		try {
			log.info("Writting Response to File: "+response);
			responseQueue.put(response);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public class ResponseWritterTaxi implements Runnable {

		@Override
		public void run() {
			log.info("TaxiResponseWritter Thread Started");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			TaxiResponse response = null;
			File file = new File(responseDir);
			try {
				file.createNewFile();
				while (true) {
					response = responseQueue.take();
					log.info("Response recieved from queue by writting thread: "+ Thread.currentThread().getId()+"Response: "+response);
					gson.toJson(response, new FileWriter(file, true));
				}
			} catch (JsonIOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("****************************** Inside TaxiResponseWritter **************************");
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(new ResponseWritterTaxi());
	}


}
