package com.miamioh.ridesharingclient.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RideSharingConfirmation {
	
	private String responseId;
	private String taxiId;
	private boolean isConfirmed;

}
