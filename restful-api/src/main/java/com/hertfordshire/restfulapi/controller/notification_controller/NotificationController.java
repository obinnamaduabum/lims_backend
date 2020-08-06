package com.hertfordshire.restfulapi.controller.notification_controller;

import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.pubsub.redis.model.PortalUserModel;
import com.hertfordshire.service.mongodb.NotificationCountMongoDbService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@RestController
public class NotificationController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class.getSimpleName());

    @Autowired
    private com.hertfordshire.pubsub.redis.service.portal_user.PortalUserService redisPortalUserService;

    @Autowired
    private UserService userService;


    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private NotificationCountMongoDbService notificationCountMongoDbService;

//    @Autowired
//    private NotificationMongodbService notificationMongodbService;

    @GetMapping("/default/notifications/view-all")
    public ResponseEntity<Object> findAllCategoriesAndTests(HttpServletResponse response,
                                                            HttpServletRequest request,
                                                            Authentication authentication,
                                                            @RequestParam("page") int page,
                                                            @RequestParam("size") int size) {

        ApiError apiError = null;
        PortalUser portalUser = null;
        PortalUserModel portalUserModel = null;
        Long portalUserId = 0L;
        UserDetailsDto requestPrincipal = null;

        try {

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


            portalUser = this.portalUserService.findPortalUserById(portalUserId);

            PaginationResponsePojo paginationResponsePojo  = null;
                    //= this.notificationMongodbService.findAllNotificationsByPortalUserOrderByDateCreatedWithPaginationResponsePojo(portalUser, page, size);

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("patient.creation.successful", "en"),
                    true, new ArrayList<>(), paginationResponsePojo);

        } catch (Exception e) {
            e.printStackTrace();

            apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, messageUtil.getMessage("patient.creation.not.successful", "en"),
                    false, new ArrayList<>(), null);
        }
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

}
