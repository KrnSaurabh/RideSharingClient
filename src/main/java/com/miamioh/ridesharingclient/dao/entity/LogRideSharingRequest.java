package com.miamioh.ridesharingclient.dao.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.miamioh.ridesharingclient.model.request.RideSharingRequest;

import lombok.Getter;
import lombok.Setter;

@RedisHash(value="LogRideSharingRequest")
@Getter @Setter
public class LogRideSharingRequest implements Serializable{
	
	@Id
	private String requestId;
	private RideSharingRequest rideSharingRequest;

}
