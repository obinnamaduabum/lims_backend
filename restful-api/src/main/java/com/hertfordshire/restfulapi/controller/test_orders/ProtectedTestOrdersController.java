package com.hertfordshire.restfulapi.controller.test_orders;

import com.google.gson.Gson;
import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.access.errors.CustomBadRequestException;
import com.hertfordshire.dao.psql.*;
import com.hertfordshire.dto.LabTestSampleDto;
import com.hertfordshire.dto.LabTestsOrderSearchDto;
import com.hertfordshire.dto.OrderedLabTestSearchDto;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.pojo.*;
import com.hertfordshire.pubsub.redis.model.PortalUserModel;
import com.hertfordshire.service.firebase.devices.FirebaseDeviceService;
import com.hertfordshire.service.mongodb.NotificationMongodbService;
import com.hertfordshire.service.psql.lab_test.LabTestService;
import com.hertfordshire.service.psql.lab_test_order_details.LabTestOrderDetailsService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.test_order.TestOrderService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;
import com.hertfordshire.utils.lhenum.SampleTypeConstant;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.Optional;

@RestController
public class ProtectedTestOrdersController extends ProtectedBaseApiController {

    private final Logger logger = LoggerFactory.getLogger(ProtectedTestOrdersController.class.getSimpleName());

    @Autowired
    private LabTestOrderDetailDao labTestOrderDetailDao;

    @Autowired
    private TestOrderService orderIdService;

    @Autowired
    private PortalUserService portalUserService;

    private Gson gson;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private LabTestService labTestService;

    @Autowired
    private LabTestOrderDetailsService labTestOrderDetailsService;

    @Autowired
    private SampleCollectedDao sampleCollectedDao;

    @Autowired
    private PortalUserInstitutionLabTestOrderDetailDao portalUserInstitutionLabTestOrderDetailDao;

    @Autowired
    private LabTestOrderDao labTestOrderDao;

    @Autowired
    private LabTestDao labTestDao;

    @Autowired
    private com.hertfordshire.pubsub.redis.service.portal_user.PortalUserService redisPortalUserService;

    @Autowired
    private UserService userService;

//    @Autowired
//    private NotificationMongodbService notificationMongodbService;

    @Value("${default.domainUrlTwo}")
    private String defaultDomainUrlTwo;

    @Autowired
    private FirebaseDeviceService firebaseDevicesService;


    public ProtectedTestOrdersController() {
        this.gson = new Gson();
    }

