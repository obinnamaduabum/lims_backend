package com.hertfordshire.dao.mongodb;


import com.hertfordshire.model.mongodb.LabTestTemplateMongoDb;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabTestTemplateMongoDbDao extends MongoRepository<LabTestTemplateMongoDb, String> {

    LabTestTemplateMongoDb findByCode(String code);
}
