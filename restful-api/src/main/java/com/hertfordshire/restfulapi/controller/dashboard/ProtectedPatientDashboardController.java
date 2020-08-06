package com.hertfordshire.restfulapi.controller.dashboard;


import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.pojo.PatientDashboardInfoPojo;
import com.hertfordshire.pubsub.redis.model.PortalUserModel;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.test_order.TestOrderService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import com.hertfordshire.utils.errors.MyApiResponse;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@RestController
public class ProtectedPatientDashboardController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedDashboardController.class.getSimpleName());

    @Autowired
    private TestOrderService testOrderService;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private com.hertfordshire.pubsub.redis.service.portal_user.PortalUserService redisPortalUserService;

    @Autowired
    private UserService userService;

    @Autowired
    MyApiResponse myApiResponse;


    @GetMapping("/default/dashboard/patient")
    public ResponseEntity<Object> patientDashboardInfo(HttpServletResponse response,
                                                       HttpServletRequest request,
                                                       Authentication authentication) {
        ApiError apiError = null;
        UserDetailsDto requestPrincipal = null;
        String lang = "en";
        Long portalUserId = 0L;

        try {

            try {
                String formOfIdentification = userService.fetchFormOfIdentification();
                if (!TextUtils.isBlank(formOfIdentification)) {
                    PortalUserModel portalUserModel = this.redisPortalUserService.fetchPortalUser(formOfIdentification);
                    if(portalUserModel != null) {
                        portalUserId = portalUserModel.getId();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                if (portalUserId == 0L) {
                    requestPrincipal = userService.getPrincipal(response, request, authentication);
                    portalUserId = requestPrincipal.getId();
                }

            } catch (NullPointerException e) {
                e.printStackTrace();

                return new MyApiResponse().unAuthorizedResponse();
            }


            PortalUser portalUser = this.portalUserService.findPortalUserById(portalUserId);

            Number numberOfOrders = this.testOrderService.countNumberOfOrdersForLoggedInUser(portalUser);

            PatientDashboardInfoPojo patientDashboardInfoPojo = new PatientDashboardInfoPojo();
            patientDashboardInfoPojo.setNumberOfTestsOrdered(numberOfOrders);

            if(portalUser != null){
                if(!TextUtils.isBlank(portalUser.getEmail())) {
                    patientDashboardInfoPojo.setDoesUserHaveEmail(true);
                } else {
                    patientDashboardInfoPojo.setDoesUserHaveEmail(false);
                }
            }

            return myApiResponse.successfullyCreated(patientDashboardInfoPojo, "user.creation.for.employee.successful");

        } catch (Exception e) {
            e.printStackTrace();
            return myApiResponse.internalServerErrorResponse();
        }
    }
}
