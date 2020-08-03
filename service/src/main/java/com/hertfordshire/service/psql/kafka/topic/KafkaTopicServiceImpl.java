package com.hertfordshire.service.psql.kafka.topic;

import com.hertfordshire.dao.psql.KafkaTopicDao;
import com.hertfordshire.model.psql.KafkaTopicModel;
import com.hertfordshire.model.psql.PortalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class KafkaTopicServiceImpl implements KafkaTopicService {

    @Autowired
    private KafkaTopicDao kafkaTopicDao;

    @Override
    public KafkaTopicModel add(String name, PortalUser portalUser) {
        KafkaTopicModel kafkaTopicModel = new KafkaTopicModel();
        kafkaTopicModel.setName(name.toLowerCase().trim());
        kafkaTopicModel.setCreatedBy(portalUser);
        return this.kafkaTopicDao.save(kafkaTopicModel);
    }

    @Transactional
    @Override
    public List<KafkaTopicModel> addAll(List<String> topics, PortalUser portalUser) {

        List<KafkaTopicModel> kafkaTopicModels = new ArrayList<>();
        for(String topic: topics) {
            KafkaTopicModel kafkaTopicModel = new KafkaTopicModel();
            kafkaTopicModel.setName(topic.toLowerCase().trim());
            kafkaTopicModel.setCreatedBy(portalUser);
            kafkaTopicModels.add(kafkaTopicModel);
        }
        return this.kafkaTopicDao.saveAll(kafkaTopicModels);
    }

    @Override
    public List<KafkaTopicModel> findAll() {
        return this.kafkaTopicDao.findAll();
    }

    @Override
    public KafkaTopicModel findByName(String name) {
        return this.kafkaTopicDao.findByName(name.toLowerCase());
    }

    @Override
    public KafkaTopicModel findById(Long id) {
        Optional<KafkaTopicModel> optionalKafkaTopicModel = this.kafkaTopicDao.findById(id);
        return optionalKafkaTopicModel.orElse(null);
    }


    @Override
    public void remove(KafkaTopicModel kafkaTopicModel) {
        this.kafkaTopicDao.delete(kafkaTopicModel);
    }
}
