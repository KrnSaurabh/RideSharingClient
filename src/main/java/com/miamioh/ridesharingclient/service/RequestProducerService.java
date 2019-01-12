package com.miamioh.ridesharingclient.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.miamioh.ridesharingclient.feign.client.ConfirmRideProxy;
import com.miamioh.ridesharingclient.feign.client.TaxiResponseServiceProxy;
import com.miamioh.ridesharingclient.model.request.Event;
import com.miamioh.ridesharingclient.model.request.RideSharingConfirmation;
import com.miamioh.ridesharingclient.model.request.RideSharingRequest;
import com.miamioh.ridesharingclient.model.response.RideSharingConfirmationAck;
import com.miamioh.ridesharingclient.model.response.TaxiResponse;
import com.miamioh.ridesharingclient.response.writter.RideSharingResponseWritter;
import com.miamioh.ridesharingclient.response.writter.TaxiResponseWritter;

import lombok.extern.slf4j.Slf4j;
//41.890922026,-87.618868355,2016-12-18 01:43:00,41.892072635,-87.62887415700001,17031081403.0,17031081600.0

@Service
@Slf4j
public class RequestProducerService {
	
	@Autowired
    private KafkaTemplate<String, RideSharingRequest> kafkaTemplate;
	
	@Value(value = "${kafka.topic}")
    private String topicName;
	
	@Autowired
	private TaxiResponseServiceProxy taxiResponseServiceProxy;
	
	@Autowired
	private ConfirmRideProxy confirmRideProxy;
	
	@Autowired
	private RideSharingResponseWritter writter;
	
	@Autowired
	private TaxiResponseWritter taxiResponseWritter;
	
	public void createAndSendRequestAsync(String input) {
		log.info("Inside RideSharing Request Producer Service");
		RideSharingRequest request = new RideSharingRequest();
		String requestId = UUID.randomUUID().toString();
		request.setRequestID(requestId);
		String[] tokens = input.split(",");
		Event pickUpEvent = new Event();
		pickUpEvent.setLatitude(Double.parseDouble(tokens[0]));
		pickUpEvent.setLongitude(Double.parseDouble(tokens[1]));
		pickUpEvent.setRequestId(requestId);
		pickUpEvent.setPickup(true);
		Event dropEvent = new Event();
		dropEvent.setLatitude(Double.parseDouble(tokens[3]));
		dropEvent.setLongitude(Double.parseDouble(tokens[4]));
		dropEvent.setRequestId(requestId);
		dropEvent.setPickup(false);
		request.setDropOffEvent(dropEvent);
		request.setPickUpEvent(pickUpEvent);
		request.setTimestamp(java.sql.Date.valueOf(LocalDate.now()));
		log.info("Generated RideSharing Request: "+request);
		
		ListenableFuture<SendResult<String, RideSharingRequest>> future = kafkaTemplate.send(topicName, request);
		
		StopWatch watch = new StopWatch();
		watch.start();
		future.addCallback(
				new ListenableFutureCallback<SendResult<String, RideSharingRequest>>() {

					@Override
					public void onSuccess(
							SendResult<String, RideSharingRequest> result) {
						log.info("Successfully published message to the kafka topic with key={} and offset={}",
								request, result.getRecordMetadata().offset());

						watch.stop();
						log.info("total time taken to send the message to topic :{}", request.getRequestID(), 
								watch.getTotalTimeMillis());
					}

					@Override
					public void onFailure(Throwable ex) {
						log.error("Unable to publish message to the kafka topic with key={}",
								request, ex);

						watch.stop();
						log.info("total time taken to send the message to topic :", request.getRequestID(), 
								watch.getTotalTimeMillis());
					}
				});
		
		try {
			log.info("Sleeping before getting the response: "+Thread.currentThread().getId());
			Thread.sleep(30000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("After Sleep "+Thread.currentThread().getId());
		TaxiResponse taxiResponsesByRequestId = taxiResponseServiceProxy.getTaxiResponsesByRequestId(requestId);
		log.info("Taxi Response: "+taxiResponsesByRequestId);
		taxiResponseWritter.writeResponse(taxiResponsesByRequestId);
		
		RideSharingConfirmation confirmRequest = new RideSharingConfirmation();
		confirmRequest.setConfirmed(true);
		confirmRequest.setResponseId(taxiResponsesByRequestId.getResponseId());
		confirmRequest.setTaxiId(taxiResponsesByRequestId.getTaxiId());
		RideSharingConfirmationAck confirmRideAck = confirmRideProxy.confirmRide(confirmRequest);
		log.info("Taxi Confirmation Ack: "+confirmRideAck);
		writter.writeResponse(confirmRideAck);
	}

}
