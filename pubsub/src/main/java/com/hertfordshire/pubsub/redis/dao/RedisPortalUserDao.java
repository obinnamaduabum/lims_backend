package com.hertfordshire.pubsub.redis.dao;


import com.hertfordshire.pubsub.redis.model.PortalUserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPortalUserDao extends CrudRepository<PortalUserModel, String> {

    PortalUserModel findByEmail(String email);

    PortalUserModel findByPhoneNumber(String phoneNumber);
}