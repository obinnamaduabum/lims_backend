package com.hertfordshire.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hertfordshire.utils.pojo.PhoneCodesPojo;
import com.hertfordshire.utils.pojo.ProperPhoneNumberPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PhoneNumberValidationUtil {


    private static final Logger logger = LoggerFactory.getLogger(PhoneNumberValidationUtil.class.getSimpleName());

    public static ProperPhoneNumberPojo validatePhoneNumber(String swissNumberStr, PhoneCodesPojo phoneCodesPojo) {

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {

            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(swissNumberStr, phoneCodesPojo.getAlpha2().toUpperCase());

            boolean isValid = phoneUtil.isValidNumber(swissNumberProto);


            if (isValid) {

                ProperPhoneNumberPojo properPhoneNumberPojo = new ProperPhoneNumberPojo();
                properPhoneNumberPojo.setFormattedNumber(""+swissNumberProto.getCountryCode() + swissNumberProto.getNationalNumber());
                properPhoneNumberPojo.setCountryCode("" + swissNumberProto.getCountryCode());
                properPhoneNumberPojo.setNationalNumber("" + swissNumberProto.getNationalNumber());
                return properPhoneNumberPojo;

            } else {

                logger.info("Phone number invalid");
                return null;
            }

        } catch (NumberParseException e) {
            e.printStackTrace();
            logger.info(e.getLocalizedMessage());
            return null;
        }
    }

    public static ProperPhoneNumberPojo validatePhoneNumber(String swissNumberStr) {

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {

            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse("+"+swissNumberStr, "");

            boolean isValid = phoneUtil.isValidNumber(swissNumberProto);

            if (isValid) {

                ProperPhoneNumberPojo properPhoneNumberPojo = new ProperPhoneNumberPojo();
                properPhoneNumberPojo.setFormattedNumber(""+swissNumberProto.getCountryCode() + swissNumberProto.getNationalNumber());
                properPhoneNumberPojo.setCountryCode("" + swissNumberProto.getCountryCode());
                properPhoneNumberPojo.setNationalNumber("" + swissNumberProto.getNationalNumber());
                return properPhoneNumberPojo;

            } else {

                logger.info("Phone number invalid");
                return null;
            }

        } catch (NumberParseException e) {
            e.printStackTrace();
            logger.info(e.getLocalizedMessage());
            return null;
        }
    }
}
