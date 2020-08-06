package com.hertfordshire.restfulapi.controller.dashboard;

import com.google.gson.Gson;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.model.psql.OrdersModel;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.pojo.LabAccountDashboardPojo;
import com.hertfordshire.pojo.LabTestsPieChartPojo;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.test_order.TestOrderService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import com.hertfordshire.utils.errors.MyApiResponse;
import com.hertfordshire.utils.lhenum.PortalAccountTypeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class ProtectedDashboardController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedDashboardController.class.getSimpleName());

    @Autowired
    private TestOrderService testOrderService;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private PortalUserService portalUserService;

    @Value("${default.domainUrlOne}")
    private String domainUrlOne;

    @Value("${no.reply.email}")
    private String noReplyEmail;


    private Gson gson;

    @Autowired
    public ProtectedDashboardController() {
        this.gson = new Gson();
    }

    @GetMapping("/default/dashboard/lab")
    public ResponseEntity<Object> labAccountDashboard() {

        ApiError apiError = null;
        LabTestsPieChartPojo labTestsPieChartPojo = new LabTestsPieChartPojo();
        int patientCount = 0;
        Long patientsNumberOfTests = 0L;
        List<PortalUser> portalUserList;

        try {

            portalUserList = this.portalUserService.findAll();

            List<PortalUser> listOfPatients = new ArrayList<>();

            //Calculate number of patients
            for (PortalUser portalUser : portalUserList) {
                Set<PortalAccount> portalAccountSet = portalUser.getPortalAccounts();

                List<PortalAccount> portalAccountList = new ArrayList<>(portalAccountSet);

                logger.info("pa size: " + portalAccountList.size());

                PortalAccount portalAccount = portalAccountList.stream().findFirst().orElse(null);

                logger.info(portalUser.getCode());
                if (portalAccount != null) {
                    if (portalAccount.getPortalAccountType().equals(PortalAccountTypeConstant.PATIENT)) {
                        logger.info("patient added");
                        patientCount++;
                        listOfPatients.add(portalUser);
                    }
                }
            }

            logger.info("patients: "+ patientCount);

            for (PortalUser portalUser : listOfPatients) {

                List<OrdersModel> ordersModelList = this.testOrderService.findByPortalUser(portalUser);

                if (labTestsPieChartPojo.getPatientsNumberOfTests() != null) {
                    labTestsPieChartPojo.setPatientsNumberOfTests(labTestsPieChartPojo.getPatientsNumberOfTests() + (long) ordersModelList.size());
                } else {
                    labTestsPieChartPojo.setPatientsNumberOfTests((long) ordersModelList.size());
                }

                for (OrdersModel ordersModel : ordersModelList) {
                    if (labTestsPieChartPojo.getPatientsTestsAmount() != null) {
                        labTestsPieChartPojo.setPatientsTestsAmount(labTestsPieChartPojo.getPatientsTestsAmount() + ordersModel.getPrice());
                    } else {
                        labTestsPieChartPojo.setPatientsTestsAmount(String.valueOf(ordersModel.getPrice()));
                    }
                }
            }

            LabAccountDashboardPojo labAccountDashboardPojo = new LabAccountDashboardPojo();
            labAccountDashboardPojo.setNumberOfPatients((long) patientCount);

            if (labTestsPieChartPojo.getPatientsNumberOfTests() != null) {
                patientsNumberOfTests = labTestsPieChartPojo.getPatientsNumberOfTests();
            }

            labAccountDashboardPojo.setTotalNumberOfLabTestsOrdered(patientsNumberOfTests);

            logger.info(this.gson.toJson(labAccountDashboardPojo));


            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.creation.for.employee.successful", "en"),
                    true, new ArrayList<>(), labAccountDashboardPojo);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

        } catch (Exception e) {
            e.printStackTrace();
            return new MyApiResponse().internalServerErrorResponse();
        }

    }
}
