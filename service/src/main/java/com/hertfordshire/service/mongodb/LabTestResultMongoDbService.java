package com.hertfordshire.service.mongodb;



import com.hertfordshire.dto.PatientRestDto;
import com.hertfordshire.model.mongodb.LabTestResultMongoDb;
import com.hertfordshire.model.psql.PortalUser;

import javax.transaction.Transactional;

public interface LabTestResultMongoDbService {

    @Transactional
    LabTestResultMongoDb save(PatientRestDto patientRestDto, PortalUser medicalLabScientist);

    LabTestResultMongoDb findByCode(String code);

}
