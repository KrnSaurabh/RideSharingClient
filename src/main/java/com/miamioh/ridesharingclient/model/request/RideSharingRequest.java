package com.miamioh.ridesharingclient.model.request;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RideSharingRequest {
	
	private String requestID;
	private Event pickUpEvent;
	private Event dropOffEvent;
	private Date timestamp;
	
}
