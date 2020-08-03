package com.hertfordshire.access.config;

import com.hertfordshire.access.transformation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class StartUpActions {

    private static final Logger logger = LoggerFactory.getLogger(StartUpActions.class);

    @Autowired
    private CreateCountryTransformer createCountryTransformer;

    @Autowired
    private CreatePrivilegesJsonTransform createPrivilegesJsonTransform;

    @Autowired
    CreatePortalUserRolesJsonTransform createPortalUserRolesJsonTransform;

    @Autowired
    private CreatePortalUserJsonTransformer createPortalUserJsonTransformer;

    @Autowired
    private CreatePaymentMethodInfoTransform createPaymentMethodInfoTransform;

    @Autowired
    private CreatePhoneNumberCodeTransformer createPhoneNumberCodeTransformer;

    @Autowired
    private CreateAdminSettingsJsonTransform createAdminSettingsJsonTransform;

    @Autowired
    private CreateLabTestCategoriesTransformer createLabTestCategoriesTransformer;

    @Autowired
    private CreateLabTestTransformer createLabTestTransformer;

    @Autowired
    private CreateKafkaTopicsJsonTransform createKafkaTopicsJsonTransform;


    @PostConstruct()
    public void initialize() {

        logger.info("started saving initial data");

        try {
            createCountryTransformer.createCountriesTransformer();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            createPrivilegesJsonTransform.createPrivileges();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            createPortalUserRolesJsonTransform.createUserRoles();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            createPaymentMethodInfoTransform.createPaymentMethods();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            createPhoneNumberCodeTransformer.create();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            createLabTestCategoriesTransformer.create();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            createLabTestTransformer.create();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
           // createAdminSettingsJsonTransform.create();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
           // createPortalUserJsonTransformer.createPortalUser();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
           // createKafkaTopicsJsonTransform.create();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
