package com.hertfordshire.restfulapi.controller.payment_method;

import com.google.gson.Gson;
import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.access.errors.CustomBadRequestException;
import com.hertfordshire.dao.psql.PaymentMethodInfoDao;
import com.hertfordshire.dto.PaymentMethodInfoDto;
import com.hertfordshire.dto.PaymentTransAndReferredByDoesNotExistDto;
import com.hertfordshire.dto.payment.FlutterWaveVerifyPaymentDto;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.payment.dto.PaymentTransactionUpdateDto;
import com.hertfordshire.payment.service.payment_code.PaymentCodeSequenceService;
import com.hertfordshire.pubsub.redis.model.PortalUserModel;
import com.hertfordshire.pubsub.redis.service.payment.RedisPaymentService;
import com.hertfordshire.service.firebase.PushNotificationService;
import com.hertfordshire.service.mongodb.NotificationMongodbService;
import com.hertfordshire.service.psql.payment_method_config.PaymentMethodConfigService;
import com.hertfordshire.service.psql.payment_transaction.PaymentTransactionService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.test_order.TestOrderService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import com.hertfordshire.utils.lhenum.PortalAccountTypeConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class ProtectedPaymentMethodController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedPaymentMethodController.class.getSimpleName());

    private Gson gson;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private PaymentMethodInfoDao paymentMethodInfoDao;

    @Autowired
    private PaymentMethodConfigService paymentMethodConfigService;

    @Autowired
    private UserService userService;

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private PaymentTransactionService paymentService;

    @Autowired
    private TestOrderService orderIdService;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private PaymentCodeSequenceService paymentCodeSequenceService;

    @Autowired
    private RedisPaymentService redisPaymentService;


    @Autowired
    private com.hertfordshire.pubsub.redis.service.portal_user.PortalUserService redisPortalUserService;

