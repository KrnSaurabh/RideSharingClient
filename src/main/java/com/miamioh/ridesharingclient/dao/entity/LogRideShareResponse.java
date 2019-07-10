package com.miamioh.ridesharingclient.dao.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.miamioh.ridesharingclient.model.response.TaxiResponse;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter 
@RedisHash("LogRideShareResponse")
public class LogRideShareResponse implements Serializable {
	
	@Id
	private String requestId;
	private TaxiResponse taxiResponse;
	private Long totalResponseTimeInMillis;

}
