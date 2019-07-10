package com.miamioh.ridesharingclient.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.miamioh.ridesharingclient.dao.entity.LogRideShareConfirmation;
import com.miamioh.ridesharingclient.dao.entity.LogRideShareResponse;
import com.miamioh.ridesharingclient.dao.entity.LogRideSharingRequest;
import com.miamioh.ridesharingclient.dao.repository.LogRideShareConfirmationRepository;
import com.miamioh.ridesharingclient.dao.repository.LogRideShareResponseRepository;
import com.miamioh.ridesharingclient.dao.repository.LogRideSharingRequestRepository;
import com.miamioh.ridesharingclient.feign.client.ConfirmRideProxy;
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
	
	@Autowired
	private LogRideShareResponseRepository logResponseRepository;
	
	@Autowired
	private LogRideShareConfirmationRepository logConfirmationRepository;
	
	@Autowired
	private LogRideSharingRequestRepository logRequestRepository;
	
	
	@Value(value = "${kafka.topic}")
    private String topicName;
	
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
		//request.setTimestamp(java.sql.Date.valueOf(LocalDate.now()));
		log.info("Generated RideSharing Request: "+request);
		LogRideSharingRequest logReq = new LogRideSharingRequest();
		logReq.setRequestId(requestId);
		logReq.setRideSharingRequest(request);
		logRequestRepository.save(logReq);
		kafkaTemplate.send(topicName, request);
		
		try {
			log.info("Sleeping before getting the response: "+Thread.currentThread().getId());
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		log.info("After Sleep "+Thread.currentThread().getId());
		StopWatch watch = new StopWatch();
		watch.start();
		TaxiResponse taxiResponsesByRequestId = confirmRideProxy.getTaxiResponsesByRequestId(requestId);
		watch.stop();
		if(taxiResponsesByRequestId != null) {
			LogRideShareResponse responseLog = new LogRideShareResponse();
			responseLog.setRequestId(requestId);
			responseLog.setTaxiResponse(taxiResponsesByRequestId);
			responseLog.setTotalResponseTimeInMillis(watch.getTotalTimeMillis());
			logResponseRepository.save(responseLog);
			
			log.info("Taxi Response: "+taxiResponsesByRequestId);
			taxiResponseWritter.writeResponse(taxiResponsesByRequestId);
			
			RideSharingConfirmation confirmRequest = new RideSharingConfirmation();
			confirmRequest.setRequestId(requestId);
			confirmRequest.setConfirmed(true);
			confirmRequest.setResponseId(taxiResponsesByRequestId.getResponseId());
			confirmRequest.setTaxiId(taxiResponsesByRequestId.getTaxiId());
			//long startTime = System.currentTimeMillis();
			RideSharingConfirmationAck confirmRideAck = confirmRideProxy.confirmRide(confirmRequest);
			
			int retryCount = 5;
			while ( !confirmRideAck.isAckStatus() && retryCount > 0) {
				log.info("Taxi Confirmation Ack: "+confirmRideAck);
				writter.writeResponse(confirmRideAck);
				try {
					log.info("Sleeping before getting the response: "+Thread.currentThread().getId());
					Thread.sleep(10000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				taxiResponsesByRequestId = confirmRideProxy.getTaxiResponsesByRequestId(requestId);
				/*responseLog = new RideShareResponseLog();
				responseLog.setRequestId(requestId);
				responseLog.setTaxiResponse(taxiResponsesByRequestId);
				responseLog.setTotalResponseTimeInMillis(watch.getTotalTimeMillis());
				responseLogRepository.save(responseLog);*/
				log.info("Taxi Response: "+taxiResponsesByRequestId);
				taxiResponseWritter.writeResponse(taxiResponsesByRequestId);
				if(taxiResponsesByRequestId !=null) {
					confirmRequest = new RideSharingConfirmation();
					confirmRequest.setRequestId(requestId);
					confirmRequest.setConfirmed(true);
					confirmRequest.setResponseId(taxiResponsesByRequestId.getResponseId());
					confirmRequest.setTaxiId(taxiResponsesByRequestId.getTaxiId());
					confirmRideAck = confirmRideProxy.confirmRide(confirmRequest);
				}
				
				retryCount--;
			}
			//long endTime = System.currentTimeMillis();
			log.info("Taxi Confirmation Ack: "+confirmRideAck);
			LogRideShareConfirmation confirmationLog = new LogRideShareConfirmation();
			confirmationLog.setRequestId(requestId);
			confirmationLog.setRideSharingConfirmationAck(confirmRideAck);
			//confirmationLog.setResponseTimeInMillis(endTime-startTime);
			logConfirmationRepository.save(confirmationLog);
			writter.writeResponse(confirmRideAck);
		}
		
	}

}
