package com.hertfordshire.dao.mongodb;


import com.hertfordshire.model.mongodb.LabTestTemplateAssignmentHistoryMongoDb;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabTestTemplateAssignmentHistoryMongoDbDao extends MongoRepository<LabTestTemplateAssignmentHistoryMongoDb, String> {

    List<LabTestTemplateAssignmentHistoryMongoDb> findByActualLabTestId(String id);

    List<LabTestTemplateAssignmentHistoryMongoDb> findByLabTestTemplateId(String id);

    List<LabTestTemplateAssignmentHistoryMongoDb> findByLabTestTemplateIdOrderByDateCreatedDesc(String id);
}
