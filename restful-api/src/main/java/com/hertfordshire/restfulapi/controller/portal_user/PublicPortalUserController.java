package com.hertfordshire.restfulapi.controller.portal_user;

import com.google.gson.Gson;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.access.errors.CustomBadRequestException;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.dto.*;
import com.hertfordshire.mailsender.pojo.MailPojo;
import com.hertfordshire.mailsender.service.EmailService;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.service.psql.email_verification.EmailVerificationService;
import com.hertfordshire.service.psql.phone_number_verification_code.PhoneNumberVerificationCodeService;
import com.hertfordshire.service.psql.portalaccount.PortalAccountService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.role.RolesService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.PhoneNumberValidationUtil;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.controllers.PublicBaseApiController;
import com.hertfordshire.utils.lhenum.TypeOfPhoneNumberVerification;
import com.hertfordshire.utils.pojo.PhoneCodesPojo;
import com.hertfordshire.utils.pojo.ProperPhoneNumberPojo;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PublicPortalUserController extends PublicBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(PublicPortalUserController.class.getSimpleName());

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private MessageUtil messageUtil;

    private Gson gson;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private EmailService emailService;

    @Value("${no.reply.email}")
    private String noReplyEmail;

    @Value("${default.domainUrlOne}")
    private String domainUrlOne;

    @Value("${default.domainUrlTwo}")
    private String domainUrlTwo;

    @Value("${phoneNumberValidForInMinutes}")
    private int phoneNumberValidForInMinutes;

    @Value("${emailValidForInMinutes}")
    private int emailValidForInMinutes;

    @Value("${passwordValidForInMinutes}")
    private int passwordValidForInMinutes;

    @Autowired
    private PhoneNumberVerificationCodeService phoneNumberVerificationCodeService;


    public PublicPortalUserController() {
        this.gson = new Gson();
    }

    // Check if already exists
    @GetMapping("/auth/portal-user/email/exists")
    public ResponseEntity<Object> findPortalUserByEmail(@RequestParam("email") String email,
                                                        HttpServletRequest request) {

        PortalUser portalUser = null;
        ApiError apiError = null;

        if (StringUtils.isNotBlank(email)) {

            logger.info(email);

            portalUser = portalUserService.findByEmail(email.toLowerCase().trim());

            if (portalUser != null) {

                System.out.println("two factor: "+ portalUser.isTwoFactor());
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.email.exists", "en"),
                        true, new ArrayList<>(), portalUser);
            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.email.does.not.exists", "en"),
                        false, new ArrayList<>(), null);
            }

        } else {
            // throw new CustomBadRequestException();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.email.does.not.exists", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @PostMapping("/auth/portal-user/two-factor/verify")
    public ResponseEntity<Object> verifyTwoFactor(@Valid @RequestBody TwoFactorVerificationDto twoFactorVerificationDto,
                                                        HttpServletRequest request) {

        PortalUser portalUser = null;
        ApiError apiError = null;

        if (StringUtils.isNotBlank(twoFactorVerificationDto.getEmail())) {

            logger.info(twoFactorVerificationDto.getEmail());

            portalUser = portalUserService.findByEmail(twoFactorVerificationDto.getEmail().toLowerCase().trim());

            if (portalUser != null) {

                if(portalUser.getSecret() != null) {

                    boolean successful = new Utils().verify2Fa(portalUser.getSecret(), twoFactorVerificationDto.getTwoFactorCode());
                    if(successful) {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("two.factor.code.verified", "en"),
                                true, new ArrayList<>(), portalUser);
                    } else {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("two.factor.code.not-valid.verified", "en"),
                                false, new ArrayList<>(), portalUser);
                    }

                } else {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.secret.does.not.exists", "en"),
                            false, new ArrayList<>(), portalUser);
                }

            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.email.does.not.exists", "en"),
                        false, new ArrayList<>(), null);
            }

        } else {
            // throw new CustomBadRequestException();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.email.does.not.exists", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @PostMapping("/auth/portal-user/phone_number/exists")
    public ResponseEntity<Object> findPortalUserByPhoneNumber(@Valid @RequestBody PhoneNumberExistsDto phoneNumberExistsDto,
                                                              HttpServletRequest request,
                                                              BindingResult bindingResult) {

        ApiError apiError = null;
        PortalUser portalUser = null;
        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {

            if (StringUtils.isNotBlank(phoneNumberExistsDto.getPhoneNumber())) {


                PhoneCodesPojo phoneCodesPojo = new PhoneCodesPojo();
                phoneCodesPojo.setAlpha2(phoneNumberExistsDto.getAlpha2());
                phoneCodesPojo.setAlpha3(phoneNumberExistsDto.getAlpha3());
                phoneCodesPojo.setInternationalPhoneNumber(phoneNumberExistsDto.getInternationalPhoneNumber());
                phoneCodesPojo.setName(phoneNumberExistsDto.getName());


                try {

                    ProperPhoneNumberPojo properPhoneNumberPojo = PhoneNumberValidationUtil.validatePhoneNumber(phoneNumberExistsDto.getPhoneNumber(), phoneCodesPojo);

                    if(properPhoneNumberPojo != null) {


                        portalUser = portalUserService.findByPhoneNumber(properPhoneNumberPojo.getFormattedNumber().trim());
                        if(portalUser == null) {
                            portalUser = portalUserService.findByPhoneNumber(properPhoneNumberPojo.getFormattedNumber().trim().replace("+", ""));
                        }

                        if (portalUser != null) {

                            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.phone.number.already.exists", "en"),
                                    true, new ArrayList<>(), properPhoneNumberPojo);
                        } else {
                            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.phone.number.does.not.exists", "en"),
                                    false, new ArrayList<>(), null);
                        }

                        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());

                    }

                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.phone.number.does.not.exists", "en"),
                            false, new ArrayList<>(), null);

                    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());


                } catch (Exception e){
                    e.printStackTrace();

                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.phone.number.does.not.exists", "en"),
                            false, new ArrayList<>(), null);

                    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                }

            }

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.phone.number.does.not.exists", "en"),
                    false, new ArrayList<>(), null);

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }
    // Check if already exists

    @PostMapping("/auth/portal-user/phone_number/exists/{code}")
    public ResponseEntity<Object> findForEmployeeByPhoneNumber(@Valid @RequestBody PhoneNumberExistsDto phoneNumberExistsDto,
                                                               @PathVariable("code") String portalUserCode,
                                                               HttpServletRequest request,
                                                               BindingResult bindingResult) {

        ApiError apiError = null;
        PortalUser portalUser = null;
        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {

            if (StringUtils.isNotBlank(phoneNumberExistsDto.getPhoneNumber())) {

                PortalUser foundPortalUser = this.portalUserService.findByPortalUserCode(portalUserCode);
                portalUser = this.portalUserService.findByPhoneNumber(phoneNumberExistsDto.getPhoneNumber());

                if (portalUser != null) {

                    if (foundPortalUser.getEmail().equalsIgnoreCase(portalUser.getEmail())) {

                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.phone.number.does.not.exists", "en"),
                                false, new ArrayList<>(), null);

                    } else {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.phone.number.already.exists", "en"),
                                true, new ArrayList<>(), portalUser);
                    }
                } else {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.phone.number.does.not.exists", "en"),
                            false, new ArrayList<>(), null);
                }

            }

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }

    @PostMapping("/auth/portal-user/email/verification_code")
    public ResponseEntity<Object> sendVerificationCodeViaEmail(@Valid @RequestBody SendVerificationCodeToEmailDto sendVerificationCodeToEmailDto,
                                                               HttpServletRequest request,
                                                               BindingResult bindingResult) {

        ApiError apiError = null;

        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {

            if (StringUtils.isNotBlank(sendVerificationCodeToEmailDto.getEmail())) {

                PortalUser portalUser = portalUserService.findByEmail(sendVerificationCodeToEmailDto.getEmail());

                if (portalUser == null) {

                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("verification.code.sent.via.email", "en"),
                            true, new ArrayList<>(), null);

                    PhoneNumberVerificationDto phoneNumberVerificationDto = new PhoneNumberVerificationDto();
                    phoneNumberVerificationDto.setPhoneNumber(sendVerificationCodeToEmailDto.getEmail());
                    phoneNumberVerificationDto.setType(TypeOfPhoneNumberVerification.EMAIL);
                    PhoneNumberVerificationCodes phoneNumberVerificationCodes = phoneNumberVerificationCodeService.save(phoneNumberVerificationDto);


                    MailPojo mailPojo = new MailPojo();
                    mailPojo.setFrom(this.noReplyEmail);
                    mailPojo.setTo(sendVerificationCodeToEmailDto.getEmail());
                    mailPojo.setSubject("Verification Code");
                    mailPojo.setTemplateName("send-verification-code-via-email.ftl");

                    Map model = new HashMap();
                    model.put("code", phoneNumberVerificationCodes.getVerificationCode());
                    model.put("live_duration", phoneNumberValidForInMinutes);
                    mailPojo.setModel(model);
                    emailService.sendActualEmail(mailPojo);


                } else {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("email.already.registered", "en"),
                            false, new ArrayList<>(), null);
                }

            }

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }

    @GetMapping("/auth/portal-user/verify-email")
    public ResponseEntity<?> verifyToken(@RequestParam("token") String token, @RequestParam("userCode") Long userCode, HttpServletResponse res, HttpServletRequest request) {

        ApiError apiError = null;

        if (StringUtils.isBlank(token)) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("email.verification.token", "en"),
                    false, new ArrayList<>(), null);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        boolean isValid = emailVerificationService.validatePasswordResetToken(token, userCode);

        try {

            if (isValid) {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("email.verification.successful", "en"),
                        true, new ArrayList<>(), null);
            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("email.verification.token.expired", "en"),
                        false, new ArrayList<>(), null);
            }

        } catch (Exception e) {

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("email.verification.failed", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @GetMapping("/auth/portal-user/resend-email-verification")
    public ResponseEntity<?> resendEmailVerification(@RequestParam("email") String email, HttpServletResponse res, HttpServletRequest request) {

        ApiError apiError = null;

        if (TextUtils.isBlank(email)) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.email.does.not.exists", "en"),
                    false, new ArrayList<>(), null);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }


        PortalUser portalUser = this.portalUserService.findPortalUserByEmail(email.toLowerCase());
        if (portalUser == null) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("email.address.not.found", "en"),
                    false, new ArrayList<>(), null);
            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        try {

            if (portalUser.isEmailVerified()) {

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("email.verification.email.already.verified", "en"),
                        false, new ArrayList<>(), null);

            } else {

                EmailVerificationToken emailVerificationToken = emailVerificationService.generateToken(portalUser);
                MailPojo mailPojo = new MailPojo();
                mailPojo.setFrom(this.noReplyEmail);
                mailPojo.setTo(portalUser.getEmail());
                mailPojo.setSubject("Email verification");
                mailPojo.setTemplateName("email-verification-patient.ftl");

                Map model = new HashMap();
                model.put("firstName", portalUser.getFirstName());
                model.put("lastName", portalUser.getLastName());
                model.put("token", emailVerificationToken.getToken());
                model.put("domainUrlOne", domainUrlOne);
                model.put("domainUrlTwo", domainUrlTwo);
                model.put("userCode", portalUser.getId());
                model.put("live_duration", this.emailValidForInMinutes);
                mailPojo.setModel(model);
                try {
                    emailService.sendActualEmail(mailPojo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("email.verification.email.sent", "en"),
                        true, new ArrayList<>(), null);
            }

        } catch (Exception e) {

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("email.verification.email.failed.not.send", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }


}