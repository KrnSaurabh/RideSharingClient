package com.miamioh.ridesharingclient.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class RideSharingRequest {
	
	private String requestID;
	private Event pickUpEvent;
	private Event dropOffEvent;
	
}
