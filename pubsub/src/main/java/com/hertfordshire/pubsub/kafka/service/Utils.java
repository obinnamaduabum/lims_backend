package com.hertfordshire.pubsub.kafka.service;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class.getSimpleName());

    private final static String BOOTSTRAP_SERVERS = "127.0.0.1:9092";

    private static Properties fetchProperties(String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        // props.put(ConsumerConfig.CLIENT_ID_CONFIG, groupId);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        // props.put("enable.partition.eof", "false");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }


    private Map<String, Object> getDefaultConsumerConfigs(String groupId) {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // 手动设置自动提交为false,交由 spring-kafka 启动的invoker执行提交
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        propsMap.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "10000");
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // 从partition中获取消息最大大小
        propsMap.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "102400");

        return propsMap;
    }

    public KafkaConsumer<String, String> fetchConsumer(String groupId) {
        Map<String, Object> propsMap = this.getDefaultConsumerConfigs(groupId);
        return new KafkaConsumer<>(propsMap);
    }

    public void subscribe(List<String> topics, String groupId) {
        // final Properties props = fetchProperties(groupId);
        final KafkaConsumer<String, String> consumer = this.fetchConsumer(groupId);
        // Subscribe to the topic.
        //logger.info("creating topic");
        //createTopic(props, topics, consumer);
        createTopic(topics, groupId);
        logger.info("start sub");
        consumerSubscribe(consumer, topics);
    }


    private static void consumerSubscribe(KafkaConsumer<String, String> consumer,
                                          List<String> topics) {

        final int giveUp = 100;
        int noRecordsCount = 0;

        try {
            consumer.subscribe(topics);
            // consumer.commitAsync();
            boolean flag = true;
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(giveUp);

                if (records.count()==0) {
                    noRecordsCount++;
                    if (noRecordsCount > giveUp) break;
                    else continue;
                }

                if (flag) {
                    Set<TopicPartition> assignments = consumer.assignment();
                    assignments.forEach(topicPartition ->
                            consumer.seek(
                                    topicPartition,
                                    0));
                    flag = false;
                }


                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            logger.info("done kobi");
            consumer.close();
        }
    }


    private static void createTopic(List<String> topics, String groupId) {

            final Properties properties = fetchProperties(groupId);

            AdminClient adminClient = AdminClient.create(properties);
            List<NewTopic> newTopics = new ArrayList<NewTopic>();
            for (String topic : topics) {
                NewTopic newTopic = new NewTopic(topic, 1, (short) 1); //new NewTopic(topicName, numPartitions, replicationFactor)
                newTopics.add(newTopic);
            }
            adminClient.createTopics(newTopics);
            adminClient.close();
    }
}
