package com.hertfordshire.service.mongodb;

import com.hertfordshire.dto.LabTestAssignmentDto;
import com.hertfordshire.model.mongodb.LabTestTemplateAssignmentHistoryMongoDb;
import com.hertfordshire.model.psql.LabTest;

import javax.transaction.Transactional;
import java.util.List;

public interface LabTestTemplateAssignmentHistoryMongoDbService {

    @Transactional
    LabTestTemplateAssignmentHistoryMongoDb save(LabTestAssignmentDto labTestAssignmentDto, LabTest labTest);

    List<LabTestTemplateAssignmentHistoryMongoDb> findAllByActualLabTestId(String actualLabTestId);

    List<LabTestTemplateAssignmentHistoryMongoDb> findAllByLabTestTemplateId(String Id);

    List<LabTestTemplateAssignmentHistoryMongoDb> findByLabTestTemplateIdOOrderByDateCreatedDesc(String Id);


}
