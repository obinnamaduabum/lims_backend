package com.hertfordshire.pubsub.redis.service.payment;


import com.hertfordshire.pubsub.redis.model.PaymentMethodInfoModel;

import java.util.List;

public interface RedisPaymentService {

    void delete();

    List<PaymentMethodInfoModel> findAll();

    void saveAll(List<PaymentMethodInfoModel> paymentMethodInfoModels);
}
