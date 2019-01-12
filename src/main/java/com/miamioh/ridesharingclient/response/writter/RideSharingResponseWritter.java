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
import com.miamioh.ridesharingclient.model.response.RideSharingConfirmationAck;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RideSharingResponseWritter implements CommandLineRunner {

	@Value("${props.response.dir}")
	private String responseDir;

	private static final BlockingQueue<RideSharingConfirmationAck> responseQueue = new ArrayBlockingQueue<>(10);

	public void writeResponse(RideSharingConfirmationAck ack) {
		try {
			log.info("Writting Ack to File: "+ack);
			responseQueue.put(ack);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public class ResponseWritter implements Runnable {

		@Override
		public void run() {
			log.info("RideSharingResponseAckWritter Thread Started");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			RideSharingConfirmationAck ack = null;
			File file = new File(responseDir);
			try (FileWriter fileWriter = new FileWriter(file, true)){
				file.createNewFile();
				while (true) {
					ack = responseQueue.take();
					log.info("Ack recieved from queue by writting thread: "+ Thread.currentThread().getId()+"Response: "+ack);
					gson.toJson(ack, fileWriter);
					fileWriter.flush();
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
		log.info("****************************** Inside RideSharingResponseWritter **************************");
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(new ResponseWritter());
	}
}
