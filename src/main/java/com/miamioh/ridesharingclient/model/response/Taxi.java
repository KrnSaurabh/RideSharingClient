package com.miamioh.ridesharingclient.model.response;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Taxi {
	
	private String taxiId;
	private String taxiNumber;
	private double longitude;
	private double latitude;
	private String model;
	private AtomicInteger noOfPassenger;
	
}
