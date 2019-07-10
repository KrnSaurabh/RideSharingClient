package com.miamioh.ridesharingclient.dao.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.miamioh.ridesharingclient.model.response.RideSharingConfirmationAck;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@RedisHash("LogRideShareConfirmation")
public class LogRideShareConfirmation implements Serializable{
	
	@Id
	private String requestId;
	private RideSharingConfirmationAck rideSharingConfirmationAck;
	//private Long responseTimeInMillis;

}
