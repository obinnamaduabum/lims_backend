package com.hertfordshire.service.psql.rabbitmq.queue;

import com.hertfordshire.model.psql.RabbitMqExchangeModel;
import com.hertfordshire.model.psql.RabbitMqQueueModel;

public interface RabbitmqQueueService {
    RabbitMqQueueModel save(RabbitMqExchangeModel rabbitMqExchangeModel, String name);

    void remove(String name);
}
