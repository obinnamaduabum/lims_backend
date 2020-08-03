package com.hertfordshire.pubsub.redis.service.payment;

import com.hertfordshire.pubsub.redis.model.PaymentMethodInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class RedisPaymentServiceImpl implements RedisPaymentService {

    private static final String KEY = "LabTest";
    private static final String NAME = "MERLIN_LABS_PAYMENT";
    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;

    @Autowired
    public RedisPaymentServiceImpl(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void delete() {
        hashOperations.delete(KEY, NAME);
    }

    @Override
    public List<PaymentMethodInfoModel> findAll() {
        return (List<PaymentMethodInfoModel>) hashOperations.get(KEY, NAME);
    }

    @Override
    public void saveAll(List<PaymentMethodInfoModel> paymentMethodInfoModels) {
        hashOperations.put(KEY, NAME, paymentMethodInfoModels);
    }
}
