package com.hertfordshire.service.sequence.lab_test_template_code;


import com.hertfordshire.service.sequence.SequenceServiceImp;
import com.hertfordshire.service.sequence.lab_test.LabTestSequence;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@LabTestSequence
@Component
public class LabTestTemplateSequenceImpl extends SequenceServiceImp {

    public LabTestTemplateSequenceImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, "lab_test_template_id");
    }
}

