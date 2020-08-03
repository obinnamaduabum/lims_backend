package com.hertfordshire.restfulapi.controller.dashboard;

import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.model.psql.OrdersModel;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.pojo.LabAccountDashboardPojo;
import com.hertfordshire.pojo.LabTestsPieChartPojo;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.test_order.TestOrderService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
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
public class PublicDashboardController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(PublicDashboardController.class.getSimpleName());

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


    @GetMapping("/default/dashboard/lab")
    public ResponseEntity<Object> labAccountDashboard() {

        ApiError apiError = null;
        LabTestsPieChartPojo labTestsPieChartPojo = new LabTestsPieChartPojo();
        int patientCount = 0;
        int institutionCount = 0;
        Long institutionNumberOfTests = 0L;
        Long patientsNumberOfTests = 0L;
        List<PortalUser> portalUserList = new ArrayList<>();

        try {

            try {

                portalUserList = this.portalUserService.findAll();

                for (PortalUser portalUser : portalUserList) {
                    Set<PortalAccount> portalAccountSet = portalUser.getPortalAccounts();
                    List<PortalAccount> portalAccountList = new ArrayList<>(portalAccountSet);
                    PortalAccount portalAccount = portalAccountList.stream().findFirst().orElse(null);

                    if (portalAccount != null) {
                        if (portalAccount.getPortalAccountType().equals(PortalAccountTypeConstant.PATIENT)) {
                            patientCount++;
                        }
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            ////////////////

            try {

                List<PortalUser> listOfPatients = new ArrayList<>();
                List<PortalUser> listOfInstitutions = new ArrayList<>();

                for (PortalUser portalUser : portalUserList) {
                    Set<PortalAccount> portalAccountSet = portalUser.getPortalAccounts();
                    List<PortalAccount> portalAccountList = new ArrayList<>(portalAccountSet);
                    PortalAccount portalAccount = portalAccountList.stream().findFirst().orElse(null);

                    if (portalAccount != null) {
                        if (portalAccount.getPortalAccountType().equals(PortalAccountTypeConstant.PATIENT)) {
                            listOfPatients.add(portalUser);
                        }
                    }
                }

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

                for (PortalUser portalUser : listOfInstitutions) {
                    List<OrdersModel> ordersModelList = this.testOrderService.findByPortalUser(portalUser);
                    labTestsPieChartPojo.setInstitutionNumberOfTests((long) ordersModelList.size());

                    for (OrdersModel ordersModel : ordersModelList) {
                        if (labTestsPieChartPojo.getInstitutionNumberOfTests() != null) {
                            labTestsPieChartPojo.setInstitutionTestsAmount(labTestsPieChartPojo.getInstitutionTestsAmount() + ordersModel.getPrice());
                        } else {
                            labTestsPieChartPojo.setInstitutionTestsAmount(String.valueOf(ordersModel.getPrice()));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //////////////

            LabAccountDashboardPojo labAccountDashboardPojo = new LabAccountDashboardPojo();
            labAccountDashboardPojo.setNumberOfInstitutes((long) patientCount);
            labAccountDashboardPojo.setNumberOfPatients((long) institutionCount);

            if (labTestsPieChartPojo.getInstitutionNumberOfTests() != null) {
                institutionNumberOfTests = labTestsPieChartPojo.getInstitutionNumberOfTests();
            }

            if (labTestsPieChartPojo.getPatientsNumberOfTests() != null) {
                patientsNumberOfTests = labTestsPieChartPojo.getPatientsNumberOfTests();
            }

            labAccountDashboardPojo.setNumberOfInstitutionTestsOrdered(institutionNumberOfTests);

            labAccountDashboardPojo.setNumberOfPatientTestsOrdered(patientsNumberOfTests);

            labAccountDashboardPojo.setTotalNumberOfLabTestsOrdered(institutionNumberOfTests + patientsNumberOfTests);


            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.creation.for.employee.successful", "en"),
                    true, new ArrayList<>(), labAccountDashboardPojo);


        } catch (Exception e) {
            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.creation.for.employee.successful", "en"),
                    false, new ArrayList<>(), null);
        }
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
