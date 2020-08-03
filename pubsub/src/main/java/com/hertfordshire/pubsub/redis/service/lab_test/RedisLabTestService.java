package com.hertfordshire.pubsub.redis.service.lab_test;


import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.pubsub.redis.pojo.LabTestPojo;

import java.util.List;

public interface RedisLabTestService {

    void save(List<LabTestPojo> labTestPojoList);

    PaginationResponsePojo fetchWithPagination(int page, int size);

    List<LabTestPojo> fetch();

    void delete();
}
