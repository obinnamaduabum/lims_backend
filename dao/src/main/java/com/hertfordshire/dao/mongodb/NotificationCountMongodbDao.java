package com.hertfordshire.dao.mongodb;


import com.hertfordshire.model.mongodb.NotificationCountMongoDb;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationCountMongodbDao extends MongoRepository<NotificationCountMongoDb, String> {

    NotificationCountMongoDb findByPortalUserId(Long portalUserId);

}
