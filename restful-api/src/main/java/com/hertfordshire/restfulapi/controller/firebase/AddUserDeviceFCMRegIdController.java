package com.hertfordshire.restfulapi.controller.firebase;

import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.model.psql.KafkaSubscription;
import com.hertfordshire.model.psql.KafkaTopicModel;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.model.psql.firebase.FireBaseDeviceGroup;
import com.hertfordshire.model.psql.firebase.FireBaseDevices;
import com.hertfordshire.pubsub.redis.model.PortalUserModel;
import com.hertfordshire.pubsub.redis.service.portal_user.PortalUserService;
import com.hertfordshire.service.firebase.devices.FirebaseDeviceService;
import com.hertfordshire.service.psql.kafka.subscription.KafkaSubscriptionService;
import com.hertfordshire.service.psql.kafka.topic.KafkaTopicService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AddUserDeviceFCMRegIdController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(AddUserDeviceFCMRegIdController.class.getSimpleName());

    @Autowired
    private FirebaseDeviceService firebaseDeviceService;

    @Autowired
    private PortalUserService redisPortalUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageUtil messageUtil;

    @Value("${fire.base.cookieName}")
    private String fireBaseCookieName;


    @Value("${fire.base.device.token}")
    private String fireBaseDeviceToken;

    @Autowired
    private KafkaSubscriptionService kafkaSubscriptionService;

    @Autowired
    private KafkaTopicService kafkaTopicService;


    @Autowired
    private com.hertfordshire.service.psql.portaluser.PortalUserService portalUserService;


    @GetMapping(value = "/default/user-device-fcm-reg-id/add/{token}")
    public ResponseEntity<Object> firebaseGroupThisUsersDevices(@PathVariable("token") String token,
                                                                HttpServletResponse response,
                                                                HttpServletRequest request,
                                                                Authentication authentication) {
        ApiError apiError = null;
        UserDetailsDto requestPrincipal = null;
        PortalUserModel portalUserModel = null;
        Long portalUserId = null;
        FireBaseDeviceGroup fireBaseDeviceGroup = null;
        List<String> errors = new ArrayList<>();

        try {

            if (token != null) {
                if (TextUtils.isBlank(token)) {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("cookie.required.firebase", "en"),
                            false, new ArrayList<>(), null);
                    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                }
            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("cookie.required.firebase", "en"),
                        false, new ArrayList<>(), null);
                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String formOfIdentification = userService.fetchFormOfIdentification();
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
        }

        try {

            PortalUser portalUser = this.portalUserService.findPortalUserById(portalUserId);

            FireBaseDevices fireBaseDevices = this.firebaseDeviceService.findDeviceByRegistrationId(token, portalUser);

            if(fireBaseDevices == null) {
                fireBaseDevices = this.firebaseDeviceService.saveDeviceRegistrationId(token, portalUser);
            }

            fireBaseDeviceGroup = this.firebaseDeviceService.findFireBaseDeviceGroupByPortalUser(portalUser);

            List<String> regIds = new ArrayList<>();
            regIds.add(token);

            if(fireBaseDeviceGroup != null) {

                if (!fireBaseDevices.isSubscribed()) {
                    this.firebaseDeviceService.updateDeviceGroup(portalUser, regIds, fireBaseDeviceGroup);
                }
            } else {
                this.firebaseDeviceService.createDeviceGroup(portalUser, regIds);
            }


            List<KafkaSubscription> kafkaSubscriptions = this.kafkaSubscriptionService.findAll(portalUser);

            for(KafkaSubscription kafkaSubscription: kafkaSubscriptions) {
                KafkaTopicModel kafkaTopicModel = this.kafkaTopicService.findById(kafkaSubscription.getKafkaTopicModel().getId());
                boolean wasSubscribed = this.firebaseDeviceService.subscribeOrUnsubscribeTopic(kafkaTopicModel.getName(), regIds, true);
                if(!wasSubscribed) {
                    errors.add("errors");
                }
            }



            if(errors.size() <= 0) {

                Cookie cookie = new Cookie(this.fireBaseDeviceToken,token);
                cookie.setMaxAge(60 * 60 * 24 * 365);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                response.addCookie(cookie);

                this.firebaseDeviceService.updateFireBaseDevice(portalUser, fireBaseDevices, true);
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("fcm.reg.id.add", "en"),
                        true, new ArrayList<>(), fireBaseDeviceGroup);
            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("fcm.reg.id.add.failed", "en"),
                        false, new ArrayList<>(), null);
            }


        } catch (Exception e) {
            e.printStackTrace();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("fcm.reg.id.add.failed", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());

    }
}
