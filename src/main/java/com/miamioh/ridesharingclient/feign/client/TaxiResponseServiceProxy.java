package com.miamioh.ridesharingclient.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.miamioh.ridesharingclient.model.response.TaxiResponse;

@FeignClient(name="DistributedRideSharingTaxiResponse" ,url="${props.DistributedRideSharing.url}")
public interface TaxiResponseServiceProxy {
	
	@GetMapping(value = "/RideSharing/TaxiResponses/{request_id}")
	public TaxiResponse getTaxiResponsesByRequestId( @PathVariable(value="request_id") String requestId);

}
