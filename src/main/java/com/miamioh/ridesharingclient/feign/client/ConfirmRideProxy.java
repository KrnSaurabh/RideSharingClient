package com.miamioh.ridesharingclient.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.miamioh.ridesharingclient.model.request.RideSharingConfirmation;
import com.miamioh.ridesharingclient.model.response.RideSharingConfirmationAck;

@FeignClient(name="DistributedRideSharing" ,url="${props.DistributedRideSharing.url}")
public interface ConfirmRideProxy {
	
	@PostMapping(value = "/RideSharing/RideConfirmation")
	public RideSharingConfirmationAck confirmRide(@RequestBody RideSharingConfirmation rideSharingConfirmation); 

}