//    @Autowired
//    private NotificationMongodbService notificationMongodbService;


    @Value("${default.domainUrlTwo}")
    private String defaultDomainUrlTwo;

    @GetMapping("/auth/payment-methods/")
    public ResponseEntity<Object> index() {

        ApiError apiError = null;
        List<PaymentMethodInfo> paymentMethodInfoList = new ArrayList<>();



        try {

            paymentMethodInfoList = this.paymentMethodConfigService.findAllOrderById();

//            List<PaymentMethodInfoModel> paymentMethodInfoModels = new ArrayList<>();
//
//            for(PaymentMethodInfo paymentMethodInfo: paymentMethodInfoList) {
//
//                PaymentMethodInfoModel paymentMethodInfoModel = new PaymentMethodInfoModel();
//                paymentMethodInfoModel.setDateCreated(paymentMethodInfo.getDateCreated());
//                paymentMethodInfoModel.setDateUpdated(paymentMethodInfo.getDateUpdated());
//                paymentMethodInfoModel.set
//                paymentMethodInfoModels.add(paymentMethodInfoModel);
//            }
//
//            this.redisPaymentService.saveAll(paymentMethodInfoModels);

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.creation.successful", "en"),
                    true, new ArrayList<>(), paymentMethodInfoList);

        } catch (Exception e) {
            e.printStackTrace();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.creation.successful", "en"),
                    false, new ArrayList<>(), paymentMethodInfoList);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @GetMapping("/auth/payment-methods/{id}")
    public ResponseEntity<Object> getOne(@PathVariable("id") Long id) {

        ApiError apiError = null;

        if (id == 0) {

            apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, messageUtil.getMessage("not.found.paymentinfo.successfully", "en"),
                    false, new ArrayList<>(), null);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

        }

        try {

            PaymentMethodInfo oldPaymentMethodInfo = this.paymentMethodInfoDao.getOne(id);

            PaymentMethodInfoDto paymentMethodInfo = new PaymentMethodInfoDto();

            paymentMethodInfo.setEnabled(oldPaymentMethodInfo.isEnabled());

            paymentMethodInfo.setPaymentMethodName(oldPaymentMethodInfo.getPaymentMethodName());

            paymentMethodInfo.setLiveActive(oldPaymentMethodInfo.isLiveActive());

            paymentMethodInfo.setLivePublicKey(oldPaymentMethodInfo.getLivePublicKey());

            paymentMethodInfo.setLiveSecret(oldPaymentMethodInfo.getLiveSecret());

            paymentMethodInfo.setLiveVerifyUrl(oldPaymentMethodInfo.getLiveVerifyUrl());

            paymentMethodInfo.setTestingPublicKey(oldPaymentMethodInfo.getTestingPublicKey());

            paymentMethodInfo.setTestingSecret(oldPaymentMethodInfo.getTestingSecret());

            paymentMethodInfo.setTestingVerifyUrl(oldPaymentMethodInfo.getTestingVerifyUrl());

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("found.paymentinfo.successfully", "en"),
                    true, new ArrayList<>(), paymentMethodInfo);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

        } catch (Exception e) {

            e.printStackTrace();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("not.found.paymentinfo.successfully", "en"),
                    false, new ArrayList<>(), null);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }

    @PostMapping("/auth/payment-methods/edit/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Long id,
                                         @RequestBody PaymentMethodInfoDto paymentMethodInfoDto) {

        ApiError apiError = null;

        if (id == 0) {

            apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, messageUtil.getMessage("not.found.paymentinfo.successfully", "en"),
                    false, new ArrayList<>(), null);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

        }

        try {


            PaymentMethodInfo paymentMethodInfo = this.paymentMethodInfoDao.getOne(id);

            paymentMethodInfo.setEnabled(paymentMethodInfoDto.isEnabled());

            paymentMethodInfo.setLiveActive(paymentMethodInfoDto.isLiveActive());

            paymentMethodInfo.setLivePublicKey(paymentMethodInfoDto.getLivePublicKey());

            paymentMethodInfo.setLiveSecret(paymentMethodInfoDto.getLiveSecret());

            paymentMethodInfo.setLiveVerifyUrl(paymentMethodInfoDto.getLiveVerifyUrl());

            paymentMethodInfo.setTestingPublicKey(paymentMethodInfoDto.getTestingPublicKey());

            paymentMethodInfo.setTestingSecret(paymentMethodInfoDto.getTestingSecret());

            paymentMethodInfo.setTestingVerifyUrl(paymentMethodInfoDto.getTestingVerifyUrl());

            this.paymentMethodInfoDao.save(paymentMethodInfo);

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("payment.info.update.successful", "en"),
                    true, new ArrayList<>(), null);

        } catch (Exception e) {

            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("payment.info.update.not.successful", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @PostMapping("/auth/payment-methods/rave/update")
    public ResponseEntity<Object> updateTransactionForBookingBoardRoom(@RequestBody PaymentTransactionUpdateDto paymentTransactionUpdateDto) {

        ApiError apiError = null;

        if (StringUtils.isBlank(paymentTransactionUpdateDto.getTransactionReference())) {
            apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, messageUtil.getMessage("not.found.paymentinfo.successfully", "en"),
                    false, new ArrayList<>(), null);
            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        try {

            PaymentTransaction paymentTransaction = this.paymentService.findByTransactionRef(paymentTransactionUpdateDto.getTransactionReference());

//            BookBoardRoom bookBoardRoom = this.bookBoardRoomDao.findByTransactionRef(paymentTransactionUpdateDto.getTransactionReference());
//
//            this.bookBoardRoomService.update(paymentTransaction, bookBoardRoom, paymentTransactionUpdateDto);

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("payment.info.update.successful", "en"),
                    true, new ArrayList<>(), paymentTransaction);

        } catch (Exception e) {

            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("payment.info.update.not.successful", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @GetMapping("/auth/payment-methods/transaction-ref")
    public ResponseEntity<Object> generateTransactionRef() {

        ApiError apiError = null;

        String transactionRef = "";


        try {

            transactionRef = String.format("TRANS_REF%04d%02d%02d%05d",
                    LocalDate.now().getYear(),
                    LocalDate.now().getMonthValue(),
                    LocalDate.now().getDayOfMonth(),
                    paymentCodeSequenceService.getNextId()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("transaction.ref.success", "en"),
                true, new ArrayList<>(), transactionRef);

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @PostMapping("/auth/payment/verify")
    public ResponseEntity<Object> verifyPayment(@RequestBody FlutterWaveVerifyPaymentDto flutterWaveVerifyPaymentDto) {
        ApiError apiError = null;

        try {
//            FlutterWaveServiceImpl flutterWaveService = new FlutterWaveServiceImpl();
//            PaymentMethodInfo paymentMethodInfo = this.paymentMethodConfigService.findByPaymentMethodName(PaymentMethodConstant.FLUTTER_WAVE.toString());
//            flutterWaveService.fetchRequiredInfo(paymentMethodInfo);
//            JSONObject jsonObject = flutterWaveService.verify(flutterWaveVerifyPaymentDto.getTransactionRef(), flutterWaveVerifyPaymentDto.getAmount(), 1);
//
//            logger.info(this.gson.toJson(jsonObject));
//
//            PaymentTransaction paymentTransaction = this.paymentService.findByTransactionRef(flutterWaveVerifyPaymentDto.getTransactionRef());
//            paymentTransaction.setPaymentVerified(true);
//            this.paymentTransactionDao.save(paymentTransaction);
//            //this.paymentService.save()
//            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("payment.info.update.successful", "en"),
//                    true, new ArrayList<>(), null);
        } catch (Exception e) {
            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("payment.info.update.not.successful", "en"),
                    false, new ArrayList<>(), null);
        }
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    ///////// orders are created here
    @PostMapping("/auth/payment/transaction/create")
    public ResponseEntity<Object> createPaymentRecord(@Valid @RequestBody PaymentTransAndReferredByDoesNotExistDto paymentTransAndReferredByDoesNotExistDto,
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
                    // logger.info("portalUserModelId: " + portalUserModel.getId());
                    portalUserId = portalUserModel.getId();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                if(portalUserId == 0L) {
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


            PaymentTransaction foundPaymentTransaction =
                    this.paymentService.findByTransactionRef(paymentTransAndReferredByDoesNotExistDto.getPaymentTransactionDto().getTransactionRef());

            if (foundPaymentTransaction != null) {

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("payment.transaction.ref.record.already.exists", lang),
                        false, new ArrayList<>(), null);

                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
            }

            try {

                PaymentMethodInfo paymentMethodInfo =
                        this.paymentMethodConfigService.findByPaymentMethodName(paymentTransAndReferredByDoesNotExistDto.getPaymentTransactionDto().getTransactionTypeConstant());

                PaymentTransaction paymentTransaction = this.paymentService.save(paymentTransAndReferredByDoesNotExistDto.getPaymentTransactionDto(), paymentMethodInfo, portalUser);
                OrdersModel ordersModel = this.orderIdService.create(paymentTransaction, paymentTransAndReferredByDoesNotExistDto.getPaymentTransactionDto(),
                        paymentTransAndReferredByDoesNotExistDto.getReferredByDoesNotExistDto(), portalUser);

                String type = "";

                Set<PortalAccount> portalAccountSet = null;
                if(portalUser != null) {
                    portalAccountSet = portalUser.getPortalAccounts();
                    logger.info("portal user found");
                } else {
                    logger.info("portal user not found");
                }


                if(portalAccountSet != null) {
//                List<PortalAccount> portalAccountList = new ArrayList<>(portalAccountSet);
//                portalAccountList.
                    PortalAccount portalAccount = portalAccountSet.stream().findFirst().orElse(null);

                    if (portalAccount != null) {

                        type = this.messageUtil.getMessage(portalAccount.getPortalAccountType().name().toLowerCase(), lang);

                        // publish message
                        List<String> topics = new ArrayList<>();
                        topics.add("lab-test-ordered");

                        String message = this.messageUtil.getMessage("hello.the.user", lang)
                                + " " +
                                portalUser.getFirstName() + " " + portalUser.getLastName()
                                + " " +
                                this.messageUtil.getMessage("with.phone.number", lang)
                                + " " +
                                portalUser.getPhoneNumber()
                                + " " +
                                this.messageUtil.getMessage("ordered.a.lab.test", lang);

                        if (ordersModel.isCashCollected()) {
                            message += this.messageUtil.getMessage("but.cash.was.not.collected", lang);
                        } else {
                            message += this.messageUtil.getMessage("and.cash.was.collected", lang);
                        }

                        String url = "";
                        if (portalAccount.getPortalAccountType().equals(PortalAccountTypeConstant.PATIENT)) {
                            url = "/dashboard/lab/orders/" + ordersModel.getId();
                        }



                        String code  = "";
                                //= this.notificationMongodbService.getNotificationId();

                        String title = this.messageUtil.getMessage("lab.test.ordered.by", lang) + " " + type;

                        this.pushNotificationService.sendPushNotificationToFCMAndKafka(title, message, code, url, topics);

                        apiError = new ApiError(HttpStatus.CREATED.value(), HttpStatus.CREATED, messageUtil.getMessage("payment.info.update.successful", lang),
                                true, new ArrayList<>(), null);
                    } else {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("payment.info.update.not.successful", lang),
                                false, new ArrayList<>(), null);
                    }
                } else {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("payment.info.update.not.successful", lang),
                            false, new ArrayList<>(), null);
                }

            } catch (Exception e) {

                e.printStackTrace();
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("payment.info.update.not.successful", lang),
                        false, new ArrayList<>(), null);
            }

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }
}
