package com.hertfordshire.service.sequence.email_verification;


import com.hertfordshire.service.sequence.SequenceServiceImp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@EmailVerificationToken
@Component
public class EmailVerificationTokenSequence extends SequenceServiceImp {

    public EmailVerificationTokenSequence(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate,"email_verification_id");
    }
}

