package com.hertfordshire.pubsub.redis.service.cart;



import com.hertfordshire.pubsub.redis.dto.CartDto;

import java.util.List;

public interface CartService {

    void removeAll();

    void remove();

    void add(String id, List<CartDto> cartDtoList);
}
