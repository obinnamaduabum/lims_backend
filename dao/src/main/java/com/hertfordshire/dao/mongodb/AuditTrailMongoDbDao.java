package com.hertfordshire.dao.mongodb;


import com.hertfordshire.model.mongodb.AuditTrailMongoDb;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailMongoDbDao extends MongoRepository<AuditTrailMongoDb, String> {


        AuditTrailMongoDb findBy_id(ObjectId _id);

        void delete(AuditTrailMongoDb deleted);

}
