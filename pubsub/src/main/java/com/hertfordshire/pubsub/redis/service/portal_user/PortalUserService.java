package com.hertfordshire.pubsub.redis.service.portal_user;

;
import com.hertfordshire.pubsub.redis.model.PortalUserModel;
import org.springframework.stereotype.Service;

@Service
public interface PortalUserService {

    PortalUserModel fetchPortalUser(String formOfIdentification);

    void savePortalUser(PortalUserModel portalUser);

    void delete(String formOfIdentification);
}
