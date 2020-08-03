package com.hertfordshire.pubsub.kafka.service.consumer;

import com.google.gson.Gson;
import com.hertfordshire.pubsub.kafka.service.Utils;
import com.hertfordshire.pubsub.pojo.NotificationPojo;
import com.hertfordshire.pubsub.pojo.NotificationPojoDateToString;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class Consumer {
    private final Logger logger = LoggerFactory.getLogger(Consumer.class);


    @Autowired
    private Gson gson;

    @Autowired
    private Utils utils;

    public void unsubscribe(String groupId) {
        final KafkaConsumer<String, String> consumer = this.utils.fetchConsumer(groupId);
        consumer.unsubscribe();
        consumer.commitAsync();
        consumer.close();
    }

    public List<NotificationPojo> fetchMessages(String groupId, List<String> topics) {

        List<NotificationPojo> notificationPojoList = new ArrayList<>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        final KafkaConsumer<String, String> consumer = this.utils.fetchConsumer(groupId);
        consumer.subscribe(topics);

        final int giveUp = 100;
        int noRecordsCount = 0;

        // consumer.subscribe(Arrays.asList("cc", "n"));

        while (true) {
            final ConsumerRecords<String, String> consumerRecords = consumer.poll(10);
            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }
            consumerRecords.forEach(record -> {
                logger.info("Consumer Record:(%d, %s, %d, %d)\n", record.key(), record.value(), record.partition(), record.offset());
                // Gson gson =  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                // Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new CustomDateSerializer()).create();
                logger.info(record.value());
                NotificationPojoDateToString notificationPojoDateToString = this.gson.fromJson(record.value(), NotificationPojoDateToString.class);

                logger.info("DateCreated");
                logger.info(notificationPojoDateToString.getDateCreated());
                logger.info(notificationPojoDateToString.getDateUpdated());

                NotificationPojo notificationPojo = new NotificationPojo();
                notificationPojo.setTitle(notificationPojoDateToString.getTitle());
                notificationPojo.setMessage(notificationPojoDateToString.getMessage());
                notificationPojo.setUrl(notificationPojoDateToString.getUrl());
                notificationPojo.setName(notificationPojoDateToString.getName());
                notificationPojo.setCode(notificationPojoDateToString.getCode());
                notificationPojo.setRead(false);
                try {
                    notificationPojo.setDateCreated(dateFormat.parse(notificationPojoDateToString.getDateCreated()));
                    notificationPojo.setDateUpdated(dateFormat.parse(notificationPojoDateToString.getDateCreated()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                notificationPojoList.add(notificationPojo);
            });
        }

        consumer.close();
        logger.info("DONE...");
        return notificationPojoList;
    }
}
