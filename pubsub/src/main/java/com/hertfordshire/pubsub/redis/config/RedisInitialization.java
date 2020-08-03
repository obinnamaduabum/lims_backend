package com.hertfordshire.pubsub.redis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;

@Component
public class RedisInitialization {

    @Value("${spring.redis.host}")
    private String REDIS_HOSTNAME;
    @Value("${spring.redis.port}")
    private int REDIS_PORT;
    @Value("${spring.redis.password}")
    private String REDIS_PASSWORD;

    private static final Logger logger = LoggerFactory.getLogger(RedisInitialization.class);


    @PostConstruct
    private void initialize() {

        try {
           // this.resetRedis();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void resetRedis() {

//        int redisPort;
//        try {
//           redisPort = Integer.parseInt(REDIS_PORT);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }

        Jedis jedis = new Jedis(REDIS_HOSTNAME, REDIS_PORT);
        jedis.auth(REDIS_PASSWORD);
        jedis.connect();
        jedis.flushAll();
    }
}
