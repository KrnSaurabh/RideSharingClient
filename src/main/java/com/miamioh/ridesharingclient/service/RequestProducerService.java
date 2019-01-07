package com.miamioh.ridesharingclient.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.miamioh.ridesharingclient.feign.client.ConfirmRideProxy;
import com.miamioh.ridesharingclient.feign.client.TaxiResponseServiceProxy;
import com.miamioh.ridesharingclient.model.request.Event;
import com.miamioh.ridesharingclient.model.request.RideSharingConfirmation;
import com.miamioh.ridesharingclient.model.request.RideSharingRequest;
import com.miamioh.ridesharingclient.model.response.RideSharingConfirmationAck;
import com.miamioh.ridesharingclient.model.response.TaxiResponse;
import com.miamioh.ridesharingclient.response.writter.RideSharingResponseWritter;
import com.miamioh.ridesharingclient.response.writter.TaxiResponseWritter;
//41.890922026,-87.618868355,2016-12-18 01:43:00,41.892072635,-87.62887415700001,17031081403.0,17031081600.0

@Service
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
		
		kafkaTemplate.send(topicName, request);
		try {
			Thread.sleep(60000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		TaxiResponse taxiResponsesByRequestId = taxiResponseServiceProxy.getTaxiResponsesByRequestId(requestId);
		taxiResponseWritter.writeResponse(taxiResponsesByRequestId);
		
		RideSharingConfirmation confirmRequest = new RideSharingConfirmation();
		confirmRequest.setConfirmed(true);
		confirmRequest.setResponseId(taxiResponsesByRequestId.getResponseId());
		confirmRequest.setTaxiId(taxiResponsesByRequestId.getTaxiId());
		RideSharingConfirmationAck confirmRideAck = confirmRideProxy.confirmRide(confirmRequest);
		writter.writeResponse(confirmRideAck);
	}

}
