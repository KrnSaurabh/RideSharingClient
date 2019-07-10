package com.miamioh.ridesharingclient.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter

public class LogStatisticResponse {
	
	@JsonProperty("Total_No_Of_Requests_Recieved")
	private Long totalNoOfRequestRecieved;
	
	@JsonProperty("Total_No_Of_Requests_With_Response")
	private Long totalNoOfRequestWithResponse;
	
	@JsonProperty("Average_PSO_Response_Time_In_Sec")
	private Double averagePSOResponseTimeInSec;
	
	@JsonProperty("Success_Rate_of_Ride_Sharing")
	private Double rideConfirmationSuccessRate;
	
	@JsonProperty("Success_Rate_of_1st_passenger")
	private Double successRateWithOnePassenger;
	
	@JsonProperty("Success_Rate_of_2nd_passenger")
	private Double successRateWithTwoPassenger;
	
	@JsonProperty("Success_Rate_of_3rd_passenger")
	private Double successRateWithThreePassenger;
	
	@JsonProperty("Success_Rate_of_4th_passenger")
	private Double successRateWithFourPassenger;
	
	@JsonProperty("Distance_Ratio_of_Ride_Sharing")
	private Double distanceRatioOfRideSharing;
	
	@JsonProperty("Average_Passenger_Waiting_Time_In_Minutes")
	private Double averagePassengerWaitingTimeInMins;
	
	@JsonProperty("Average_Number_of_Messages")
	private Double averageNumberOfMessages;
	
	@JsonProperty("Average_Request_processing_time_In_Millis")
	private Double averageResponseTime; 

	
}
