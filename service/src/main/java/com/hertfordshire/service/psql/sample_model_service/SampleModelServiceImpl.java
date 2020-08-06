package com.hertfordshire.service.psql.sample_model_service;

import com.hertfordshire.dao.psql.SampleCollectedDao;
import com.hertfordshire.dto.LabTestSampleDto;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.pojo.PortalUserPojo;
import com.hertfordshire.pojo.SampleAndPortalUserPojo;
import com.hertfordshire.pojo.SampleCollectionPojo;
import com.hertfordshire.service.psql.lab_test.LabTestService;
import com.hertfordshire.service.psql.lab_test_order_details.LabTestOrderDetailsService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.utils.lhenum.SampleTypeConstant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional
public class SampleModelServiceImpl implements SampleModelService {

    @Autowired
    private LabTestService labTestService;

    @Autowired
    private LabTestOrderDetailsService labTestOrderDetailsService;

    @Autowired
    private SampleCollectedDao sampleCollectedDao;

    @Autowired
    private PortalUserService portalUserService;


    @Autowired
    private MessageUtil messageUtil;

    public ResponseEntity<Object> sample(OrdersModel ordersModel,
                                         LabTest labTest,
                                         LabTestSampleDto labTestSampleDto,
                                         PortalUser portalUser,
                                         ApiError apiError,
                                         String lang) {

        LabTestOrderDetail labTestOrderDetail = this.labTestOrderDetailsService.findByOrdersModelAndLabTestAndUniqueId(ordersModel, labTest, labTestSampleDto.getUniqueId());

        SampleCollectedModel sampleCollectedModel = this.labTestOrderDetailsService.findSampleCollectionStatusByPortalUserAndLabTestOrderDetail(portalUser, labTestOrderDetail);

        if (sampleCollectedModel == null) {

            return sampleNotFound(portalUser, labTestOrderDetail, lang);

        } else {

            // logger.info("updating sample info ...");

            if (sampleCollectedModel.getSampleCollected().equals(SampleTypeConstant.SAMPLE_NOT_COLLECTED)) {
//                        Optional<LabTest> optionalLabTest1 = this.labTestDao.findById(labTestOrderDetail.getLabTest().getId());
//                        if(optionalLabTest1.isPresent()) {
//                            LabTest labTest1 = optionalLabTest1.get();
//                            if (!TextUtils.isBlank(labTest1.getResultTemplateId())) {
//                                this.labTestOrderDetailsService.updateSampleCollectionStatus(labTestOrderDetail, portalUser);
//                            } else {
//                                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("please.assign.template", lang),
//                                        false, new ArrayList<>(), null);
//                                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
//                            }
//                        } else {
//                            logger.info("test not found");
//                        }

                sampleCollectedModel = this.labTestOrderDetailsService.updateSampleCollectionStatus(portalUser, labTestOrderDetail);

                PortalUser collectedBy = portalUser;

                if (collectedBy != null) {

                    SampleAndPortalUserPojo sampleAndPortalUserPojo = setup(collectedBy, sampleCollectedModel);


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
                }
                else {
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


            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }


    private ResponseEntity<Object> sampleNotFound(PortalUser portalUser, LabTestOrderDetail labTestOrderDetail, String lang) {

        ApiError apiError = null;

        SampleCollectedModel sampleCollectedModel = this.labTestOrderDetailsService.updateSampleCollectionStatus(portalUser, labTestOrderDetail);

        PortalUser collectedBy = this.portalUserService.findById(sampleCollectedModel.getCollectedBy().getId());

        if (collectedBy != null) {

            SampleAndPortalUserPojo sampleAndPortalUserPojo = setup(collectedBy, sampleCollectedModel);

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

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    SampleAndPortalUserPojo setup(PortalUser collectedBy, SampleCollectedModel sampleCollectedModel) {
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

        return sampleAndPortalUserPojo;
    }
}
