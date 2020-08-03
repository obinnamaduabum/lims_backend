package com.hertfordshire.service.sequence.phone_number_verification_code;


import com.hertfordshire.service.sequence.SequenceServiceImp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@PhoneNumberVerificationCodeSequence
@Component
public class PhoneNumberVerificationCodeSequenceService  extends SequenceServiceImp {
    public PhoneNumberVerificationCodeSequenceService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, "phone_number_verification_id");
    }
}
