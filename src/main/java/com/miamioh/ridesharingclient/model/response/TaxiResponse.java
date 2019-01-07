package com.miamioh.ridesharingclient.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TaxiResponse {
	
	private String responseId;
	private String requestId;
	private String taxiId;
	private String taxiNumber;
	private String taxiModel;
	private int availableSeats;
	private int pickUpIndex;
	private int dropIndex;
	private Long timeToDestinationInMinutes;
	private double distanceInKms;
	private double cost;
	private Long pickTimeInMinutes;
	
}
