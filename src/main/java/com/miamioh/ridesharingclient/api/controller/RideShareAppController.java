package com.miamioh.ridesharingclient.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.miamioh.ridesharingclient.dao.entity.LogRideShareConfirmation;
import com.miamioh.ridesharingclient.dao.entity.LogRideShareResponse;
import com.miamioh.ridesharingclient.dao.repository.LogRideShareConfirmationRepository;
import com.miamioh.ridesharingclient.dao.repository.LogRideShareResponseRepository;
import com.miamioh.ridesharingclient.dao.repository.LogRideSharingRequestRepository;
import com.miamioh.ridesharingclient.model.response.LogStatisticResponse;
import com.miamioh.ridesharingclient.model.response.RideSharingConfirmationAck;
import com.miamioh.ridesharingclient.model.response.Taxi;
import com.miamioh.ridesharingclient.model.response.TaxiResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RideShareAppController {
	
	@Autowired
	private LogRideShareResponseRepository logRideShareResponseRepository;
	
	@Autowired
	private LogRideShareConfirmationRepository logConfirmationRepository;
	
	@Autowired
	private LogRideSharingRequestRepository logRequestRepository;
	
	@GetMapping(value = "/RideSharing/getStats", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public LogStatisticResponse getStatistics() {
		log.info("Calculating Stats");
		LogStatisticResponse response = new LogStatisticResponse();
		
		long totalNoOfrequests = logRequestRepository.count(); // this is count of all requests triggered including requests without response
		response.setTotalNoOfRequestRecieved(totalNoOfrequests);
		
		log.info("totalNoOfrequests: "+totalNoOfrequests);
		Double totalNoOfRequestConfirmed = 0.0; // total no of requests for which confirmation ack is true
		Double noOfConfirmedRideWithOnePassenger = 0.0; // total no of requests for which confirmation ack is true && no of passenger =1
		Double noOfConfirmedRideWithTwoPassenger = 0.0;
		Double noOfConfirmedRideWithThreePassenger = 0.0;
		Double noOfConfirmedRideWithFourPassenger = 0.0;
		for(LogRideShareConfirmation rideShareConfirmationLog: logConfirmationRepository.findAll()) {
			RideSharingConfirmationAck rideSharingConfirmationAck = rideShareConfirmationLog.getRideSharingConfirmationAck();
			if(rideSharingConfirmationAck.isAckStatus()) {
				totalNoOfRequestConfirmed++;
				Taxi taxi = rideSharingConfirmationAck.getTaxi();
				switch(taxi.getNoOfPassenger()){
				case 1: noOfConfirmedRideWithOnePassenger++; break;
				case 2: noOfConfirmedRideWithTwoPassenger++; break;
				case 3: noOfConfirmedRideWithThreePassenger++; break;
				case 4: noOfConfirmedRideWithFourPassenger++; break;
				}
			}
		}
		log.info("totalNoOfRequestConfirmed: "+totalNoOfRequestConfirmed);
		log.info("noOfConfirmedRideWithOnePassenger: "+noOfConfirmedRideWithOnePassenger);
		log.info("noOfConfirmedRideWithTwoPassenger: "+noOfConfirmedRideWithTwoPassenger);
		log.info("noOfConfirmedRideWithThreePassenger: "+noOfConfirmedRideWithThreePassenger);
		log.info("noOfConfirmedRideWithFourPassenger: "+noOfConfirmedRideWithFourPassenger);

		response.setRideConfirmationSuccessRate(totalNoOfRequestConfirmed/totalNoOfrequests);
		response.setSuccessRateWithOnePassenger(noOfConfirmedRideWithOnePassenger/totalNoOfRequestConfirmed);
		response.setSuccessRateWithTwoPassenger(noOfConfirmedRideWithTwoPassenger/totalNoOfRequestConfirmed);
		response.setSuccessRateWithThreePassenger(noOfConfirmedRideWithThreePassenger/totalNoOfRequestConfirmed);
		response.setSuccessRateWithFourPassenger(noOfConfirmedRideWithFourPassenger/totalNoOfRequestConfirmed);
		
		Long noOfRequestWithResponse=0L; //Total number of requests for which response was recieved
		Double totalPickupWaitingTime = 0.0; //summation of pickUpTime for all responses
		Double totalResponseTime = 0.0; // summation of Response time in fetching the response
		Double totalPSOResponseTime = 0.0;
		
		Iterable<LogRideShareResponse> findAll = logRideShareResponseRepository.findAll();
		for(LogRideShareResponse responseLog: findAll) {
			noOfRequestWithResponse++;
			TaxiResponse taxiResponse = responseLog.getTaxiResponse();
			totalPickupWaitingTime = totalPickupWaitingTime + taxiResponse.getPickTimeInMinutes();
			totalPSOResponseTime = totalPSOResponseTime + (taxiResponse.getPsoResponseTimeInSeconds()!=null? taxiResponse.getPsoResponseTimeInSeconds():0.0);
			totalResponseTime = totalResponseTime + responseLog.getTotalResponseTimeInMillis();
		}
		log.info("noOfRequestWithResponse: "+noOfRequestWithResponse);
		log.info("totalPickupWaitingTime: "+totalPickupWaitingTime);
		log.info("totalPSOResponseTime: "+totalPSOResponseTime);
		log.info("totalResponseTime: "+totalResponseTime);
		
		response.setTotalNoOfRequestWithResponse(noOfRequestWithResponse);
		response.setAveragePassengerWaitingTimeInMins(totalPickupWaitingTime/noOfRequestWithResponse);
		response.setAveragePSOResponseTimeInSec(totalPSOResponseTime/noOfRequestWithResponse);
		response.setAverageResponseTime(totalResponseTime/noOfRequestWithResponse);
		//response.setAverageNumberOfMessages(totalNoOfrequests/);
		
		return response;
	}

}
