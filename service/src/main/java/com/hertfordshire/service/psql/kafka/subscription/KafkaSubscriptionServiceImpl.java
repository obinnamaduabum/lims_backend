package com.hertfordshire.service.psql.kafka.subscription;


import com.hertfordshire.model.psql.KafkaSubscription;
import com.hertfordshire.model.psql.KafkaTopicModel;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.dao.psql.KafkaSubscriptionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class KafkaSubscriptionServiceImpl implements KafkaSubscriptionService {

    @Autowired
    private KafkaSubscriptionDao kafkaSubscriptionDao;

    @Override
    public KafkaSubscription add(PortalUser portalUser, KafkaTopicModel kafkaTopicModel) {


        KafkaSubscription kafkaSubscriptionModel = this.kafkaSubscriptionDao.findByPortalUserAndKafkaTopicModel(portalUser, kafkaTopicModel);

        if(kafkaSubscriptionModel == null) {
            kafkaSubscriptionModel = new KafkaSubscription();
            kafkaSubscriptionModel.setPortalUser(portalUser);
            kafkaSubscriptionModel.setKafkaTopicModel(kafkaTopicModel);
            return this.kafkaSubscriptionDao.save(kafkaSubscriptionModel);
        }

        return kafkaSubscriptionModel;
    }

    @Override
    public void remove(PortalUser portalUser, KafkaTopicModel kafkaTopicModel) {
        KafkaSubscription kafkaSubscriptionModel =
                this.kafkaSubscriptionDao.findByPortalUserAndKafkaTopicModel(portalUser, kafkaTopicModel);
        this.kafkaSubscriptionDao.delete(kafkaSubscriptionModel);
    }

    @Transactional
    @Override
    public void removeAll(PortalUser portalUser) {

        List<KafkaSubscription> kafkaSubscriptionList = this.findAll(portalUser);
        kafkaSubscriptionList.forEach(kafkaSubscription -> {
            this.kafkaSubscriptionDao.delete(kafkaSubscription);
        });

    }

    @Override
    public void remove(KafkaSubscription kafkaSubscription) {
        this.kafkaSubscriptionDao.delete(kafkaSubscription);
    }

    @Override
    public KafkaSubscription checkIfExists(PortalUser portalUser, KafkaTopicModel kafkaTopicModel) {
        return this.kafkaSubscriptionDao.findByPortalUserAndKafkaTopicModel(portalUser, kafkaTopicModel);
    }

    @Override
    public KafkaSubscription findByIdAndPortalUser(PortalUser portalUser, Long id) {
        return this.kafkaSubscriptionDao.findByIdAndPortalUser(id, portalUser);
    }

    @Override
    public List<KafkaSubscription> findAll(PortalUser portalUser) {
        return this.kafkaSubscriptionDao.findByPortalUser(portalUser);
    }
}
