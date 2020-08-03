package com.hertfordshire.restfulapi.controller.report;

import com.google.gson.Gson;
import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.dao.psql.LabTestOrderDetailDao;
import com.hertfordshire.model.psql.LabTestOrderDetail;
import com.hertfordshire.model.psql.OrdersModel;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.pojo.LabTestDetailsPojo;
import com.hertfordshire.pojo.LabTestOrdersPojo;
import com.hertfordshire.pojo.PortalUserPojo;
import com.hertfordshire.pubsub.redis.model.PortalUserModel;
import com.hertfordshire.restfulapi.service.jasper.JasperReportService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.test_order.TestOrderService;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;
import com.hertfordshire.utils.lhenum.DocumentTypesConstant;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class ReportController extends ProtectedBaseApiController {

    private Logger logger = LoggerFactory.getLogger(ReportController.class);

    private Gson gson;

    @Autowired
    private JasperReportService jasperReportService;

    @Autowired
    private LabTestOrderDetailDao labTestOrderDetailDao;

    @Autowired
    private TestOrderService orderIdService;

    @Autowired
    private UserService userService;

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private com.hertfordshire.pubsub.redis.service.portal_user.PortalUserService redisPortalUserService;

    public ReportController() {
        this.gson = new Gson();
    }


    @PostMapping("/default/report/patient/receipt/{id}")
    public ResponseEntity<byte[]> generatePatientReceipt(@PathVariable Long id,
                                                         HttpServletResponse response,
                                                         HttpServletRequest request,
                                                         Authentication authentication,
                                                         @RequestParam("doc_type") String docType) {
        String application = "";
        String extension = "";
        PortalUser portalUser = null;
        UserDetailsDto requestPrincipal = null;
        Long portalUserId = 0L;
        PortalUserModel portalUserModel = null;
        DocumentTypesConstant documentTypesConstant;
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
                            logger.info("unauthenticated");
                            // return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            portalUser = portalUserService.findPortalUserById(portalUserId);

            logger.info("id: " + id);
            Optional<OrdersModel> ordersModelOptional = this.orderIdService.findByIdAndPortalUser(id, portalUser);


            //this.logger.info(this.gson.toJson(labTestOrdersPojo));

            ByteArrayOutputStream baos = this.fetchOrderByteArray(ordersModelOptional, portalUser, docType, application, extension);
            if (baos != null) {


                if (StringUtils.isNotBlank(docType)) {

                    documentTypesConstant = DocumentTypesConstant.valueOf(docType.toUpperCase());
                    if (documentTypesConstant.equals(DocumentTypesConstant.PDF)) {

                        application = "pdf";
                        extension = "pdf";


                    } else if (documentTypesConstant.equals(DocumentTypesConstant.EXCEL)) {

                        application = "vnd.ms-excel";
                        extension = "xls";
                    }
                }

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                String fileName = String.format("RET%04d%02d%02d%05d",
                        LocalDate.now().getYear(),
                        LocalDate.now().getMonthValue(),
                        LocalDate.now().getDayOfMonth(),
                        timestamp.getTime());

                return ResponseEntity
                        .ok()
                        .header("Content-Type", "application/" + application + "; charset=UTF-8")
                        .header("Content-Disposition", "inline; filename=\"" + fileName + "." + extension + "\"")
                        .body(baos.toByteArray());
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @PostMapping("/default/report/patient/receipt/for-admin/{id}")
    @PreAuthorize("hasAnyAuthority('RECEPTIONIST','MEDICAL_LAB_SCIENTIST', 'DOCTOR', 'PATHOLOGIST' ,'SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<byte[]> generatePatientReceiptForAdmin(@PathVariable Long id,
                                                                 HttpServletResponse response,
                                                                 HttpServletRequest request,
                                                                 Authentication authentication,
                                                                 @RequestParam("doc_type") String docType) {
        String application = "";
        String extension = "";
        PortalUser portalUser = null;
        DocumentTypesConstant documentTypesConstant;
        try {


            // PortalUser portalUser = portalUserService.findPortalUserByEmail(email);

            // logger.info("id: " + id);
            Optional<OrdersModel> ordersModelOptional = this.orderIdService.findById(id);
            //this.logger.info(this.gson.toJson(labTestOrdersPojo));

            ByteArrayOutputStream baos = this.fetchOrderByteArray(ordersModelOptional, portalUser, docType, application, extension);
            if (baos != null) {

                if (StringUtils.isNotBlank(docType)) {

                    documentTypesConstant = DocumentTypesConstant.valueOf(docType.toUpperCase());
                    if (documentTypesConstant.equals(DocumentTypesConstant.PDF)) {

                        application = "pdf";
                        extension = "pdf";


                    } else if (documentTypesConstant.equals(DocumentTypesConstant.EXCEL)) {

                        application = "vnd.ms-excel";
                        extension = "xls";
                    }
                }

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                String fileName = String.format("RET%04d%02d%02d%05d",
                        LocalDate.now().getYear(),
                        LocalDate.now().getMonthValue(),
                        LocalDate.now().getDayOfMonth(),
                        timestamp.getTime());

                return ResponseEntity
                        .ok()
                        .header("Content-Type", "application/" + application + "; charset=UTF-8")
                        .header("Content-Disposition", "inline; filename=\"" + fileName + "." + extension + "\"")
                        .body(baos.toByteArray());
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private ByteArrayOutputStream fetchOrderByteArray(Optional<OrdersModel> ordersModelOptional, PortalUser portalUser, String docType, String application, String extension) {
        if (ordersModelOptional.isPresent()) {
            OrdersModel ordersModel = ordersModelOptional.get();

            portalUser = this.portalUserService.findPortalUserById(ordersModel.getPortalUser().getId());


            logger.info(this.gson.toJson(ordersModel.getCode()));

            PortalUserPojo portalUserPojo = new PortalUserPojo();
            portalUserPojo.setFirstName(portalUser.getFirstName());
            portalUserPojo.setLastName(portalUser.getLastName());
            portalUserPojo.setCode(portalUser.getCode());
            portalUserPojo.setOtherName(portalUser.getOtherName());
            portalUserPojo.setPhoneNumber(portalUser.getPhoneNumber());

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


                labTestDetailsPojos.add(labTestDetailsPojo);
            }

            labTestOrdersPojo.setLabTestDetailsPojos(labTestDetailsPojos);

            /////////// receipt starts
            DocumentTypesConstant documentTypesConstant;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

                if (StringUtils.isNotBlank(docType)) {

                    documentTypesConstant = DocumentTypesConstant.valueOf(docType.toUpperCase());

                    // List<InvestorTransactionAdminResponseDto> investorTransactionAdminResponseDtoList = this.investorTransactionService.generateReport(investorTransactionSearchQueryDto);

                    List<Map<String, Object>> list = this.orderIdService.generateReceipt(labTestOrdersPojo);

                    JasperPrint jasperPrint = jasperReportService.generatePDFReportForASingleElementInArray("patient_receipt", list);

                    if (documentTypesConstant.equals(DocumentTypesConstant.PDF)) {

                        SimpleOutputStreamExporterOutput simpleOutputStreamExporterOutput = new SimpleOutputStreamExporterOutput(baos);

                        JRPdfExporter pdfExporter = new JRPdfExporter();
                        pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                        pdfExporter.setExporterOutput(simpleOutputStreamExporterOutput);

                        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                        configuration.setCreatingBatchModeBookmarks(true);
                        pdfExporter.setConfiguration(configuration);

                        logger.info("pdf");

                        try {
                            pdfExporter.exportReport();
                        } catch (JRException e) {
                            e.printStackTrace();
                        }

                    } else if (documentTypesConstant.equals(DocumentTypesConstant.EXCEL)) {

                        JRXlsExporter xlsExporter = new JRXlsExporter();

                        xlsExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                        xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));

                        SimpleXlsReportConfiguration xlsReportConfiguration = new SimpleXlsReportConfiguration();
                        xlsReportConfiguration.setOnePagePerSheet(false);
                        xlsReportConfiguration.setRemoveEmptySpaceBetweenRows(true);
                        xlsReportConfiguration.setDetectCellType(false);
                        xlsReportConfiguration.setWhitePageBackground(false);
                        xlsExporter.setConfiguration(xlsReportConfiguration);

                        logger.info("excel");
                        try {
                            xlsExporter.exportReport();
                        } catch (JRException e) {
                            e.printStackTrace();
                        }
                    }


                    return baos;


                } else {
                    return null;
                }

        }

        return null;
    }
}
