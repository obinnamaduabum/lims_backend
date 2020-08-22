package com.hertfordshire.service.psql.lab_test_result;

import com.hertfordshire.dto.PatientResultDto;
import com.hertfordshire.model.psql.LabTestResultModel;
import com.hertfordshire.model.psql.PortalUser;


public interface LabTestResultService {

    LabTestResultModel save(PatientResultDto patientResultDto,
                            PortalUser loggedUser);

}
