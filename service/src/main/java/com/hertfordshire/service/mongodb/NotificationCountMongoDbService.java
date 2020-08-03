package com.hertfordshire.service.mongodb;

import com.hertfordshire.model.mongodb.NotificationCountMongoDb;
import com.hertfordshire.model.psql.PortalUser;

public interface NotificationCountMongoDbService {

    NotificationCountMongoDb incrementOrSave(Long count, PortalUser portalUser);

    NotificationCountMongoDb decrement(PortalUser portalUser);

    NotificationCountMongoDb findByPortalUserId(Long portalUserId);
}
