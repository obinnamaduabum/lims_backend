package com.hertfordshire.pubsub.redis.service.portal_user;


import com.hertfordshire.pubsub.redis.model.PortalUserModel;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

import static com.hertfordshire.utils.Utils.checkIfValidNumber;


@Service
public class PortalUserServiceImpl implements PortalUserService {

    private static final String KEY = "PortalUser";
    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;
    private static final int SESSION_TIMEOUT = 30;


    @Autowired
    public PortalUserServiceImpl(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public PortalUserModel fetchPortalUser(String formOfIdentification) {
        PortalUserModel portalUserModel = null;
        if (EmailValidator.getInstance(true).isValid(formOfIdentification.toLowerCase())) {
            portalUserModel = (PortalUserModel) hashOperations.get(KEY, formOfIdentification.toLowerCase().trim());
        } else if(checkIfValidNumber(formOfIdentification)) {
            portalUserModel = (PortalUserModel) hashOperations.get(KEY, formOfIdentification.toLowerCase().trim());
        }
        return portalUserModel;
    }

    @Override
    public void savePortalUser(PortalUserModel portalUser) {
      hashOperations.put(KEY,portalUser.getPhoneNumber(), portalUser);
      redisTemplate.expire(KEY,SESSION_TIMEOUT, TimeUnit.MINUTES);
    }


    @Override
    public void delete(String formOfIdentification) {
        hashOperations.delete(KEY, formOfIdentification);
    }
}
