package com.hertfordshire.restfulapi.controller.authentication;


import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.utils.errors.CustomBadRequestException;
import com.hertfordshire.dto.LoginDto;
import com.hertfordshire.mailsender.service.EmailService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.PublicBaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
public class PublicAuthenticationController extends PublicBaseApiController {


    private static final Logger logger = LoggerFactory.getLogger(PublicAuthenticationController.class.getSimpleName());

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public PublicAuthenticationController(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

//    @GetMapping("/test/email")
//    public String testEmail(Authentication authentication){
//
////        logger.info(gson.toJson(authentication.getDetails()));
////
////        logger.info(gson.toJson(authentication.getPrincipal()));
////
////        logger.info(gson.toJson(authentication.getPrincipal()));
//
//        MailPojo mailPojo = new MailPojo();
//        mailPojo.setFrom("obinnamaduabum@backend.com");
//        mailPojo.setTo("robinhojohn07@gmail.com");
//        mailPojo.setSubject("User Registration");
//        mailPojo.setTemplateName("email-verification.ftl");
//
//
//        String hostName = "";
//        hostName = "localhost:4200";
//
//        Map model = new HashMap();
//        model.put("firstName", "ghvhgvgh");
//        model.put("lastName", "ghvhgvgvghvhgvhghvhg");
//        model.put("token", "hbhgvhvgvhg");
//        model.put("host", "ghvghvgvgvhgvgh");
//        model.put("hostName", hostName);
//        model.put("http", "http");
//        mailPojo.setModel(model);
//        emailService.sendActualEmail(mailPojo);
//
//        return "dfdf";
//    }


    @PostMapping("/auth/partial-login")
    public ResponseEntity<?> partialUserAuth(@Valid @RequestBody LoginDto loginDto, BindingResult bindingResult) {

        ApiError apiError = null;

        try {

            if (bindingResult.hasErrors()) {

                bindingResult.getAllErrors().forEach(objectError -> {
                    logger.info(objectError.toString());
                });

                throw new CustomBadRequestException();

            } else {


                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginDto.getEmail().trim(),
                                loginDto.getPassword().trim(),
                                new ArrayList<>()
                        ));

                if (authentication.isAuthenticated()) {

                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.login.successful", "en"),
                            true, new ArrayList<>(), null);
                } else {
                    apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("user.login.failed", "en"),
                            false, new ArrayList<>(), null);
                }

                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
            }

        } catch (Exception e) {

            e.printStackTrace();

            logger.info(e.getMessage());

            apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, messageUtil.getMessage("server.error", "en"),
                    false, new ArrayList<>(), null);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }
}
