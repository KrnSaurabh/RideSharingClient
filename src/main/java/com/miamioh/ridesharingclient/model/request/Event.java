package com.miamioh.ridesharingclient.model.request;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Event {
	
	private String requestId;
	private double longitude;
	private double latitude;
	private Date eventTime;
	private long tolerance;
	private boolean isPickup;
	private int index;
	private Date timeOfOccurance;
	
}
