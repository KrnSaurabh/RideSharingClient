package com.miamioh.ridesharingclient.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString

public class RideSharingConfirmation {
	
	private String responseId;
	private String taxiId;
	private boolean isConfirmed;

}
