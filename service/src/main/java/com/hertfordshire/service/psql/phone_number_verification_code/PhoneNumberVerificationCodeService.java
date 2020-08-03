package com.hertfordshire.service.psql.phone_number_verification_code;


import com.hertfordshire.dto.PhoneNumberVerificationDto;
import com.hertfordshire.model.psql.PhoneNumberVerificationCodes;
import com.hertfordshire.model.psql.PortalUser;

import java.util.List;

public interface PhoneNumberVerificationCodeService {

    PhoneNumberVerificationCodes save(PhoneNumberVerificationDto phoneNumberVerificationDto);

    PhoneNumberVerificationCodes validateCode(String code);

    List<PhoneNumberVerificationCodes> findByVerificationCodeAndDateCreated(String code);

    PortalUser fetchUserFromCode(String code);
}
