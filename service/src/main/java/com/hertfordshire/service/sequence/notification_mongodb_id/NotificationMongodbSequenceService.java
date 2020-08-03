package com.hertfordshire.service.sequence.notification_mongodb_id;


import com.hertfordshire.service.sequence.SequenceServiceImp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@NotificationMongodbSequence
@Component
public class NotificationMongodbSequenceService extends SequenceServiceImp {
     public NotificationMongodbSequenceService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, "notification_mongodb_id");
    }
}
