package com.hertfordshire.service.sequence.portal_user_id;



import com.hertfordshire.service.sequence.SequenceServiceImp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@PortalUserSequence
@Component
public class PortalUserSequenceService extends SequenceServiceImp {
     public PortalUserSequenceService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate,"portal_user_id");
    }
}
