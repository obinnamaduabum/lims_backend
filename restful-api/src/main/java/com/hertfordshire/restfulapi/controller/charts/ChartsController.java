package com.hertfordshire.restfulapi.controller.charts;

import com.google.gson.Gson;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.model.psql.OrdersModel;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.pojo.LabTestsPieChartPojo;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.service.psql.test_order.TestOrderService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import com.hertfordshire.utils.lhenum.PortalAccountTypeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class ChartsController extends ProtectedBaseApiController {


    private static final Logger logger = LoggerFactory.getLogger(ChartsController.class.getSimpleName());

    @Autowired
    private PortalUserDao portalUserDao;


    private Gson gson;

    @Autowired
    private TestOrderService testOrderService;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    public ChartsController() {
        gson = new Gson();
    }

    @GetMapping("/default/charts/lab-tests")
    public ResponseEntity<Object> labsPatientsVsInstitutions() {

        ApiError apiError = null;
        try {

            List<PortalUser> portalUserList = portalUserDao.findAll();
            List<PortalUser> listOfPatients = new ArrayList<>();
            List<PortalUser> listOfInstitutions = new ArrayList<>();
            LabTestsPieChartPojo labTestsPieChartPojo = new LabTestsPieChartPojo();


            for(PortalUser portalUser: portalUserList) {
                Set<PortalAccount> portalAccountSet = portalUser.getPortalAccounts();
                List<PortalAccount> portalAccountList = new ArrayList<>(portalAccountSet);
                PortalAccount portalAccount = portalAccountList.stream().findFirst().orElse(null);

                if(portalAccount != null) {
                    if (portalAccount.getPortalAccountType().equals(PortalAccountTypeConstant.PATIENT)) {
                        listOfPatients.add(portalUser);
                    }
                }
            }

            for(PortalUser portalUser: listOfPatients) {
                List<OrdersModel> ordersModelList = this.testOrderService.findByPortalUser(portalUser);

                if(labTestsPieChartPojo.getPatientsNumberOfTests() != null) {
                    labTestsPieChartPojo.setPatientsNumberOfTests(labTestsPieChartPojo.getPatientsNumberOfTests() + (long) ordersModelList.size());
                } else {
                    labTestsPieChartPojo.setPatientsNumberOfTests((long) ordersModelList.size());
                }

                for(OrdersModel ordersModel: ordersModelList) {
                    if(labTestsPieChartPojo.getPatientsTestsAmount() != null) {
                        labTestsPieChartPojo.setPatientsTestsAmount(labTestsPieChartPojo.getPatientsTestsAmount() + ordersModel.getPrice());
                    } else {
                        labTestsPieChartPojo.setPatientsTestsAmount(String.valueOf(ordersModel.getPrice()));
                    }
                }

            }

            //logger.info(this.gson.toJson(labTestsPieChartPojo));
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.creation.not.successful", "en"),
                    true, new ArrayList<>(), labTestsPieChartPojo);

        } catch (Exception e) {
            e.printStackTrace();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.creation.not.successful", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
