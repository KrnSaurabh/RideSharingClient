package com.miamioh.ridesharingclient.feign.client;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.miamioh.ridesharingclient.model.request.RideSharingConfirmation;
import com.miamioh.ridesharingclient.model.response.RideSharingConfirmationAck;
import com.miamioh.ridesharingclient.model.response.TaxiResponse;

@FeignClient(name="DistributedRideSharing")// ,url="${props.DistributedRideSharing.url}")
@RibbonClient(name="DistributedRideSharing")
public interface ConfirmRideProxy {
	
	@PostMapping(value = "/RideSharing/RideConfirmation")
	public RideSharingConfirmationAck confirmRide(@RequestBody RideSharingConfirmation rideSharingConfirmation); 
	
	@GetMapping(value = "/RideSharing/TaxiResponse/{request_id}")
	public TaxiResponse getTaxiResponsesByRequestId( @PathVariable(value="request_id") String requestId);

}
