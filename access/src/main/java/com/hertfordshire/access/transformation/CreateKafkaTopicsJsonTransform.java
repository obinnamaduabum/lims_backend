package com.hertfordshire.access.transformation;

import com.google.gson.Gson;
import com.hertfordshire.model.psql.KafkaTopicModel;
import com.hertfordshire.service.psql.kafka.topic.KafkaTopicService;
import com.hertfordshire.utils.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import static com.hertfordshire.access.transformation.Transformer.*;

@Component
public class CreateKafkaTopicsJsonTransform {

    private Gson gson;

    private static final Logger logger = Logger.getLogger(CreateKafkaTopicsJsonTransform.class.getSimpleName());

    @Autowired
    private KafkaTopicService kafkaTopicService;

    public CreateKafkaTopicsJsonTransform() {
        this.gson = new Gson();
    }

    public void create() {

        BufferedReader bufferedReader;

        InputStream inputStream = ResourceUtil.getResourceAsStream(TRANSFORMATION_DATA_FOLDER + File.separator + JSON_FOLDER + File.separator + KAFKA_TOPICS);

        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String[] topics = gson.fromJson(bufferedReader, String[].class);

        // logger.info(this.gson.toJson(topics));
        logger.info("Adding topics");
        for (String topic : topics) {
            KafkaTopicModel kafkaTopicModel =
                    this.kafkaTopicService.findByName(topic.toLowerCase());
            if (kafkaTopicModel != null) {
                logger.info("topic already added exist");
            } else {
                this.kafkaTopicService.add(topic, null);
            }
        }
        logger.info("Done adding topics");
    }
}
