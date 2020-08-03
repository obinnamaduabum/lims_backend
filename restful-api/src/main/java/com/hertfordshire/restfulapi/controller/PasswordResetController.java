package com.hertfordshire.restfulapi.controller;


import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.access.errors.CustomBadRequestException;
import com.hertfordshire.dao.psql.PasswordResetTokenDao;
import com.hertfordshire.dao.psql.PortalAccountAndPortalUserRoleMapperDao;
import com.hertfordshire.dao.psql.RolesDao;
import com.hertfordshire.dto.PasswordRestDto;
import com.hertfordshire.mailsender.pojo.MailPojo;
import com.hertfordshire.mailsender.service.EmailService;
import com.hertfordshire.model.psql.PasswordResetToken;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.service.psql.password_reset.PasswordResetService;
import com.hertfordshire.service.psql.password_reset.PasswordRestTokenServiceImpl;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.portaluser.PortalUserServiceImp;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.PublicBaseApiController;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class PasswordResetController extends PublicBaseApiController {


    private PortalUserService portalUserService;

    private PortalUserDao portalUserDao;

    private PasswordResetService passwordResetService;

    @Autowired
    private UserService userService;


    private EmailService emailService;

    @Value("${no.reply.email}")
    private String noReplyEmail;

    @Value("${default.domainUrlOne}")
    private String domainUrlOne;

    @Value("${default.domainUrlTwo}")
    private String domainUrlTwo;

    @Value("${passwordValidForInMinutes}")
    private int passwordValidForInMinutes;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MessageSource messageSource;

    private MessageUtil messageUtil;


    @Autowired
    public PasswordResetController(PortalUserDao portalUserDao,
                                   RolesDao rolesDao,
                                   PortalAccountAndPortalUserRoleMapperDao portalAccountAndPortalUserRoleMapperDao,
                                   PasswordResetTokenDao passwordResetTokenDao) {

            passwordEncoder = new BCryptPasswordEncoder(12);
            this.portalUserService = new PortalUserServiceImp(portalUserDao, passwordEncoder, rolesDao, portalAccountAndPortalUserRoleMapperDao);
            this.messageUtil = new MessageUtil(messageSource);
            this.portalUserDao = portalUserDao;
            this.passwordResetService = new PasswordRestTokenServiceImpl(passwordResetTokenDao);
            this.emailService = new EmailService();
    }


    @GetMapping("/auth/password-rest/create")
    public ResponseEntity<?> create(@RequestParam("email") String userEmail) {

        ApiError apiError = null;
        PasswordResetToken passwordResetToken = null;

        if (TextUtils.isBlank(userEmail)) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.email.does.not.exists", "en"),
                    false, new ArrayList<>(), null);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        PortalUser portalUser = portalUserService.findByEmail(userEmail);

        if (portalUser == null) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.not.associated.to.email", "en"),
                    false, new ArrayList<>(), null);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        try {
            passwordResetToken = passwordResetService.create(portalUser);
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("password.reset.email.sent", "en"),
                    true, new ArrayList<>(), passwordResetToken);
        } catch (Exception e) {

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("password.reset.token.creation.failed", "en"),
                    false, new ArrayList<>(), null);
        }


        MailPojo mailPojo = new MailPojo();
        mailPojo.setFrom(this.noReplyEmail);
        mailPojo.setTo(portalUser.getEmail());
        mailPojo.setSubject("Password Reset");
        mailPojo.setTemplateName("password-reset.ftl");

        try {

            assert passwordResetToken != null;
            Map model = new HashMap();
            model.put("firstName", portalUser.getFirstName());
            model.put("lastName", portalUser.getLastName());
            model.put("otherName", portalUser.getOtherName());
            model.put("passwordRestToken", passwordResetToken.getToken());
            model.put("userId", portalUser.getId());
            model.put("domainUrlOne", domainUrlOne);
            model.put("domainUrlTwo", domainUrlTwo);
            model.put("http", "http");
            model.put("live_duration", passwordValidForInMinutes);
            mailPojo.setModel(model);
            emailService.sendActualEmail(mailPojo);
        } catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

    }


    @GetMapping("/auth/password-rest/verify")
    public ResponseEntity<?> verifyToken(@RequestParam("token") String token, @RequestParam("userCode") Long userCode, HttpServletResponse res, HttpServletRequest request) {

        ApiError apiError = null;

        if (StringUtils.isBlank(token)) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("password.reset.token.not.found", "en"),
                    false, new ArrayList<>(), null);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        boolean isValid = passwordResetService.validatePasswordResetToken(token, userCode);

        try {

            if (isValid) {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("password.reset.token.is.valid", "en"),
                        true, new ArrayList<>(), null);
            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("password.reset.token.is.expired", "en"),
                        false, new ArrayList<>(), null);
            }

        } catch (Exception e) {

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("password.reset.token.is.expired", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @PostMapping("/auth/password-rest/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordRestDto passwordRestDto,
                                            BindingResult bindingResult) {
        ApiError apiError = null;

        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                //logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();
        }

        Optional<PortalUser> optionalPortalUser = portalUserDao.findById(passwordRestDto.getUserId());



        if (optionalPortalUser.isPresent()) {

            PortalUser portalUser = optionalPortalUser.get();

            portalUser.setPassword(passwordEncoder.encode(passwordRestDto.getPassword()));

            portalUserDao.save(portalUser);

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("password.reset.successful", "en"),
                    true, new ArrayList<>(), null);
        } else {

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("password.reset.failed", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @GetMapping("/auth/password-rest/verify-old-password")
    public ResponseEntity<?> verifyOldPassword(@RequestParam("password") String oldPassword,
                                               HttpServletResponse res, HttpServletRequest request,
                                               Authentication authentication) {

        ApiError apiError = null;

        UserDetailsDto requestPrincipal = null;

        requestPrincipal = userService.getPrincipal(res, request, authentication);

        if (StringUtils.isBlank(oldPassword)) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("password.reset.token.not.found", "en"),
                    false, new ArrayList<>(), null);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        if (oldPassword.equalsIgnoreCase(requestPrincipal.getPassword())) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("password.reset.token.is.valid", "en"),
                    true, new ArrayList<>(), null);
        } else {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("password.reset.token.is.expired", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
