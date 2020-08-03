package com.hertfordshire.service.mongodb;


import com.hertfordshire.model.mongodb.NotificationMongoDb;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.dao.mongodb.NotificationMongodbDao;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.pubsub.pojo.NotificationPojo;
import com.hertfordshire.service.sequence.notification_mongodb_id.NotificationMongodbSequenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NotificationMongodbServiceImpl implements NotificationMongodbService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationMongodbServiceImpl.class.getSimpleName());

//    @Autowired
//    private NotificationMongodbDao notificationMongodbDao;

    @Autowired
    private NotificationMongodbSequenceService notificationMongodbSequenceService;

    @Override
    public void saveAll(List<NotificationPojo> notificationPojoList, PortalUser portalUser) {

        List<NotificationMongoDb> notificationMongoDbList = new ArrayList<>();

        for (NotificationPojo notificationPojo : notificationPojoList) {


            NotificationMongoDb foundNotificationMongoDb = this.findByCodeAndPortalUser(notificationPojo.getCode(), portalUser);

            if (foundNotificationMongoDb == null) {
                NotificationMongoDb notificationMongoDb = new NotificationMongoDb();
                notificationMongoDb.setMessage(notificationPojo.getMessage());
                notificationMongoDb.setTitle(notificationPojo.getTitle());
                notificationMongoDb.setUrl(notificationPojo.getUrl());
                notificationMongoDb.setName(notificationPojo.getName());
                notificationMongoDb.setPortalUserId(portalUser.getId());
                notificationMongoDb.setCode(notificationPojo.getCode().toUpperCase());

                Date dateCreated = notificationPojo.getDateCreated();

                Date dateUpdated = notificationPojo.getDateUpdated();

                notificationMongoDb.setDateCreated(dateCreated);

                notificationMongoDb.setDateUpdated(dateUpdated);

                notificationMongoDbList.add(notificationMongoDb);
            } else {
                logger.info("already created");
            }
        }

        //this.notificationMongodbDao.saveAll(notificationMongoDbList);
    }

    @Override
    public void save(NotificationPojo notificationPojo, PortalUser portalUser) {

    }


    @Override
    public NotificationMongoDb saveAndSetAsRead(NotificationPojo notificationPojo, PortalUser portalUser, boolean read) {
        NotificationMongoDb notificationMongoDb = new NotificationMongoDb();
        notificationMongoDb.setMessage(notificationPojo.getMessage());
        notificationMongoDb.setTitle(notificationPojo.getTitle());
        notificationMongoDb.setUrl(notificationPojo.getUrl());
        notificationMongoDb.setName(notificationPojo.getName());
        notificationMongoDb.setPortalUserId(portalUser.getId());
        notificationMongoDb.setCode(notificationPojo.getCode().toUpperCase());
        Date dateCreated = notificationPojo.getDateCreated();
        Date dateUpdated = notificationPojo.getDateUpdated();
        notificationMongoDb.setDateCreated(dateCreated);
        notificationMongoDb.setDateUpdated(dateUpdated);
        notificationMongoDb.setRead(read);
        //return this.notificationMongodbDao.save(notificationMongoDb);

        return null;
    }

    @Override
    public NotificationMongoDb findByCodeAndPortalUser(String code, PortalUser portalUser) {
        // return this.notificationMongodbDao.findByCodeAndPortalUserId(code.toUpperCase(), portalUser.getId());
        return null;
    }

    @Override
    public List<NotificationMongoDb> findAllByPortalUserOrderByDateCreated(PortalUser portalUser) {
        Pageable sortedByDateCreated = PageRequest.of(0, 7, Sort.by("dateCreated").descending());
        try {
            // return this.notificationMongodbDao.findByPortalUserId(portalUser.getId(), sortedByDateCreated);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    @Override
    public PaginationResponsePojo findAllNotificationsByPortalUserOrderByDateCreatedWithPaginationResponsePojo(PortalUser portalUser, int page, int size) {

        if(size == 0){
            size = 10;
        }
        Pageable sortedByDateCreated = PageRequest.of(page, size, Sort.by("dateCreated").descending());
        try {

            List<NotificationMongoDb> notificationMongoDbList = null;

                    // = this.notificationMongodbDao.findByPortalUserId(portalUser.getId(), sortedByDateCreated);

            PaginationResponsePojo paginationResponsePojo = new PaginationResponsePojo();
            paginationResponsePojo.setDataList(notificationMongoDbList);
            paginationResponsePojo.setPageNumber((long) page);
            paginationResponsePojo.setPageSize((long) size);

            Number count = null;

                    // = this.notificationMongodbDao.countByPortalUserId(portalUser.getId());
            paginationResponsePojo.setLength((long) count);

            return paginationResponsePojo;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public NotificationMongoDb update(NotificationMongoDb notificationMongoDb) {
        return null;
        // return this.notificationMongodbDao.save(notificationMongoDb);
    }

    @Override
    public String getNotificationId() {
        return String.format("NOTIF_%04d%02d%02d%05d",
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth(),
                notificationMongodbSequenceService.getNextId());
    }
}
