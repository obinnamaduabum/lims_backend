package com.hertfordshire.service.psql.rabbitmq.exchange;

import com.hertfordshire.model.psql.RabbitMqExchangeModel;

public interface RabbitmqExchangeService {

    RabbitMqExchangeModel save(String name);

    void remove(String name);
}