    @PostMapping("/default/test-orders")
    public ResponseEntity<Object> index(HttpServletResponse res,
                                        HttpServletRequest request,
                                        @RequestBody LabTestsOrderSearchDto labTestsOrderSearchDto,
                                        @RequestParam("page") int page,
                                        @RequestParam("size") int size) {
        if (page == 0) {
            page = 0;
        }

        if (size == 0) {
            size = 10;
        }

        Pageable sortedByDateCreated = PageRequest.of(page, size, Sort.by("date_created").descending());
        ApiError apiError = null;
        String lang = "en";

        try {
            String tmpLang = Utils.fetchLanguageFromCookie(request);
            if (tmpLang != null) {
                lang = tmpLang;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            PaginationResponsePojo paginationResponsePojo = this.orderIdService.findByWithPagination(labTestsOrderSearchDto, sortedByDateCreated);

            Long total = this.orderIdService.countByWithPagination(labTestsOrderSearchDto);

            paginationResponsePojo.setLength(total);

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("transaction.ref.success", lang),
                    true, new ArrayList<>(), paginationResponsePojo);


        } catch (Exception e) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("transaction.ref.success", lang),
                    false, new ArrayList<>(), null);
            e.printStackTrace();
        }


        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @PostMapping("/default/test-orders/specific-user")
    public ResponseEntity<Object> forSpecificPatient(HttpServletResponse res,
                                                     HttpServletRequest request,
                                                     Authentication authentication,
                                                     @Valid @RequestBody LabTestsOrderSearchDto labTestsOrderSearchDto,
                                                     @RequestParam("page") int page,
                                                     @RequestParam("size") int size) {
        if (page == 0) {
            page = 0;
        }


        if (size == 0) {
            size = 10;
        }

        Pageable sortedByDateCreated = PageRequest.of(page, size, Sort.by("date_created").descending());
        ApiError apiError = null;
        String lang = "en";

        try {
            String tmpLang = Utils.fetchLanguageFromCookie(request);
            if (tmpLang != null) {
                lang = tmpLang;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserDetailsDto requestPrincipal = null;

        requestPrincipal = userService.getPrincipal(res, request, authentication);

        String email = requestPrincipal.getEmail();

        PortalUser portalUser = portalUserService.findPortalUserByEmail(email);


        try {
            PaginationResponsePojo paginationResponsePojo = this.orderIdService.findByPortalUserWithPagination(portalUser, labTestsOrderSearchDto, sortedByDateCreated);

            Long total = this.orderIdService.countByPortalUserWithPagination(portalUser, labTestsOrderSearchDto);

            paginationResponsePojo.setLength(total);

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("transaction.ref.success", lang),
                    true, new ArrayList<>(), paginationResponsePojo);


        } catch (Exception e) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("transaction.ref.success", lang),
                    false, new ArrayList<>(), null);
            e.printStackTrace();
        }


        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @GetMapping("/default/test-orders/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {

        // logger.info("found ++++");

        ApiError apiError = null;
        String lang = "en";
        PortalUserPojo portalUserPojo = new PortalUserPojo();

        try {
            Optional<OrdersModel> ordersModelOptional = this.orderIdService.findById(id);

            if (ordersModelOptional.isPresent()) {

                OrdersModel ordersModel = ordersModelOptional.get();

                try {

                   // logger.info("order id" + ordersModel.getId());

                    PortalUser portalUser = this.portalUserService.findById(ordersModel.getPortalUser().getId());
                    if (portalUser != null) {
                        portalUserPojo.setFirstName(portalUser.getFirstName());
                        portalUserPojo.setLastName(portalUser.getLastName());
                        portalUserPojo.setCode(portalUser.getCode());
                        portalUserPojo.setOtherName(portalUser.getOtherName());
                        portalUserPojo.setPhoneNumber(portalUser.getPhoneNumber());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                LabTestOrdersPojo labTestOrdersPojo = new LabTestOrdersPojo();
                labTestOrdersPojo.setPortalUserPojo(portalUserPojo);
                labTestOrdersPojo.setId(ordersModel.getId());
                labTestOrdersPojo.setCurrencyType(ordersModel.getCurrencyType());
                labTestOrdersPojo.setCode(ordersModel.getCode());
                labTestOrdersPojo.setCashCollected(ordersModel.isCashCollected());
                labTestOrdersPojo.setDateCreated(ordersModel.getDateCreated());
                labTestOrdersPojo.setDateUpdated(ordersModel.getDateUpdated());

                if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.NGN)) {
                    labTestOrdersPojo.setPrice(Long.valueOf(Utils.koboToNaira(ordersModel.getPrice())));
                } else if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.USD)) {
                    labTestOrdersPojo.setPrice(ordersModel.getPrice());
                } else if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.EUR)) {
                    labTestOrdersPojo.setPrice(ordersModel.getPrice());
                }

                List<LabTestOrderDetail> labTestOrderDetails = labTestOrderDetailDao.findByOrdersModel(ordersModel);

                List<LabTestDetailsPojo> labTestDetailsPojos = new ArrayList<>();

                for (LabTestOrderDetail labTestOrderDetail : labTestOrderDetails) {

                    LabTestDetailsPojo labTestDetailsPojo = new LabTestDetailsPojo();
                    labTestDetailsPojo.setId(labTestOrderDetail.getId());
                    labTestDetailsPojo.setLabTestId(labTestOrderDetail.getLabTest().getId());
                    labTestDetailsPojo.setName(labTestOrderDetail.getName());
                    labTestDetailsPojo.setUniqueId(labTestOrderDetail.getUniqueId());


                    try {


                        if (labTestOrderDetail.getPortalUserInstitutionLabTestOrderDetail() != null) {

                            Optional<PortalUserInstitutionLabTestOrderDetail> optionalPILD = portalUserInstitutionLabTestOrderDetailDao.findById(
                                    labTestOrderDetail.getPortalUserInstitutionLabTestOrderDetail().getId());


                            if (optionalPILD.isPresent()) {
                                InstitutionPatientInfoPojo institutionPatientInfoPojo = new InstitutionPatientInfoPojo();
                                institutionPatientInfoPojo.setFirstName(optionalPILD.get().getFirstName());
                                institutionPatientInfoPojo.setLastName(optionalPILD.get().getLastName());
                                institutionPatientInfoPojo.setFileNumber(optionalPILD.get().getFileNumber());
                                institutionPatientInfoPojo.setPhoneNumber(optionalPILD.get().getPhoneNumber());
                                institutionPatientInfoPojo.setOtherName(optionalPILD.get().getOtherName());
                                institutionPatientInfoPojo.setDateCreated(optionalPILD.get().getDateCreated());
                                institutionPatientInfoPojo.setDateUpdated(optionalPILD.get().getDateUpdated());

                                labTestDetailsPojo.setInstitutionPatientInfoPojo(institutionPatientInfoPojo);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.NGN)) {
                        labTestDetailsPojo.setPrice(Long.valueOf(Utils.koboToNaira(labTestOrderDetail.getPrice())));
                    } else if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.USD)) {
                        labTestDetailsPojo.setPrice(labTestOrderDetail.getPrice());
                    } else if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.EUR)) {
                        labTestDetailsPojo.setPrice(labTestOrderDetail.getPrice());
                    }

                    labTestDetailsPojo.setQuantity(labTestOrderDetail.getQuantity());

                    if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.NGN)) {
                        labTestDetailsPojo.setTotal(Long.valueOf(Utils.koboToNaira(labTestOrderDetail.getTotal())));
                    } else if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.USD)) {
                        labTestDetailsPojo.setTotal(labTestOrderDetail.getTotal());
                    } else if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.EUR)) {
                        labTestDetailsPojo.setTotal(labTestOrderDetail.getTotal());
                    }


                    if (labTestOrderDetail.getSampleCollected() != null) {
                        Optional<SampleCollectedModel> optionalSampleCollectedModel = this.sampleCollectedDao.findById(labTestOrderDetail.getSampleCollected().getId());
                        if (optionalSampleCollectedModel.isPresent()) {
                            SampleCollectedModel sampleCollectedModel = optionalSampleCollectedModel.get();
                            SampleCollectionPojo sampleCollectionPojo = new SampleCollectionPojo();

                            if (sampleCollectedModel.getCollectedBy() != null) {
                                if (sampleCollectedModel.getCollectedBy().getId() != null) {
                                    sampleCollectionPojo.setCollectedBy(sampleCollectedModel.getCollectedBy().getId());
                                }
                            }


                            if (sampleCollectedModel.getSampleCollected() != null) {
                                if (sampleCollectedModel.getDateCreated() != null) {
                                    if (sampleCollectedModel.getSampleCollected().equals(SampleTypeConstant.SAMPLE_COLLECTED)) {
                                        sampleCollectionPojo.setDateCreated(sampleCollectedModel.getDateCreated());
                                    } else {
                                        sampleCollectionPojo.setDateCreated(null);
                                    }
                                } else {
                                    sampleCollectionPojo.setDateCreated(null);
                                }

                                if (sampleCollectedModel.getDateUpdated() != null) {
                                    if (sampleCollectedModel.getSampleCollected().equals(SampleTypeConstant.SAMPLE_COLLECTED)) {
                                        sampleCollectionPojo.setDateCreated(sampleCollectedModel.getDateUpdated());
                                    } else {
                                        sampleCollectionPojo.setDateCreated(null);
                                    }
                                } else {
                                    sampleCollectionPojo.setDateCreated(null);
                                }


                                sampleCollectionPojo.setSampleCollected(sampleCollectedModel.getSampleCollected().toString().toUpperCase());
                            }

                            labTestDetailsPojo.setSampleCollectionPojo(sampleCollectionPojo);
                        }
                    }

                    labTestDetailsPojos.add(labTestDetailsPojo);
                }

                labTestOrdersPojo.setLabTestDetailsPojos(labTestDetailsPojos);

