package com.hertfordshire.dao.mongodb;


import com.hertfordshire.model.mongodb.NotificationMongoDb;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationMongodbDao extends MongoRepository<NotificationMongoDb, String> {

    NotificationMongoDb findBy_id(ObjectId _id);

    NotificationMongoDb findByCodeAndPortalUserId(String code, Long portalUserId);

    List<NotificationMongoDb> findByPortalUserId(Long portalUserId, Pageable pageable);

    Number countByPortalUserId(Long portalUserId);

    void delete(NotificationMongoDb notificationMongoDb);

}