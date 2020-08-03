package com.hertfordshire.pubsub.redis.service.cart;

import com.google.gson.Gson;
import com.hertfordshire.pubsub.redis.dto.CartDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private static final String KEY = "Cart";
    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;

    @Autowired
    private Gson gson;

    @Autowired
    public CartServiceImpl(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void removeAll() {

    }

    @Override
    public void remove() {

    }

    @Override
    public void add(String id, List<CartDto> cartDtoList) {
        hashOperations.put(KEY, id, cartDtoList);
    }
}
