package com.hertfordshire.service.mongodb;



import com.hertfordshire.model.mongodb.NotificationMongoDb;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.pubsub.pojo.NotificationPojo;

import java.util.List;

public interface NotificationMongodbService {

    void saveAll(List<NotificationPojo> notificationPojoList, PortalUser portalUser);

    void save(NotificationPojo notificationPojo, PortalUser portalUser);

    NotificationMongoDb saveAndSetAsRead(NotificationPojo notificationPojo, PortalUser portalUser, boolean read);

    NotificationMongoDb findByCodeAndPortalUser(String code, PortalUser portalUser);

    List<NotificationMongoDb> findAllByPortalUserOrderByDateCreated(PortalUser portalUser);

    PaginationResponsePojo findAllNotificationsByPortalUserOrderByDateCreatedWithPaginationResponsePojo(PortalUser portalUser, int page, int size);

    NotificationMongoDb update(NotificationMongoDb notificationMongoDb);

    String getNotificationId();
}
