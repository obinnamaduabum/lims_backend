package com.hertfordshire.service.mongodb;


import com.hertfordshire.model.mongodb.NotificationCountMongoDb;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.dao.mongodb.NotificationCountMongodbDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationCountMongoDbServiceImpl implements NotificationCountMongoDbService {

//    @Autowired
//    private NotificationCountMongodbDao notificationCountMongodbDao;


    // update me

    @Override
    public NotificationCountMongoDb incrementOrSave(Long count, PortalUser portalUser) {

        NotificationCountMongoDb notificationCountMongoDb = null;
                //this.notificationCountMongodbDao.findByPortalUserId(portalUser.getId());

        try {

            if (notificationCountMongoDb != null) {
                    notificationCountMongoDb.setCount(notificationCountMongoDb.getCount() + count);
                    return null;
                    //return this.notificationCountMongodbDao.save(notificationCountMongoDb);
            } else {
                NotificationCountMongoDb newNotificationCountMongoDb = new NotificationCountMongoDb();
                newNotificationCountMongoDb.setPortalUserId(portalUser.getId());
                if (count > 0L) {
                    newNotificationCountMongoDb.setCount(count);
                } else {
                    newNotificationCountMongoDb.setCount(0L);
                }

                return null;
                //return this.notificationCountMongodbDao.save(newNotificationCountMongoDb);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public NotificationCountMongoDb decrement(PortalUser portalUser) {

        try {

            NotificationCountMongoDb notificationCountMongoDb = null;
                    //this.notificationCountMongodbDao.findByPortalUserId(portalUser.getId());

            if(notificationCountMongoDb != null) {
                if(notificationCountMongoDb.getCount() > 0L) {
                    notificationCountMongoDb.setCount(notificationCountMongoDb.getCount() - 1);
                    return null;
                    //return this.notificationCountMongodbDao.save(notificationCountMongoDb);
                }

            }

        } catch (Exception e){
            e.printStackTrace();
        }
            return null;
    }

    @Override
    public NotificationCountMongoDb findByPortalUserId(Long portalUserId) {
        NotificationCountMongoDb foundNotificationCountMongoDb = null;
                //= this.notificationCountMongodbDao.findByPortalUserId(portalUserId);
        if(foundNotificationCountMongoDb != null) {
            return foundNotificationCountMongoDb;
        } else {
            NotificationCountMongoDb notificationCountMongoDb = new NotificationCountMongoDb();
            notificationCountMongoDb.setCount(0L);
            return null;
            //return this.notificationCountMongodbDao.save(notificationCountMongoDb);
        }
    }
}
