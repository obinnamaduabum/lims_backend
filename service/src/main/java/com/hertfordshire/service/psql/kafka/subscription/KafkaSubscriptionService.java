package com.hertfordshire.service.psql.kafka.subscription;

import com.hertfordshire.model.psql.KafkaSubscription;
import com.hertfordshire.model.psql.KafkaTopicModel;
import com.hertfordshire.model.psql.PortalUser;

import java.util.List;

public interface KafkaSubscriptionService {

    KafkaSubscription add(PortalUser portalUser, KafkaTopicModel kafkaTopicModel);

    void remove(PortalUser portalUser, KafkaTopicModel kafkaTopicModel);

    void removeAll(PortalUser portalUser);

    void remove(KafkaSubscription kafkaSubscription);

    KafkaSubscription checkIfExists(PortalUser portalUser, KafkaTopicModel kafkaTopicModel);

    KafkaSubscription findByIdAndPortalUser(PortalUser portalUser, Long id);

    List<KafkaSubscription> findAll(PortalUser portalUser);
}
