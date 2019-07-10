package com.miamioh.ridesharingclient.dao.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.miamioh.ridesharingclient.dao.entity.LogRideSharingRequest;

@Repository
public interface LogRideSharingRequestRepository extends CrudRepository<LogRideSharingRequest, String>{

}