//                logger.info("found ++++");
//                logger.info(this.gson.toJson(labTestOrdersPojo));
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("transaction.ref.success", lang),
                        true, new ArrayList<>(), labTestOrdersPojo);
            } else {

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("transaction.ref.success", lang),
                        false, new ArrayList<>(), null);
            }
        } catch (Exception e) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("transaction.ref.success", lang),
                    false, new ArrayList<>(), null);
            e.printStackTrace();
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @PostMapping("/default/test-orders/{id}")
    public ResponseEntity<Object> updateSampleStatus(@PathVariable Long id,
                                                     @Valid @RequestBody LabTestSampleDto labTestSampleDto,
                                                     HttpServletResponse response,
                                                     HttpServletRequest request,
                                                     Authentication authentication,
                                                     BindingResult bindingResult) {
        ApiError apiError = null;
        UserDetailsDto requestPrincipal = null;
        String lang = "en";
        Long portalUserId = 0L;


        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {

            try {
                String formOfIdentification = userService.fetchFormOfIdentification();
                if (!TextUtils.isBlank(formOfIdentification)) {
                    PortalUserModel portalUserModel = this.redisPortalUserService.fetchPortalUser(formOfIdentification);
                    portalUserId = portalUserModel.getId();
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

                apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("user.unauthorized", "en"),
                        false, new ArrayList<>(), null);

                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
            }


            PortalUser portalUser = this.portalUserService.findPortalUserById(portalUserId);

            try {

                Optional<OrdersModel> optionalOrdersModel = this.orderIdService.findById(id);
                OrdersModel ordersModel = null;
                if (optionalOrdersModel.isPresent()) {
                    ordersModel = optionalOrdersModel.get();
                    if (!ordersModel.isCashCollected()) {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("cash.not.collected", lang),
                                false, new ArrayList<>(), null);
                        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                    }
                }
                Optional<LabTest> optionalLabTest = this.labTestService.findById(labTestSampleDto.getLabTestId());
                LabTest labTest = null;
                if (optionalLabTest.isPresent()) {
                    labTest = optionalLabTest.get();
                }


                LabTestOrderDetail labTestOrderDetail =
                        this.labTestOrderDetailsService
                                .findByOrdersModelAndLabTestAndUniqueId(ordersModel, labTest, labTestSampleDto.getUniqueId());


                SampleCollectedModel sampleCollectedModel = this.labTestOrderDetailsService
                        .findSampleCollectionStatusByPortalUserAndLabTestOrderDetail(portalUser, labTestOrderDetail);


                if (sampleCollectedModel == null) {

                    sampleCollectedModel = this.labTestOrderDetailsService.updateSampleCollectionStatus(portalUser, labTestOrderDetail);

                    PortalUser collectedBy = this.portalUserService.findById(sampleCollectedModel.getCollectedBy().getId());

                    if (collectedBy != null) {

                        PortalUserPojo portalUserPojo = new PortalUserPojo();
                        portalUserPojo.setPhoneNumber(collectedBy.getPhoneNumber());
                        portalUserPojo.setFirstName(collectedBy.getFirstName());
                        portalUserPojo.setLastName(collectedBy.getLastName());
                        portalUserPojo.setOtherName(collectedBy.getOtherName());

                        SampleCollectionPojo sampleCollectionPojo = new SampleCollectionPojo();
                        sampleCollectionPojo.setSampleCollected(sampleCollectedModel.getSampleCollected().toString().toUpperCase());
                        sampleCollectionPojo.setCollectedBy(sampleCollectedModel.getCollectedBy().getId());
                        // sampleCollectionPojo.setCollectedBy(sampleCollectedModel.getCollectedBy().getId());
                        sampleCollectionPojo.setDateCreated(sampleCollectedModel.getDateCreated());
                        sampleCollectionPojo.setDateUpdated(sampleCollectedModel.getDateUpdated());

                        SampleAndPortalUserPojo sampleAndPortalUserPojo = new SampleAndPortalUserPojo();
                        sampleAndPortalUserPojo.setPortalUserPojo(portalUserPojo);
                        sampleAndPortalUserPojo.setSampleCollectionPojo(sampleCollectionPojo);

                        ///////////// push notification

//                        String url = "/dashboard/lab/orders/"+ ;
//
//                        String code = this.notificationMongodbService.getNotificationId();
//
//                        String title = this.messageUtil.getMessage("lab.test.ordered.by", lang) + " " + type;
//                        this.producer.sendMessage(title, message, code, url, topics);
//                        //  publish message to kafka
//
//                        //this.firebaseDevicesService.pushMessageToGroup(null, title, message, url, "APA91bGJWnPsbtMLAJ8_gMGu9ru9recYk1YqeC7-QrrgQPF6J9-CrORFVLBD6MF7fmuMP6MzMxi8CCftUy1sq-5gMIwmgurgKxNoasI03G27Fy8A9KaR6M1qo7y45twFZ_yVlB2DvtvZ");
//                        logger.info("url: " + this.defaultDomainUrlTwo + url);
//                        this.firebaseDevicesService.pushMessageToTopic(topics.stream().findFirst().orElse(""), title, message, code, url, null);

                        //this.pushNotificationService.sendPushNotificationToTopic(topics.stream().findFirst().orElse(""), title, message, url);
                        //  publish message to firebase

                        ///////////// push notification

                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("sample.collection.status", lang),
                                true, new ArrayList<>(), sampleAndPortalUserPojo);

                    } else {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("sample.collection.status.failed", lang),
                                false, new ArrayList<>(), null);
                    }

                } else {

                   // logger.info("updating sample info ...");

                    if(sampleCollectedModel.getSampleCollected().equals(SampleTypeConstant.SAMPLE_NOT_COLLECTED)){
                        Optional<LabTest> optionalLabTest1 = this.labTestDao.findById(labTestOrderDetail.getLabTest().getId());
                        if(optionalLabTest1.isPresent()) {
                            LabTest labTest1 = optionalLabTest1.get();
                            if (!TextUtils.isBlank(labTest1.getResultTemplateId())) {
                                this.labTestOrderDetailsService.updateSampleCollectionStatus(labTestOrderDetail, portalUser);
                            } else {
                                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("please.assign.template", lang),
                                        false, new ArrayList<>(), null);
                                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                            }
                        } else {
                            logger.info("test not found");
                        }



                        PortalUser collectedBy = this.portalUserService.findById(sampleCollectedModel.getCollectedBy().getId());

                        if (collectedBy != null) {

                            PortalUserPojo portalUserPojo = new PortalUserPojo();
                            portalUserPojo.setPhoneNumber(collectedBy.getPhoneNumber());
                            portalUserPojo.setFirstName(collectedBy.getFirstName());
                            portalUserPojo.setLastName(collectedBy.getLastName());
                            portalUserPojo.setOtherName(collectedBy.getOtherName());

                            SampleCollectionPojo sampleCollectionPojo = new SampleCollectionPojo();
                            sampleCollectionPojo.setSampleCollected(sampleCollectedModel.getSampleCollected().toString().toUpperCase());
                            sampleCollectionPojo.setCollectedBy(sampleCollectedModel.getCollectedBy().getId());
                            // sampleCollectionPojo.setCollectedBy(sampleCollectedModel.getCollectedBy().getId());
                            sampleCollectionPojo.setDateCreated(sampleCollectedModel.getDateCreated());
                            sampleCollectionPojo.setDateUpdated(sampleCollectedModel.getDateUpdated());

                            SampleAndPortalUserPojo sampleAndPortalUserPojo = new SampleAndPortalUserPojo();
                            sampleAndPortalUserPojo.setPortalUserPojo(portalUserPojo);
                            sampleAndPortalUserPojo.setSampleCollectionPojo(sampleCollectionPojo);


//                            String url = "";
//                            if (portalAccount.getPortalAccountType().equals(PortalAccountTypeConstant.PATIENT)) {
//                                url = "/dashboard/lab/orders/" + ordersModel.getId();
//                            }
//
//
//                            //dashboard/lab/orders/13422
//
//                            if (portalAccount.getPortalAccountType().equals(PortalAccountTypeConstant.INSTITUTION)) {
//                                url = "/dashboard/lab/orders/institution/" + ordersModel.getId();
//                            }
//
//
//                            String code = this.notificationMongodbService.getNotificationId();
//
//                            String title = this.messageUtil.getMessage("lab.test.ordered.by", lang) + " " + type;
//                            this.producer.sendMessage(title, message, code, url, topics);
//                            //  publish message to kafka
//
//                            //this.firebaseDevicesService.pushMessageToGroup(null, title, message, url, "APA91bGJWnPsbtMLAJ8_gMGu9ru9recYk1YqeC7-QrrgQPF6J9-CrORFVLBD6MF7fmuMP6MzMxi8CCftUy1sq-5gMIwmgurgKxNoasI03G27Fy8A9KaR6M1qo7y45twFZ_yVlB2DvtvZ");
//                            logger.info("url: " + this.defaultDomainUrlTwo + url);
//                            this.firebaseDevicesService.pushMessageToTopic(topics.stream().findFirst().orElse(""), title, message, code, url, null);
//
//                            //this.pushNotificationService.sendPushNotificationToTopic(topics.stream().findFirst().orElse(""), title, message, url);
//                            //  publish message to firebase




                            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("sample.collection.status", lang),
                                    true, new ArrayList<>(), sampleAndPortalUserPojo);
                        } else {
                            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("sample.collection.status.failed", lang),
                                    false, new ArrayList<>(), null);
                        }
                    } else if (sampleCollectedModel.getSampleCollected().equals(SampleTypeConstant.SAMPLE_COLLECTED)) {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("sample.already.collected", lang),
                                false, new ArrayList<>(), null);
                        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                    } else {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("sample.collection.status.failed", lang),
                                false, new ArrayList<>(), null);
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("sample.collection.status.failed", lang),
                        false, new ArrayList<>(), null);
            }

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }


    @GetMapping("/default/test-orders/cash-collected-status/{id}")
    public ResponseEntity<Object> updateCashCollectedStatus(@PathVariable Long id) {

        ApiError apiError = null;
        String lang = "en";

        try {
            Optional<OrdersModel> ordersModelOptional = this.orderIdService.findById(id);

            if (ordersModelOptional.isPresent()) {

                OrdersModel ordersModel = ordersModelOptional.get();

                // PortalUser portalUser = this.portalUserService.findById(ordersModel.getPortalUser().getId());

                if (ordersModel.isCashCollected()) {

                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("cash.already.collected", lang),
                            false, new ArrayList<>(), null);

                } else {

                    ordersModel.setCashCollected(true);

                    this.labTestOrderDao.save(ordersModel);

                    this.labTestOrderDetailsService.updateSampleCollectionDbWithDataOnceCashIsCollected(null, ordersModel);

                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("cash.collection.status.updated.successfully", lang),
                            true, new ArrayList<>(), null);
                }

            } else {

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.ordered.not.found", lang),
                        false, new ArrayList<>(), null);
            }

        } catch (Exception e) {

            e.printStackTrace();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.ordered.not.found", lang),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    ///finalize and notify lab scientist
    @GetMapping("/default/test-orders/finalize-test-order/{id}")
    public ResponseEntity<Object> finalizeSampleCollectionAndNotifyMedicalLabScientist(@PathVariable Long id) {

        ApiError apiError = null;
        String lang = "en";

        try {
            Optional<OrdersModel> ordersModelOptional = this.orderIdService.findById(id);

            if (ordersModelOptional.isPresent()) {

                OrdersModel ordersModel = ordersModelOptional.get();

                // PortalUser portalUser = this.portalUserService.findById(ordersModel.getPortalUser().getId());


                if (!ordersModel.isCashCollected()) {

                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("cash.collection.status.updated.successfully", lang),
                            true, new ArrayList<>(), null);

                    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                }

                List<LabTestOrderDetail> labTestOrderDetails = this.labTestOrderDetailsService.findByOrdersModel(ordersModel);

                for (LabTestOrderDetail labTestOrderDetail : labTestOrderDetails) {

                    SampleCollectedModel sampleCollectedModel =
                            this.labTestOrderDetailsService.findSampleCollectedById(labTestOrderDetail.getSampleCollected().getId());

                    if (sampleCollectedModel.getSampleCollected() == null) {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.ordered.not.found", lang),
                                false, new ArrayList<>(), null);

                        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                    }

                }


                // publish message
//                List<String> topics = new ArrayList<>();
//                topics.add("lab-test-ordered-notify-medical-lab-scientist");
//
//                String message = this.messageUtil.getMessage("hello.the.user", lang)
//                        + " " +
//                        portalUser.getFirstName() + " " + portalUser.getLastName()
//                        + " " +
//                        this.messageUtil.getMessage("with.phone.number", lang)
//                        + " " +
//                        portalUser.getPhoneNumber()
//                        + " " +
//                        this.messageUtil.getMessage("ordered.a.lab.test", lang);
//
//                if (ordersModel.isCashCollected()) {
//                    message += this.messageUtil.getMessage("but.cash.was.not.collected", lang);
//                } else {
//                    message += this.messageUtil.getMessage("and.cash.was.collected", lang);
//                }
//                String url = "/dashboard/lab/orders/" + ordersModel.getId();
//                String title = this.messageUtil.getMessage("lab.test.ordered.by", lang) + " "+ type;
//                this.producer.sendMessage(title, message, url, topics);
                //  publish message


            } else {

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.ordered.not.found", lang),
                        false, new ArrayList<>(), null);
            }

        } catch (Exception e) {

            e.printStackTrace();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.ordered.not.found", lang),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @PostMapping("/default/orders/lab-tests")
    public ResponseEntity<Object> viewLabTestsOrderedByUser(HttpServletResponse res,
                                                            HttpServletRequest request,
                                                            Authentication authentication,
                                                            @Valid @RequestBody OrderedLabTestSearchDto orderedLabTestSearchDto,
                                                            @RequestParam("page") int page,
                                                            @RequestParam("size") int size) {

        if (size == 0) {
            size = 10;
        }

        Pageable sortedByDateCreated = PageRequest.of(page, size, Sort.by("date_created").descending());
        ApiError apiError = null;
        String lang = "en";

        try {
            String tmpLang = Utils.fetchLanguageFromCookie(request);
            if (tmpLang != null) {
                lang = tmpLang;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        UserDetailsDto requestPrincipal = null;
//
//        requestPrincipal = userService.getPrincipal(res, request, authentication);
//
//        String email = requestPrincipal.getEmail();
//
//        PortalUser portalUser = portalUserService.findPortalUserByEmail(email);


        try {
            // logger.info("orderedLabTestSearchDto");
            // logger.info(this.gson.toJson(orderedLabTestSearchDto));

            PaginationResponsePojo paginationResponsePojo = this.labTestOrderDetailsService.findAllByLabTestsOrdered(orderedLabTestSearchDto, sortedByDateCreated);

            Long total = this.labTestOrderDetailsService.countAllByLabTestOrdered(orderedLabTestSearchDto);

            paginationResponsePojo.setLength(total);

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("transaction.ref.success", lang),
                    true, new ArrayList<>(), paginationResponsePojo);

        } catch (Exception e) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("transaction.ref.success", lang),
                    false, new ArrayList<>(), null);
            e.printStackTrace();
        }


        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
