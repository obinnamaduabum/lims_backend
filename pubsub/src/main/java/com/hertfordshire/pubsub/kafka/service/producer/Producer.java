package com.hertfordshire.pubsub.kafka.service.producer;


import com.hertfordshire.pubsub.pojo.NotificationPojo;
import com.hertfordshire.pubsub.pojo.NotificationPojoDateToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProperties.Producer.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String title, String message, String code, String url, List<String> topics){

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

//        logger.info(String.format("$$ -> Producing date --> %s", dateFormat.format(date)));
//        logger.info(String.format("$$ -> Producing title --> %s",title));
//        logger.info(String.format("$$ -> Producing message --> %s",message));
//        logger.info(String.format("$$ -> Producing url --> %s",url));
        NotificationPojoDateToString notificationPojo = new NotificationPojoDateToString();
        notificationPojo.setTitle(title);
        notificationPojo.setCode(code);
        notificationPojo.setMessage(message);
        notificationPojo.setDateUpdated(dateFormat.format(date));
        notificationPojo.setDateCreated(dateFormat.format(date));
        notificationPojo.setUrl(url);

        for(String topic: topics) {

            ListenableFuture<SendResult<String, Object>> future = this.kafkaTemplate.send(topic, notificationPojo);

            future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

                @Override
                public void onSuccess(SendResult<String, Object> result) {
                    logger.info("Sent message=[" + message +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");
                }
                @Override
                public void onFailure(Throwable ex) {
                   logger.info("Unable to send message=["
                            + message + "] due to : " + ex.getMessage());
                }
            });
        }
        this.kafkaTemplate.flush();
    }
}
