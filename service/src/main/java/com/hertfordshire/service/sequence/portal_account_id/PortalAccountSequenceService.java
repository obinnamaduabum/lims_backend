package com.hertfordshire.service.sequence.portal_account_id;

import com.hertfordshire.service.sequence.SequenceServiceImp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@PortalAccountSequence
@Component
public class PortalAccountSequenceService extends SequenceServiceImp {
     public PortalAccountSequenceService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate,"portal_account_id");
    }
}
