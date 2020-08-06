package com.hertfordshire.restfulapi.controller.pub_sub;

import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.utils.errors.CustomBadRequestException;
import com.hertfordshire.dto.NotificationSetAsReadDto;
import com.hertfordshire.model.mongodb.NotificationCountMongoDb;
import com.hertfordshire.model.mongodb.NotificationMongoDb;
import com.hertfordshire.model.psql.KafkaSubscription;
import com.hertfordshire.model.psql.KafkaTopicModel;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.model.psql.firebase.FireBaseDeviceGroup;
import com.hertfordshire.model.psql.firebase.FireBaseDevices;
import com.hertfordshire.pojo.AngularNotificationPojo;
import com.hertfordshire.pojo.KafkaSubscriptionPojo;
import com.hertfordshire.pojo.KafkaTopicPojo;
import com.hertfordshire.pubsub.kafka.service.Utils;
import com.hertfordshire.pubsub.kafka.service.consumer.Consumer;
import com.hertfordshire.pubsub.kafka.service.producer.Producer;
import com.hertfordshire.pubsub.pojo.NotificationPojo;
import com.hertfordshire.pubsub.redis.model.PortalUserModel;
import com.hertfordshire.service.firebase.devices.FirebaseDeviceService;
import com.hertfordshire.service.mongodb.NotificationCountMongoDbService;
import com.hertfordshire.service.mongodb.NotificationMongodbService;
import com.hertfordshire.service.psql.kafka.subscription.KafkaSubscriptionService;
import com.hertfordshire.service.psql.kafka.topic.KafkaTopicService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
public class KafkaController extends ProtectedBaseApiController {

    private Logger logger = LoggerFactory.getLogger(KafkaController.class);

    @Autowired
    private Producer producer;

    @Autowired
    private Consumer consumer;

    @Autowired
    private com.hertfordshire.pubsub.redis.service.portal_user.PortalUserService redisPortalUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private Utils utils;

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private NotificationMongodbService notificationMongodbService;

    @Autowired
    private NotificationCountMongoDbService notificationCountMongoDbService;

    @Autowired
    private KafkaTopicService kafkaTopicService;

    @Autowired
    private KafkaSubscriptionService kafkaSubscriptionService;

    @Value("${fire.base.cookieName}")
    private String fireBaseCookieName;

    @Autowired
    private FirebaseDeviceService firebaseDeviceService;

