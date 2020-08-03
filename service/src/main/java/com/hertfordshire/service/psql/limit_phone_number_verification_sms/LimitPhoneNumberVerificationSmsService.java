package com.hertfordshire.service.psql.limit_phone_number_verification_sms;


import com.hertfordshire.model.psql.PortalUser;

public interface LimitPhoneNumberVerificationSmsService {

    boolean saveOrUpdate(PortalUser portalUser);
}
