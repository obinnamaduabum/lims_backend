package com.hertfordshire.service.psql.sample_model_service;

import com.hertfordshire.dto.LabTestSampleDto;
import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.model.psql.OrdersModel;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.utils.errors.ApiError;
import org.springframework.http.ResponseEntity;

public interface SampleModelService {

    ResponseEntity<Object> sample(OrdersModel ordersModel,
                          LabTest labTest,
                          LabTestSampleDto labTestSampleDto,
                          PortalUser portalUser,
                          ApiError apiError,
                          String lang);
}
