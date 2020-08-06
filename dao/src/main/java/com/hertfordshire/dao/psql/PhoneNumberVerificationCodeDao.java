package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.PhoneNumberVerificationCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PhoneNumberVerificationCodeDao extends JpaRepository<PhoneNumberVerificationCodes, Long> {

    List<PhoneNumberVerificationCodes> findByVerificationCodeAndDateCreatedBetween(String code, Date start, Date end);

    PhoneNumberVerificationCodes findByVerificationCode(String code);

    List<PhoneNumberVerificationCodes> findByCode(String code);
}
