package com.hertfordshire.restfulapi.controller;

import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.utils.errors.CustomBadRequestException;
import com.hertfordshire.dto.ContactUsDto;
import com.hertfordshire.mailsender.pojo.MailPojo;
import com.hertfordshire.mailsender.service.EmailService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.PublicBaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PublicContactUsController extends PublicBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(PublicContactUsController.class);

    @Autowired
    private MessageUtil messageUtil;

    @Value("${no.reply.email}")
    private String noReplyEmail;

    @Value("${contact-us.email}")
    private String contactUsEmail;

    @Value("${default.domainUrlOne}")
    private String domainUrlOne;

    @Autowired
    private EmailService emailService;

    @PostMapping("/default/contact-us/send")
    public ResponseEntity<?> send(@Valid @RequestBody ContactUsDto contactUsDto,
                                  HttpServletResponse res,
                                  HttpServletRequest request,
                                  Authentication authentication,
                                  BindingResult bindingResult) {

        ApiError apiError = null;

        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                // logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {

            try {

                MailPojo mailPojo = new MailPojo();
                mailPojo.setFrom(noReplyEmail);
                mailPojo.setTo(contactUsEmail);
                mailPojo.setSubject("Contact Us");
                mailPojo.setTemplateName("contact-us.ftl");

                Map model = new HashMap();
                model.put("firstName", contactUsDto.getFirstName());
                model.put("lastName", contactUsDto.getLastName());
                model.put("email", contactUsDto.getEmail());
                model.put("message", contactUsDto.getMessage());
                model.put("phoneNumber", contactUsDto.getPhoneNumber());
                model.put("domainName", domainUrlOne);
                mailPojo.setModel(model);
                emailService.sendActualEmail(mailPojo);


                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("message.sent", "en"),
                        true, new ArrayList<>(), null);

            } catch (Exception e) {
                e.printStackTrace();

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("message.sending.failed", "en"),
                        false, new ArrayList<>(), null);

            }

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }
}
