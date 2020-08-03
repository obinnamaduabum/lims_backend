package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.KafkaSubscription;
import com.hertfordshire.model.psql.KafkaTopicModel;
import com.hertfordshire.model.psql.PortalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KafkaSubscriptionDao extends JpaRepository<KafkaSubscription, Long> {

    List<KafkaSubscription> findByPortalUser(PortalUser portalUser);

    KafkaSubscription findByIdAndPortalUser(Long id, PortalUser portalUser);

    KafkaSubscription findByPortalUserAndKafkaTopicModel(PortalUser portalUser, KafkaTopicModel kafkaTopicModel);
}
