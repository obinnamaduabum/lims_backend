package com.hertfordshire.service.sequence.order_id;

import com.hertfordshire.service.sequence.SequenceServiceImp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@OrderIdSequence
@Component
public class OrderIdSequenceImpl extends SequenceServiceImp {

    public OrderIdSequenceImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, "test_order");
    }
}

