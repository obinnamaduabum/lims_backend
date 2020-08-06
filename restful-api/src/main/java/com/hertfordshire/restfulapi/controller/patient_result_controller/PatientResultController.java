package com.hertfordshire.restfulapi.controller.patient_result_controller;

import com.google.gson.Gson;
import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.utils.errors.CustomBadRequestException;
import com.hertfordshire.dto.PatientRestDto;
import com.hertfordshire.model.mongodb.LabTestResultMongoDb;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.pubsub.redis.model.PortalUserModel;
import com.hertfordshire.service.mongodb.LabTestResultMongoDbService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
public class PatientResultController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(PatientResultController.class.getSimpleName());

    @Autowired
    private com.hertfordshire.pubsub.redis.service.portal_user.PortalUserService redisPortalUserService;

    @Autowired
    private UserService userService;

    private Gson gson;

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private LabTestResultMongoDbService labTestResultMongoDbService;

    @Autowired
    private MessageUtil messageUtil;


    @PostMapping("/default/patient-result/create")
    public ResponseEntity<Object> create(@Valid @RequestBody PatientRestDto patientRestDto,
                                         HttpServletResponse response,
                                         HttpServletRequest request,
                                         Authentication authentication,
                                         BindingResult bindingResult) {

        ApiError apiError = null;
        PortalUser portalUser = null;
        PortalUserModel portalUserModel = null;
        Long portalUserId = 0L;
        UserDetailsDto requestPrincipal = null;

        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {



            try {
                String formOfIdentification = this.userService.fetchFormOfIdentification();
                if (!TextUtils.isBlank(formOfIdentification)) {
                    portalUserModel = this.redisPortalUserService.fetchPortalUser(formOfIdentification);

                    if (portalUserModel != null) {
                        portalUserId = portalUserModel.getId();
                    } else {

                        try {
                            requestPrincipal = userService.getPrincipal(response, request, authentication);
                            portalUserId = requestPrincipal.getId();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("user.unauthorized", "en"),
                                    false, new ArrayList<>(), null);
                            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }


            try {

                portalUser = this.portalUserService.findPortalUserById(portalUserId);

                LabTestResultMongoDb labTestResultMongoDb = this.labTestResultMongoDbService.save(patientRestDto, portalUser);

                if(labTestResultMongoDb != null) {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("patient.labtest.result", "en"),
                            true, new ArrayList<>(), null);
                } else {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("patient.labtest.result.failed", "en"),
                            false, new ArrayList<>(), null);
                }

            } catch (Exception e) {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("server.error", "en"),
                        false, new ArrayList<>(), null);
            }

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());

        }
    }
}
