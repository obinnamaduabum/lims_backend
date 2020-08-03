package com.hertfordshire.service.psql.kafka.topic;



import com.hertfordshire.model.psql.KafkaTopicModel;
import com.hertfordshire.model.psql.PortalUser;

import java.util.List;

public interface KafkaTopicService {

    KafkaTopicModel add(String name, PortalUser portalUser);

    List<KafkaTopicModel> addAll(List<String> name, PortalUser portalUser);

    KafkaTopicModel findByName(String name);

    KafkaTopicModel findById(Long id);

    List<KafkaTopicModel> findAll();

    void remove(KafkaTopicModel kafkaTopicModel);
}
