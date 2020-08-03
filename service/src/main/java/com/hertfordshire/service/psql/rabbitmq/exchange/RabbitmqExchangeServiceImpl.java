//package com.hertfordshire.service.psql.rabbitmq.exchange;
//
//import com.hertfordshire.dao.psql.RabbitMqExchangeDao;
//import com.hertfordshire.model.psql.RabbitMqExchangeModel;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class RabbitmqExchangeServiceImpl implements RabbitmqExchangeService {
//
//    @Autowired
//    private RabbitMqExchangeDao rabbitMqExchangeDao;
//
//    @Override
//    public RabbitMqExchangeModel save(String name) {
//        RabbitMqExchangeModel rabbitMqExchangeModel = new RabbitMqExchangeModel();
//        rabbitMqExchangeModel.setName(name);
//        return this.rabbitMqExchangeDao.save(rabbitMqExchangeModel);
//    }
//
//    @Override
//    public void remove(String name) {
//        Optional<RabbitMqExchangeModel> optionalRabbitMqExchangeModel = this.rabbitMqExchangeDao.findByName(name);
//
//        if(optionalRabbitMqExchangeModel.isPresent()) {
//            RabbitMqExchangeModel rabbitMqExchangeModel = optionalRabbitMqExchangeModel.get();
//            this.rabbitMqExchangeDao.delete(rabbitMqExchangeModel);
//        } else {
//            System.out.println();
//        }
//    }
//}
