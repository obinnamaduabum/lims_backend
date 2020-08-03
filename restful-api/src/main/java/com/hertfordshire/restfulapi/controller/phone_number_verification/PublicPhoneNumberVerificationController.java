package com.hertfordshire.restfulapi.controller.phone_number_verification;

import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.access.errors.CustomBadRequestException;
import com.hertfordshire.dao.psql.AdminSettingsDao;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.dto.PhoneNumberProperVerificationDto;
import com.hertfordshire.dto.PhoneNumberVerificationDto;
import com.hertfordshire.model.psql.AdminSettings;
import com.hertfordshire.model.psql.PhoneNumberVerificationCodes;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.restfulapi.controller.portal_user.PublicPortalUserController;
import com.hertfordshire.service.psql.limit_phone_number_verification_sms.LimitPhoneNumberVerificationSmsService;
import com.hertfordshire.service.psql.phone_number_verification_code.PhoneNumberVerificationCodeService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.smsmodule.pojo.SendSmsPojo;
import com.hertfordshire.smsmodule.service.MultiTexterService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.PublicBaseApiController;
import com.hertfordshire.utils.lhenum.GenericStatusConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PublicPhoneNumberVerificationController extends PublicBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(PublicPortalUserController.class.getSimpleName());

    @Autowired
    private MessageUtil messageUtil;

    private Gson gson;

    @Autowired
    private PhoneNumberVerificationCodeService phoneNumberVerificationCodeService;


    @Autowired
    private MultiTexterService multiTexterService;

    @Autowired
    private LimitPhoneNumberVerificationSmsService limitPhoneNumberVerificationSmsService;

    @Autowired
    private PortalUserDao portalUserDao;

    @Value("${if.remaining.units.is.below}")
     private String ifRemainingUnitsIsBelow;

    @Autowired
    private AdminSettingsDao adminSettingsDao;


    public PublicPhoneNumberVerificationController() {
        this.gson = new Gson();
    }

    @PostMapping("/auth/phone_number/verify")
    public ResponseEntity<Object> phoneNumber(@Valid @RequestBody PhoneNumberVerificationDto phoneNumberVerificationDto,
                                              BindingResult bindingResult) {

        ApiError apiError = null;
        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        }

        String swissNumberStr = phoneNumberVerificationDto.getPhoneNumber();
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {

            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(swissNumberStr, phoneNumberVerificationDto.getCode());
            //System.out.println(gson.toJson(swissNumberProto));
            boolean isValid = phoneUtil.isValidNumber(swissNumberProto);


            if (isValid) {
                PhoneNumberProperVerificationDto phoneNumberProperVerificationDto =
                        new PhoneNumberProperVerificationDto();
                phoneNumberProperVerificationDto.setFormattedNumber("+" + swissNumberProto.getCountryCode() + swissNumberProto.getNationalNumber());
                phoneNumberProperVerificationDto.setCountryCode("" + swissNumberProto.getCountryCode());
                phoneNumberProperVerificationDto.setNationalNumber("" + swissNumberProto.getNationalNumber());

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("phone.number.valid", "en"),
                        true, new ArrayList<>(), phoneNumberProperVerificationDto);
            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("phone.number.is.invalid", "en"),
                        false, new ArrayList<>(), null);
            }

        } catch (NumberParseException e) {

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("phone.number.is.invalid", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @GetMapping("/auth/phone_number/is_valid")
    public ResponseEntity<Object> isPhoneNumberValid(@RequestParam("phoneNumber") String phoneNumber,
                                                     HttpServletRequest request) {

        ApiError apiError = null;
        if (StringUtils.isBlank(phoneNumber)) {
            apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, messageUtil.getMessage("user.creation.successful", "en"),
                    true, new ArrayList<>(), null);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        String swissNumberStr = phoneNumber;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {

            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(swissNumberStr, "CH");
            boolean isValid = phoneUtil.isValidNumber(swissNumberProto);

            if (isValid) {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.creation.successful", "en"),
                        true, new ArrayList<>(), null);
            } else {
                apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, messageUtil.getMessage("user.creation.successful", "en"),
                        false, new ArrayList<>(), null);
            }

        } catch (NumberParseException e) {


            apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, messageUtil.getMessage("user.creation.successful", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @GetMapping("/auth/phone_number/verification/code")
    public ResponseEntity<Object> verifyPhoneNumberByCode(@RequestParam("code") String code,
                                                          HttpServletResponse res,
                                                          HttpServletRequest request,
                                                          Authentication authentication) {

        ApiError apiError = null;

        PhoneNumberVerificationCodes phoneNumberVerificationCodes = null;

        // requestPrincipal = userService.getPrincipal(res, request, authentication);

        // String email = requestPrincipal.getEmail();

        // PortalUser portalUser = portalUserService.findPortalUserByEmail(email);

        phoneNumberVerificationCodes = phoneNumberVerificationCodeService.validateCode(code);

        if (phoneNumberVerificationCodes != null) {

            if (phoneNumberVerificationCodes.isCodeExpired()) {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("verification.code.expired", "en"),
                        false, new ArrayList<>(), null);
            } else {

                logger.info("xxxx: " + phoneNumberVerificationCodes.getPhoneNumber());
                PortalUser portalUser = portalUserDao.findByPhoneNumber(phoneNumberVerificationCodes.getPhoneNumber().trim());
                if (portalUser != null) {
                    portalUser.setPhoneNumberVerified(true);
                    portalUser.setEmailOrPhoneNumberIsVerified(true);
                    portalUser.setUserStatus(GenericStatusConstant.ACTIVE);
                    this.portalUserDao.save(portalUser);
                } else {
                    logger.info("user not found");
                }

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("verification.code.valid", "en"),
                        true, new ArrayList<>(), phoneNumberVerificationCodes);
            }
        } else {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("verification.code.invalid", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @GetMapping("/auth/phone_number/send_verification_code")
    public ResponseEntity<Object> sendVerificationCode(@RequestParam("phoneNumber") String phoneNumber) {

        ApiError apiError = null;
        String nigeriaPrefix = "234";

        if (TextUtils.isBlank(phoneNumber)) {
            apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, messageUtil.getMessage("phone.number.required", "en"),
                    false, new ArrayList<>(), null);
            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        try {
            logger.info(phoneNumber.trim().toLowerCase());
            PortalUser portalUser = this.portalUserDao.findByPhoneNumber(phoneNumber.trim().toLowerCase());

            if (portalUser != null) {

                if (portalUser.isPhoneNumberVerified()) {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("phone.number.already.verified", "en"),
                            false, new ArrayList<>(), null);
                    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                }

                boolean status = this.limitPhoneNumberVerificationSmsService.saveOrUpdate(portalUser);
                if(!status) {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("sms.verification.count.exceeded", "en"),
                            false, new ArrayList<>(), null);
                    return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
                }

                logger.info("found user");

                logger.info(portalUser.getEmail());

            } else {
                logger.info("user not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            PhoneNumberVerificationDto phoneNumberVerificationDto = new PhoneNumberVerificationDto();
            phoneNumberVerificationDto.setPhoneNumber(phoneNumber);
            PhoneNumberVerificationCodes phoneNumberVerificationCodes = phoneNumberVerificationCodeService.save(phoneNumberVerificationDto);

            SendSmsPojo sendSmsPojo = new SendSmsPojo();
            sendSmsPojo.setToNumber(phoneNumber);


            if (phoneNumber.contains(nigeriaPrefix)) {

                logger.info(phoneNumberVerificationCodes.getVerificationCode());
                sendSmsPojo.setMessageBody("Merlin Labs verification code is " + phoneNumberVerificationCodes.getVerificationCode());
                sendSmsPojo.setFromNumber("Merlin Labs");
                this.multiTexterService.sendActualSms(sendSmsPojo);
                List<AdminSettings> adminSettingsList = this.adminSettingsDao.findAll();
                AdminSettings adminSetting = adminSettingsList.stream().findFirst().orElse(null);

                if(adminSetting != null) {

                    try {


                        if(adminSetting.getMultiTexterNumberOfUnits() <= Long.valueOf(ifRemainingUnitsIsBelow)) {
                            logger.info("units are very low");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    int unitsPerText = adminSetting.getMultiTexterUnitsPerText();
                    adminSetting.setMultiTexterNumberOfUnits(adminSetting.getMultiTexterNumberOfUnits() - unitsPerText);
                    adminSettingsDao.save(adminSetting);
                }



            } else {
//                sendSmsPojo.setMessageBody("<#> Parcel Dove is " + phoneNumberVerificationCodes.getVerificationCode());
//                sendSmsPojo.setFromNumber("+18636245851");
//                this.twilioSmsService.sendActualSms(sendSmsPojo);
            }

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("sms.sent", "en"),
                    true, new ArrayList<>(), null);

        } catch (Exception e) {

            apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, messageUtil.getMessage("sms.sending.failed", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

}
