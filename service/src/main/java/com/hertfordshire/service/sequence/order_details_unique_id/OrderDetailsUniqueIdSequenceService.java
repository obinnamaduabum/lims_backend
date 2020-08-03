package com.hertfordshire.service.sequence.order_details_unique_id;

import com.hertfordshire.service.sequence.SequenceServiceImp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@OrderDetailsUniqueIdSequence
@Component
public class OrderDetailsUniqueIdSequenceService extends SequenceServiceImp {
    public OrderDetailsUniqueIdSequenceService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, "order_details_unique_id");
    }
}

