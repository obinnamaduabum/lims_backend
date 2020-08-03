package com.hertfordshire.pubsub.redis.service.lab_test;

import com.google.gson.Gson;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.pubsub.redis.pojo.LabTestPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class RedisRedisLabTestServiceImpl implements RedisLabTestService {

    private static final String KEY = "LabTest";
    private static final String NAME = "MERLIN_LABS_LABTEST";
    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;


    @Autowired
    public RedisRedisLabTestServiceImpl(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void save(List<LabTestPojo> labTestPojoList) {
        hashOperations.put(KEY, NAME, labTestPojoList);
    }

    @Override
    public PaginationResponsePojo fetchWithPagination(int page, int size) {
        List<LabTestPojo> labTestPojoList = this.fetch();
        int total = labTestPojoList.size();
        PaginationResponsePojo paginationResponsePojo = new PaginationResponsePojo();
        paginationResponsePojo.setLength((long) total);
        paginationResponsePojo.setPageNumber((long) page);
        paginationResponsePojo.setPageSize((long) size);
        paginationResponsePojo.setDataList(labTestPojoList);
        return paginationResponsePojo;
    }


    @Override
    public List<LabTestPojo> fetch() {
        return (List<LabTestPojo>) hashOperations.get(KEY, NAME);
    }

    @Override
    public void delete() {
        hashOperations.delete(KEY, NAME);
    }
}
