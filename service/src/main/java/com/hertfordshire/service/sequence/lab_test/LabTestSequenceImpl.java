package com.hertfordshire.service.sequence.lab_test;


import com.hertfordshire.service.sequence.SequenceServiceImp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@LabTestSequence
@Component
public class LabTestSequenceImpl extends SequenceServiceImp {

    public LabTestSequenceImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, "lab_test_id");
    }
}

