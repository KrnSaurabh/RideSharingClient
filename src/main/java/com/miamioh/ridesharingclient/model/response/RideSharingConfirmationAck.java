package com.miamioh.ridesharingclient.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RideSharingConfirmationAck {
	
	private String responseId;
	private boolean ackStatus;
	private Taxi taxi;
	private String message;

}
