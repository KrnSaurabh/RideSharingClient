package com.miamioh.ridesharingclient.model.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class RideSharingConfirmationAck {
	
	private String responseId;
	private boolean ackStatus;
	private Taxi taxi;
	private String message;

}
