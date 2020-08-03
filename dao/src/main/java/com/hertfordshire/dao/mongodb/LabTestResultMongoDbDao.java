package com.hertfordshire.dao.mongodb;

import com.hertfordshire.model.mongodb.LabTestResultMongoDb;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabTestResultMongoDbDao extends MongoRepository<LabTestResultMongoDb, String> {

    LabTestResultMongoDb findByCode(String code);
}