    @GetMapping(value = "/default/shila/topic")
    public ResponseEntity<Object> allTopic(HttpServletResponse response,
                                           HttpServletRequest request,
                                           Authentication authentication) {
        ApiError apiError = null;
        try {
            List<KafkaTopicModel> kafkaTopicModels = this.kafkaTopicService.findAll();
            List<KafkaTopicPojo> kafkaTopicPojoList = new ArrayList<>();
            for (KafkaTopicModel kafkaTopicModel : kafkaTopicModels) {
                KafkaTopicPojo kafkaTopicPojo = new KafkaTopicPojo();
                kafkaTopicPojo.setDateCreated(kafkaTopicModel.getDateCreated());
                kafkaTopicPojo.setDateUpdated(kafkaTopicModel.getDateUpdated());
                kafkaTopicPojo.setName(kafkaTopicModel.getName());
                kafkaTopicPojo.setId(kafkaTopicModel.getId());
                kafkaTopicPojoList.add(kafkaTopicPojo);
            }

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.topic.deleted", "en"),
                    true, new ArrayList<>(), kafkaTopicPojoList);
        } catch (Exception e) {
            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.topic.deletion", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @GetMapping(value = "/default/shila/topic/create")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Object> createTopic(@RequestParam("topic-name") String topicName,
                                              HttpServletResponse response,
                                              HttpServletRequest request,
                                              Authentication authentication) {

        ApiError apiError = null;
        UserDetailsDto requestPrincipal = null;
        PortalUserModel portalUserModel = null;
        Long portalUserId = null;

        if (TextUtils.isBlank(topicName)) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.email.does.not.exists", "en"),
                    false, new ArrayList<>(), null);
            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
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
            this.kafkaTopicService.add(topicName, portalUser);
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.topic.create", "en"),
                    true, new ArrayList<>(), null);
        } catch (Exception e) {
            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.topic.creation.failed", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @GetMapping(value = "/default/shila/topic/user/{id}")
    public ResponseEntity<Object> portalUserTopics(@PathVariable("id") String id,
                                                   HttpServletResponse response,
                                                   HttpServletRequest request,
                                                   Authentication authentication) {

        ApiError apiError = null;

        if (TextUtils.isBlank(id)) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user-id.required", "en"),
                    false, new ArrayList<>(), null);
            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        try {

            PortalUser portalUser = this.portalUserService.findPortalUserById(Long.valueOf(id));
            List<KafkaSubscription> kafkaSubscriptions = this.kafkaSubscriptionService.findAll(portalUser);

            List<KafkaSubscriptionPojo> kafkaSubscriptionPojoList = new ArrayList<>();

            for (KafkaSubscription kafkaSubscription : kafkaSubscriptions) {

                KafkaSubscriptionPojo kafkaSubscriptionPojo = new KafkaSubscriptionPojo();
                kafkaSubscriptionPojo.setDateCreated(kafkaSubscription.getDateCreated());
                kafkaSubscriptionPojo.setDateUpdated(kafkaSubscription.getDateUpdated());
                kafkaSubscriptionPojo.setId(kafkaSubscription.getId());
                KafkaTopicModel kafkaTopicModel = this.kafkaTopicService.findById(kafkaSubscription.getKafkaTopicModel().getId());
                kafkaSubscriptionPojo.setTopicName(kafkaTopicModel.getName());
                kafkaSubscriptionPojo.setTopicId(kafkaTopicModel.getId());
                kafkaSubscriptionPojoList.add(kafkaSubscriptionPojo);
            }

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.topic.create", "en"),
                    true, new ArrayList<>(), kafkaSubscriptionPojoList);
        } catch (Exception e) {
            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.topic.creation.failed", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @GetMapping(value = "/default/shila/topic/delete/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Object> deleteTopic(@PathVariable("id") Long id,
                                              HttpServletResponse response,
                                              HttpServletRequest request,
                                              Authentication authentication) {
        ApiError apiError = null;

        try {
            KafkaTopicModel kafkaTopicModel = this.kafkaTopicService.findById(id);

            if (kafkaTopicModel != null) {
                this.kafkaTopicService.remove(kafkaTopicModel);
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.topic.deleted", "en"),
                        true, new ArrayList<>(), null);
            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.topic.deletion", "en"),
                        false, new ArrayList<>(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.topic.deletion", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @GetMapping(value = "/default/shila/consumer")
    public ResponseEntity<Object> fetchMessages(HttpServletResponse response,
                                                HttpServletRequest request,
                                                Authentication authentication) {

        ApiError apiError = null;
        UserDetailsDto requestPrincipal = null;
        PortalUserModel portalUserModel = null;
        Long portalUserId = null;

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


        PortalUser portalUser = this.portalUserService.findById(portalUserId);
        List<KafkaSubscription> kafkaSubscriptionList =
                this.kafkaSubscriptionService.findAll(portalUser);
        List<String> topics = new ArrayList<>();
        for (KafkaSubscription kafkaSubscription : kafkaSubscriptionList) {
            KafkaTopicModel kafkaTopicModel = this.kafkaTopicService.findById(kafkaSubscription.getKafkaTopicModel().getId());
            topics.add(kafkaTopicModel.getName());
        }

        try {

            List<NotificationMongoDb> notificationMongoDbList = new ArrayList<>();
            Long count = 0L;
            try {
                if (topics.size() > 0) {
                    List<NotificationPojo> notificationPojoList = this.consumer.fetchMessages("" + portalUser.getId(), topics);
                    this.notificationMongodbService.saveAll(notificationPojoList, portalUser);
                    this.notificationCountMongoDbService.incrementOrSave((long) notificationPojoList.size(), portalUser);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                notificationMongoDbList =
                        this.notificationMongodbService.findAllByPortalUserOrderByDateCreated(portalUser);
                NotificationCountMongoDb notificationCountMongoDb =
                        this.notificationCountMongoDbService.findByPortalUserId(portalUser.getId());
                count = notificationCountMongoDb.getCount();
            } catch (Exception e) {
                e.printStackTrace();
            }

            AngularNotificationPojo angularNotificationPojo = new AngularNotificationPojo();
            angularNotificationPojo.setDataList(notificationMongoDbList);
            angularNotificationPojo.setCount(count);


            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.notification.message", "en"),
                    true, new ArrayList<>(), angularNotificationPojo);

        } catch (Exception e) {
            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.notification.error.fetching.message", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @GetMapping(value = "/default/shila/subscribe/{name}")
    public ResponseEntity<Object> subscribe(@PathVariable("name") String name,
                                            @RequestParam("user-id") String userId,
                                            HttpServletRequest request) {

        ApiError apiError = null;
        KafkaSubscriptionPojo subscriptionPojo = null;
        FireBaseDeviceGroup fireBaseDeviceGroup = null;
        List<String> errors = new ArrayList<>();
        List<String> regIds = new ArrayList<>();

        try {

            if (TextUtils.isBlank(userId)) {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user-id.required", "en"),
                        false, new ArrayList<>(), null);
                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
            }

            PortalUser portalUser = this.portalUserService.findPortalUserById(Long.valueOf(userId));

            if (portalUser == null) {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.not.found", "en"),
                        false, new ArrayList<>(), null);
                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
            }


            try {

                try {


                    //logger.info("hit sub...");
                    List<FireBaseDevices> fireBaseDevicesList = this.firebaseDeviceService.findFireBaseDevicesByPortalUser(portalUser);

                    /// Add fcm device to group
                    if (fireBaseDevicesList.size() > 0) {

                        fireBaseDeviceGroup = this.firebaseDeviceService.findFireBaseDeviceGroupByPortalUser(portalUser);

                        regIds = new ArrayList<>();
                        for (FireBaseDevices fireBaseDevices : fireBaseDevicesList) {
                            regIds.add(fireBaseDevices.getRegistrationId());
                        }

                        if (fireBaseDeviceGroup != null) {
                            boolean status = this.firebaseDeviceService.updateDeviceGroup(portalUser, regIds, fireBaseDeviceGroup);
                        } else {
                            this.firebaseDeviceService.createDeviceGroup(portalUser, regIds);
                        }
                    }


                    // FireBaseDevices fireBaseDevices = this.firebaseDeviceService.saveDeviceRegistrationId(token, portalUser);
                    // if (fireBaseDevices != null) {


                    boolean wasSubscribed = this.firebaseDeviceService.subscribeOrUnsubscribeTopic(name.trim(), regIds, true);

                    for (FireBaseDevices fireBaseDevices : fireBaseDevicesList) {
                        if (wasSubscribed) {
                            this.firebaseDeviceService.updateFireBaseDevice(portalUser, fireBaseDevices, true);
                        } else {
                            errors.add("FCM upload failed");
                        }

                        // this.firebaseDeviceService.updateFireBaseGroupSubscription(fireBaseDeviceGroup, true);
                    }
                    //  }

                    if (errors.size() <= 0 & wasSubscribed) {
                        KafkaTopicModel kafkaTopicModel = this.kafkaTopicService.findByName(name.trim().toLowerCase());

                        KafkaSubscription alreadySubscribedTo = this.kafkaSubscriptionService.checkIfExists(portalUser, kafkaTopicModel);
                        if (alreadySubscribedTo != null) {
                            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.user.already.subscribed.to.topic", "en"),
                                    false, new ArrayList<>(), null);
                            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                        }
                        KafkaSubscription createdKafkaSubscription = this.kafkaSubscriptionService.add(portalUser, kafkaTopicModel);
                        List<KafkaSubscription> kafkaSubscriptionList = this.kafkaSubscriptionService.findAll(portalUser);

                        List<String> topics = new ArrayList<>();
                        for (KafkaSubscription kafkaSubscription : kafkaSubscriptionList) {
                            KafkaTopicModel kafkaTopicModel1 = this.kafkaTopicService.findById(kafkaSubscription.getKafkaTopicModel().getId());
                            topics.add(kafkaTopicModel1.getName());
                        }

                        this.utils.subscribe(topics, "" + portalUser.getId());
                        subscriptionPojo = new KafkaSubscriptionPojo();
                        KafkaTopicModel foundKafkaTopic = this.kafkaTopicService.findById(createdKafkaSubscription.getKafkaTopicModel().getId());
                        subscriptionPojo.setTopicId(foundKafkaTopic.getId());
                        subscriptionPojo.setTopicName(foundKafkaTopic.getName());
                        subscriptionPojo.setId(createdKafkaSubscription.getId());
                        subscriptionPojo.setDateCreated(createdKafkaSubscription.getDateCreated());
                        subscriptionPojo.setDateUpdated(createdKafkaSubscription.getDateUpdated());


                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.user.subscribed.to.topic", "en"),
                                true, new ArrayList<>(), subscriptionPojo);
                    } else {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.user.subscribed.to.topic.failed", "en"),
                                false, new ArrayList<>(), null);
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.user.subscribed.to.topic.failed", "en"),
                            false, new ArrayList<>(), null);
                }

            } catch (Exception e) {
                e.printStackTrace();

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.user.subscribed.to.topic.failed", "en"),
                        false, new ArrayList<>(), null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.user.subscribed.to.topic.failed", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @GetMapping(value = "/default/shila/unsubscribe/{id}")
    public ResponseEntity<Object> unsubscribe(@PathVariable("id") String id,
                                              @RequestParam("user-id") String userId) {

        ApiError apiError = null;
        try {

            if (TextUtils.isBlank(userId)) {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user-id.required", "en"),
                        false, new ArrayList<>(), null);
                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
            }

            PortalUser portalUser = this.portalUserService.findPortalUserById(Long.valueOf(userId));
            if (portalUser == null) {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.not.found", "en"),
                        false, new ArrayList<>(), null);
                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
            }

            KafkaSubscription alreadySubscribedTo = this.kafkaSubscriptionService.findByIdAndPortalUser(portalUser, Long.valueOf(id));
            if (alreadySubscribedTo == null) {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.user.not.subscribed.to.topic", "en"),
                        false, new ArrayList<>(), null);
                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
            }

            this.kafkaSubscriptionService.remove(alreadySubscribedTo);
            List<KafkaSubscription> kafkaSubscriptionList = this.kafkaSubscriptionService.findAll(portalUser);

            List<String> topics = new ArrayList<>();
            for (KafkaSubscription kafkaSubscription : kafkaSubscriptionList) {
                KafkaTopicModel kafkaTopicModel1 = this.kafkaTopicService.findById(kafkaSubscription.getKafkaTopicModel().getId());
                topics.add(kafkaTopicModel1.getName());
            }


            KafkaTopicModel kafkaTopicModel = this.kafkaTopicService.findById(alreadySubscribedTo.getKafkaTopicModel().getId());
            List<FireBaseDevices> fireBaseDevicesList = this.firebaseDeviceService.findFireBaseDevicesByPortalUser(portalUser);


            List<String> registrationTokens = new ArrayList<>();
            for (FireBaseDevices fireBaseDevices : fireBaseDevicesList) {
                registrationTokens.add(fireBaseDevices.getRegistrationId());
            }


            boolean wasSubscribed = this.firebaseDeviceService.subscribeOrUnsubscribeTopic(kafkaTopicModel.getName().trim(), registrationTokens, false);

            this.utils.subscribe(topics, "" + portalUser.getId());

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.user.unsubscribed.to.topic", "en"),
                    true, new ArrayList<>(), null);
        } catch (Exception e) {
            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.user.unsubscribed.to.topic.failed", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    //    update notification status to read on click
    @PostMapping(value = "/default/notification/status/{code}")
    public ResponseEntity<Object> notificationRead(@PathVariable("code") String code,
                                                   @Valid @RequestBody NotificationSetAsReadDto notificationSetAsReadDto,
                                                   HttpServletResponse response,
                                                   HttpServletRequest request,
                                                   Authentication authentication,
                                                   BindingResult bindingResult) {
        ApiError apiError = null;
        UserDetailsDto requestPrincipal = null;
        PortalUserModel portalUserModel = null;
        Long portalUserId = null;
        // logger.info("i got here");

        try {

            if (bindingResult.hasErrors()) {

                bindingResult.getAllErrors().forEach(objectError -> {
                    logger.info(objectError.toString());
                });

                throw new CustomBadRequestException();

            } else {

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

                try {

                    PortalUser portalUser = this.portalUserService.findById(portalUserId);

                    NotificationMongoDb notificationMongoDb = this.notificationMongodbService.findByCodeAndPortalUser(code, portalUser);

                    if (notificationMongoDb == null) {
                        NotificationPojo notificationPojo = new NotificationPojo();
                        notificationPojo.setCode(notificationSetAsReadDto.getCode());
                        notificationPojo.setUrl(notificationSetAsReadDto.getUrl());
                        notificationPojo.setTitle(notificationSetAsReadDto.getTitle());
                        notificationPojo.setDateCreated(notificationSetAsReadDto.getDateCreated());
                        notificationPojo.setDateUpdated(notificationSetAsReadDto.getDateUpdated());
                        //notificationPojo.setData();
                        notificationPojo.setMessage(notificationSetAsReadDto.getMessage());
                        //notificationPojo.setName();

                        this.notificationMongodbService.saveAndSetAsRead(notificationPojo, portalUser, true);
                    }

                    if (notificationMongoDb != null) {
                        if (notificationMongoDb.isRead()) {
                            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.notification.error.message.already.read", "en"),
                                    false, new ArrayList<>(), null);
                        } else {
                            notificationMongoDb.setRead(true);
                            this.notificationMongodbService.update(notificationMongoDb);
                            this.notificationCountMongoDbService.decrement(portalUser);
                            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.notification.error.fetching.message", "en"),
                                    true, new ArrayList<>(), null);
                        }
                    } else {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.notification.error.fetching.message", "en"),
                                false, new ArrayList<>(), null);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.notification.error.fetching.message", "en"),
                            false, new ArrayList<>(), null);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.notification.error.fetching.message", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @GetMapping(value = "/default/notification/count")
    public ResponseEntity<Object> notificationCountForUser(
            HttpServletResponse response,
            HttpServletRequest request,
            Authentication authentication) {
        ApiError apiError = null;
        UserDetailsDto requestPrincipal = null;
        PortalUserModel portalUserModel = null;
        Long portalUserId = null;
        try {

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


            PortalUser portalUser = this.portalUserService.findById(portalUserId);
            NotificationCountMongoDb notificationCountMongoDb =
                    this.notificationCountMongoDbService.findByPortalUserId(portalUser.getId());
            //this.notificationCountMongoDbService.increment(notificationCountMongoDb);

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.topic.deletion", "en"),
                    false, new ArrayList<>(), notificationCountMongoDb);

        } catch (Exception e) {
            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("kafka.topic.deletion", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
