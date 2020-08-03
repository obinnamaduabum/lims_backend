//package com.hertfordshire.service.psql.rabbitmq.queue;
//
//
//import com.hertfordshire.model.psql.RabbitMqExchangeModel;
//import com.hertfordshire.model.psql.RabbitMqQueueModel;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class RabbitmqQueueServiceImpl implements RabbitmqQueueService {
//
//
//    @Autowired
//    private RabbitMqQueueDao rabbitMqQueueDao;
//
//    @Override
//    public RabbitMqQueueModel save(RabbitMqExchangeModel rabbitMqExchangeModel, String name) {
//        RabbitMqQueueModel rabbitMqQueueModel = new RabbitMqQueueModel();
//        rabbitMqQueueModel.setName(name);
//        rabbitMqQueueModel.setRabbitMqExchangeModel(rabbitMqExchangeModel);
//        return this.rabbitMqQueueDao.save(rabbitMqQueueModel);
//    }
//
//    @Override
//    public void remove(String name) {
//        Optional<RabbitMqQueueModel> optionalRabbitMqQueueModel = this.rabbitMqQueueDao.findByName(name);
//        if(optionalRabbitMqQueueModel.isPresent()) {
//            RabbitMqQueueModel rabbitMqQueueModel = optionalRabbitMqQueueModel.get();
//            this.rabbitMqQueueDao.delete(rabbitMqQueueModel);
//        } else {
//        }
//    }
//}
