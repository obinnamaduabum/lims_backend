package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.LimitPhoneNumberVerificationSms;
import com.hertfordshire.model.psql.PortalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LimitPhoneNumberVerificationSmsDao extends JpaRepository<LimitPhoneNumberVerificationSms, Long> {

    LimitPhoneNumberVerificationSms findByOwner(PortalUser portalUser);
}
