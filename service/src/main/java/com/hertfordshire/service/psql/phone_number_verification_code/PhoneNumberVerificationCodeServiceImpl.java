package com.hertfordshire.service.psql.phone_number_verification_code;

import com.google.gson.Gson;
import com.hertfordshire.dto.PhoneNumberVerificationDto;
import com.hertfordshire.model.psql.PhoneNumberVerificationCodes;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.dao.psql.PhoneNumberVerificationCodeDao;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.service.sequence.phone_number_verification_code.PhoneNumberVerificationCodeSequenceService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class PhoneNumberVerificationCodeServiceImpl implements PhoneNumberVerificationCodeService {


    private final Logger logger = LoggerFactory.getLogger(PhoneNumberVerificationCodeServiceImpl.class.getSimpleName());

    @Value("${phoneNumberValidForInMinutes}")
    private int phoneNumberValidForInMinutes;

    @Autowired
    private PhoneNumberVerificationCodeSequenceService phoneNumberVerificationCodeSequenceService;

    @Autowired
    private PhoneNumberVerificationCodeDao phoneNumberVerificationCodeDao;

    @Autowired
    private PortalUserDao portalUserDao;

    private Gson gson;

    public PhoneNumberVerificationCodeServiceImpl() {
        this.gson = new Gson();
    }

    @Override
    public PhoneNumberVerificationCodes save(PhoneNumberVerificationDto phoneNumberVerificationDto) {

        String code = String.format("CODE_%04d%02d%02d%05d",
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth(),
                phoneNumberVerificationCodeSequenceService.getNextId()
        );


        String numeric = RandomStringUtils.randomNumeric(4);

        PhoneNumberVerificationCodes phoneNumberVerificationCodes = new PhoneNumberVerificationCodes();
        phoneNumberVerificationCodes.setCode(code);
        phoneNumberVerificationCodes.setType(phoneNumberVerificationDto.getType());
        phoneNumberVerificationCodes.setVerificationCode(numeric);
        phoneNumberVerificationCodes.setExpiryDate(phoneNumberValidForInMinutes);
        phoneNumberVerificationCodes.setPhoneNumber(phoneNumberVerificationDto.getPhoneNumber());
        phoneNumberVerificationCodeDao.save(phoneNumberVerificationCodes);
        return phoneNumberVerificationCodes;
    }

    @Override
    public List<PhoneNumberVerificationCodes> findByVerificationCodeAndDateCreated(String code) {

        List<PhoneNumberVerificationCodes> phoneNumberVerificationCodesList = phoneNumberVerificationCodeDao.findByCode(code);

        phoneNumberVerificationCodesList.forEach(phoneNumberVerificationCodes -> {

        });

        return new ArrayList<>();
    }


    public PhoneNumberVerificationCodes validateCode(String code) {

        PhoneNumberVerificationCodes phoneNumberVerificationCodes = phoneNumberVerificationCodeDao.findByVerificationCode(code);

        if (phoneNumberVerificationCodes == null) {
            return null;
        }


        Calendar cal = Calendar.getInstance();
        if ((phoneNumberVerificationCodes.getExpiryDate()
                .getTime() - cal.getTime()
                .getTime()) <= 0) {

            phoneNumberVerificationCodes.setCodeExpired(true);
            phoneNumberVerificationCodes = phoneNumberVerificationCodeDao.save(phoneNumberVerificationCodes);
            return phoneNumberVerificationCodes;
        }

        return phoneNumberVerificationCodes;
    }


    @Override
    public PortalUser fetchUserFromCode(String code) {

        PhoneNumberVerificationCodes phoneNumberVerificationCodes = phoneNumberVerificationCodeDao.findByVerificationCode(code);

        this.logger.info(phoneNumberVerificationCodes.getPhoneNumber());

        return portalUserDao.findByPhoneNumber(phoneNumberVerificationCodes.getPhoneNumber());
    }

}
