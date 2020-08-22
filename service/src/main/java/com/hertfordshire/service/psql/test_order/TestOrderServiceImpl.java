package com.hertfordshire.service.psql.test_order;

import com.google.gson.Gson;
import com.hertfordshire.dao.psql.*;
import com.hertfordshire.dto.LabTestsOrderSearchDto;
import com.hertfordshire.dto.OrderDetailsDto;
import com.hertfordshire.dto.PaymentTransactionDto;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.pojo.LabTestDetailsPojo;
import com.hertfordshire.pojo.LabTestOrderPojoReport;
import com.hertfordshire.pojo.LabTestOrdersPojo;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.pojo.report.LabTestDetailsReportPojo;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.sequence.order_details_unique_id.OrderDetailsUniqueIdSequenceService;
import com.hertfordshire.utils.PhoneNumberValidationUtil;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;
import com.hertfordshire.utils.lhenum.PaymentMethodConstant;
import com.hertfordshire.utils.pojo.ProperPhoneNumberPojo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class TestOrderServiceImpl implements TestOrderService {

    private final Logger logger = LoggerFactory.getLogger(TestOrderServiceImpl.class.getSimpleName());

    private String dateSource = "2000-09-09";

    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    private LabTestOrderDetailDao labTestOrderDetailDao;


    @Autowired
    private LabTestOrderDao labTestOrderDao;

    @Autowired
    private LabTestDao labTestDao;

    @Autowired
    private PortalUserService portalUserService;

    private Gson gson;

    @Autowired
    private OrderDetailsUniqueIdSequenceService orderDetailsUniqueIdSequenceService;

    @Autowired
    public TestOrderServiceImpl() {
        this.gson = new Gson();
    }

    @Override
    public OrdersModel findByCode(String code) {
        return this.labTestOrderDao.findByCode(code.toLowerCase());
    }

    @Override
    public Optional<OrdersModel> findByIdAndPortalUser(Long id, PortalUser portalUser) {
        return this.labTestOrderDao.findByIdAndPortalUser(id, portalUser);
    }

    @Override
    public Optional<OrdersModel> findById(Long id) {
        return this.labTestOrderDao.findById(id);
    }

    @Override
    public List<OrdersModel> findByPortalUser(PortalUser portalUser) {
        return this.labTestOrderDao.findByPortalUser(portalUser);
    }

    @Override
    public PaginationResponsePojo findByPortalUserWithPagination(PortalUser portalUser, LabTestsOrderSearchDto labTestsOrderSearchDto, Pageable pageable) {

        Date startDate = null;
        Date endDate = null;
        String code = null;


        if (labTestsOrderSearchDto == null) {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            Date date1 = null;
            try {
                date1 = simpleDateFormat.parse(dateSource);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            startDate = Utils.atStartOfDay(date1);
            Date date = new Date();
            endDate = Utils.atEndOfDay(date);

        } else {

            if (StringUtils.isNotBlank(labTestsOrderSearchDto.getCode())) {
                code = labTestsOrderSearchDto.getCode().toLowerCase().trim();
            }

            if (labTestsOrderSearchDto.getStartDate() != null) {
                startDate = Utils.atStartOfDay(labTestsOrderSearchDto.getStartDate());
            } else {

                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                Date date = null;
                try {
                    date = simpleDateFormat.parse(dateSource);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                startDate = Utils.atStartOfDay(date);
            }

            if (labTestsOrderSearchDto.getEndDate() != null) {
                endDate = Utils.atEndOfDay(labTestsOrderSearchDto.getEndDate());
            } else {
                Date date = new Date();
                endDate = Utils.atEndOfDay(date);
            }
        }

        Query query = this.entityManager.createQuery("select o" +
                " from OrdersModel o" +
                " where o.portalUser.id = :portalUserId" +
                " and (:code is null or lower(o.code) = :code)" +
                " and o.cashCollected = true"+
                " and o.dateCreated between :startDate and :endDate ORDER BY o.dateCreated DESC");

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        query.setFirstResult((pageNumber) * pageSize);
        query.setParameter("code", code);
        query.setParameter("portalUserId", portalUser.getId());
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setMaxResults(pageSize);
        this.entityManager.close();


        List<LabTestOrdersPojo> labTestOrdersPojoList = new ArrayList<>();

        List<OrdersModel> ordersModels = query.getResultList();

        PaginationResponsePojo paginationResponsePojo = new PaginationResponsePojo();

        for (int i = 0; i < ordersModels.size(); i++) {
            LabTestOrdersPojo labTestOrdersPojo = new LabTestOrdersPojo();

            if (pageNumber == 0) {
                labTestOrdersPojo.setPosition((long) (i + 1));
            } else {
                labTestOrdersPojo.setPosition((long) (i + pageSize + pageNumber));
            }

            labTestOrdersPojo.setCashCollected(ordersModels.get(i).isCashCollected());
            labTestOrdersPojo.setCode(ordersModels.get(i).getCode());
            labTestOrdersPojo.setCurrencyType(ordersModels.get(i).getCurrencyType());
            labTestOrdersPojo.setId(ordersModels.get(i).getId());
            labTestOrdersPojo.setDateCreated(ordersModels.get(i).getDateCreated());
            labTestOrdersPojo.setDateUpdated(ordersModels.get(i).getDateUpdated());

            if (ordersModels.get(i).getCurrencyType().equals(CurrencyTypeConstant.NGN)) {
                labTestOrdersPojo.setPrice(Long.valueOf(Utils.koboToNaira(ordersModels.get(i).getPrice())));
            } else {
                labTestOrdersPojo.setPrice(ordersModels.get(i).getPrice());
            }

            labTestOrdersPojoList.add(labTestOrdersPojo);

        }

        paginationResponsePojo.setDataList(labTestOrdersPojoList);
        paginationResponsePojo.setPageSize((long) pageSize);
        paginationResponsePojo.setPageNumber((long) pageNumber);
        return paginationResponsePojo;
    }

    @Override
    public Long countByPortalUserWithPagination(PortalUser portalUser, LabTestsOrderSearchDto labTestsOrderSearchDto) {

        Date startDate = null;
        Date endDate = null;
        String code = null;


        if (labTestsOrderSearchDto == null) {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            Date date1 = null;
            try {
                date1 = simpleDateFormat.parse(dateSource);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            startDate = Utils.atStartOfDay(date1);
            Date date = new Date();
            endDate = Utils.atEndOfDay(date);

        } else {

            if (StringUtils.isNotBlank(labTestsOrderSearchDto.getCode())) {
                code = labTestsOrderSearchDto.getCode().toLowerCase().trim();
            }

            if (labTestsOrderSearchDto.getStartDate() != null) {
                startDate = Utils.atStartOfDay(labTestsOrderSearchDto.getStartDate());
            } else {

                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                Date date = null;
                try {
                    date = simpleDateFormat.parse(dateSource);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                startDate = Utils.atStartOfDay(date);
            }

            if (labTestsOrderSearchDto.getEndDate() != null) {
                endDate = Utils.atEndOfDay(labTestsOrderSearchDto.getEndDate());
            } else {
                Date date = new Date();
                endDate = Utils.atEndOfDay(date);
            }
        }

        Query query = this.entityManager.createQuery("select count(o)" +
                " from OrdersModel o" +
                " where o.portalUser.id = :portalUserId" +
                " and (:code is null or lower(o.code) = :code)" +
                " and o.cashCollected = true"+
                " and o.dateCreated between :startDate and :endDate");

        query.setParameter("code", code);
        query.setParameter("portalUserId", portalUser.getId());
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        this.entityManager.close();
        int count = ((Number) query.getSingleResult()).intValue();
        return (long) count;
    }

    @Override
    public PaginationResponsePojo findByWithPagination(LabTestsOrderSearchDto labTestsOrderSearchDto, Pageable pageable) {
        Date startDate = null;
        Date endDate = null;
        String code = null;


        if (labTestsOrderSearchDto == null) {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            Date date1 = null;
            try {
                date1 = simpleDateFormat.parse(dateSource);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            startDate = Utils.atStartOfDay(date1);
            Date date = new Date();
            endDate = Utils.atEndOfDay(date);

        } else {

            if (StringUtils.isNotBlank(labTestsOrderSearchDto.getCode())) {
                code = labTestsOrderSearchDto.getCode().toLowerCase().trim();
            }

            if (labTestsOrderSearchDto.getStartDate() != null) {
                startDate = Utils.atStartOfDay(labTestsOrderSearchDto.getStartDate());
            } else {

                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                Date date = null;
                try {
                    date = simpleDateFormat.parse(dateSource);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                startDate = Utils.atStartOfDay(date);
            }

            if (labTestsOrderSearchDto.getEndDate() != null) {
                endDate = Utils.atEndOfDay(labTestsOrderSearchDto.getEndDate());
            } else {
                Date date = new Date();
                endDate = Utils.atEndOfDay(date);
            }
        }

        Query query = this.entityManager.createQuery("select o" +
                " from OrdersModel o" +
                " where (:code is null or lower(o.code) = :code)" +
                " and o.dateCreated between :startDate and :endDate ORDER BY o.dateCreated DESC");

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        query.setFirstResult((pageNumber) * pageSize);
        query.setParameter("code", code);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setMaxResults(pageSize);

        this.entityManager.close();


        List<LabTestOrdersPojo> labTestOrdersPojoList = new ArrayList<>();

        List<OrdersModel> ordersModels = query.getResultList();

        PaginationResponsePojo paginationResponsePojo = new PaginationResponsePojo();

        for (int i = 0; i < ordersModels.size(); i++) {
            LabTestOrdersPojo labTestOrdersPojo = new LabTestOrdersPojo();

            if (pageNumber == 0) {
                labTestOrdersPojo.setPosition((long) (i + 1));
            } else {
                labTestOrdersPojo.setPosition((long) (i + pageSize + pageNumber));
            }

            labTestOrdersPojo.setCashCollected(ordersModels.get(i).isCashCollected());
            labTestOrdersPojo.setCode(ordersModels.get(i).getCode());
            labTestOrdersPojo.setCurrencyType(ordersModels.get(i).getCurrencyType());
            labTestOrdersPojo.setId(ordersModels.get(i).getId());
            labTestOrdersPojo.setDateCreated(ordersModels.get(i).getDateCreated());
            labTestOrdersPojo.setDateUpdated(ordersModels.get(i).getDateUpdated());

            if (ordersModels.get(i).getCurrencyType().equals(CurrencyTypeConstant.NGN)) {
                labTestOrdersPojo.setPrice(Long.valueOf(Utils.koboToNaira(ordersModels.get(i).getPrice())));
            } else {
                labTestOrdersPojo.setPrice(ordersModels.get(i).getPrice());
            }

            try {

                PortalUser portalUser = this.portalUserService.findPortalUserById(ordersModels.get(i).getPortalUser().getId());

                if (portalUser != null) {
                    PortalAccount portalAccount = portalUser.getPortalAccounts().stream().findFirst().orElse(null);
                    if (portalAccount != null) {
                        labTestOrdersPojo.setOrderedByWhatTypeOfAccount(portalAccount.getPortalAccountType().name());
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            labTestOrdersPojoList.add(labTestOrdersPojo);

        }

        paginationResponsePojo.setDataList(labTestOrdersPojoList);
        paginationResponsePojo.setPageSize((long) pageSize);
        paginationResponsePojo.setPageNumber((long) pageNumber);
        return paginationResponsePojo;
    }

    @Override
    public Long countByWithPagination(LabTestsOrderSearchDto labTestsOrderSearchDto) {

        Date startDate = null;
        Date endDate = null;
        String code = null;


        if (labTestsOrderSearchDto == null) {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            Date date1 = null;
            try {
                date1 = simpleDateFormat.parse(dateSource);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            startDate = Utils.atStartOfDay(date1);
            Date date = new Date();
            endDate = Utils.atEndOfDay(date);

        } else {

            if (StringUtils.isNotBlank(labTestsOrderSearchDto.getCode())) {
                code = labTestsOrderSearchDto.getCode().toLowerCase().trim();
            }

            if (labTestsOrderSearchDto.getStartDate() != null) {
                startDate = Utils.atStartOfDay(labTestsOrderSearchDto.getStartDate());
            } else {

                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                Date date = null;
                try {
                    date = simpleDateFormat.parse(dateSource);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                startDate = Utils.atStartOfDay(date);
            }

            if (labTestsOrderSearchDto.getEndDate() != null) {
                endDate = Utils.atEndOfDay(labTestsOrderSearchDto.getEndDate());
            } else {
                Date date = new Date();
                endDate = Utils.atEndOfDay(date);
            }
        }

        Query query = this.entityManager.createQuery("select count(o)" +
                " from OrdersModel o" +
                " where (:code is null or lower(o.code) = :code)" +
                " and o.dateCreated between :startDate and :endDate");

        query.setParameter("code", code);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        this.entityManager.close();
        int count = ((Number) query.getSingleResult()).intValue();
        return (long) count;
    }

    @Override
    public List<Map<String, Object>> generateReceipt(LabTestOrdersPojo labTestOrdersPojo) {

        List<Map<String, Object>> labTestOrderReportHashMapArray = new ArrayList<Map<String, Object>>();
        // List<Map<String, Object>> resultLabTestDetailsHashMapArray = new ArrayList<Map<String, Object>>();

        LabTestOrderPojoReport labTestOrderPojoReport = new LabTestOrderPojoReport();

        labTestOrderPojoReport.setCode(labTestOrdersPojo.getCode());
        labTestOrderPojoReport.setLastName(labTestOrdersPojo.getPortalUserPojo().getLastName());
        labTestOrderPojoReport.setFirstName(labTestOrdersPojo.getPortalUserPojo().getFirstName());
        labTestOrderPojoReport.setCurrencyType(labTestOrdersPojo.getCurrencyType().toString());
        labTestOrderPojoReport.setPhoneNumber(labTestOrdersPojo.getPortalUserPojo().getPhoneNumber());
        labTestOrderPojoReport.setPrice(NumberFormat.getNumberInstance(Locale.US).format(labTestOrdersPojo.getPrice()));
        labTestOrderPojoReport.setDateCreated(this.convertDateToHumanReadableDate(labTestOrdersPojo.getDateCreated()));

        labTestOrderPojoReport.setDateUpdated(this.convertDateToHumanReadableDate(labTestOrdersPojo.getDateUpdated()));


        List<LabTestDetailsPojo> labTestDetailsPojoList = labTestOrdersPojo.getLabTestDetailsPojos();

        List<LabTestDetailsReportPojo> labTestDetailsReportPojoList = new ArrayList<>();
        for (LabTestDetailsPojo labTestDetailsPojo : labTestDetailsPojoList) {
            LabTestDetailsReportPojo labTestDetailsReportPojo = new LabTestDetailsReportPojo();
            labTestDetailsReportPojo.setName(labTestDetailsPojo.getName());
            labTestDetailsReportPojo.setQuantity(String.valueOf(labTestDetailsPojo.getQuantity()));
            labTestDetailsReportPojo.setPrice(NumberFormat.getNumberInstance(Locale.US).format(labTestDetailsPojo.getPrice()));
            labTestDetailsReportPojo.setTotal(NumberFormat.getNumberInstance(Locale.US).format(labTestDetailsPojo.getTotal()));
            labTestDetailsReportPojoList.add(labTestDetailsReportPojo);
        }

        labTestOrderPojoReport.setLabTestDetailsReportPojoList(labTestDetailsReportPojoList);

        if (labTestOrdersPojo.isCashCollected()) {
            labTestOrderPojoReport.setCashCollected("YES");
        } else {
            labTestOrderPojoReport.setCashCollected("NO");
        }

        //// conversion

        Map<String, Object> labTestOrderReportHashMap = new HashMap<>();
        labTestOrderReportHashMap.put("dateCreated", labTestOrderPojoReport.getDateCreated());
        labTestOrderReportHashMap.put("dateUpdated", labTestOrderPojoReport.getDateUpdated());
        labTestOrderReportHashMap.put("price", labTestOrderPojoReport.getPrice());
        labTestOrderReportHashMap.put("code", labTestOrderPojoReport.getCode());
        labTestOrderReportHashMap.put("lastName", labTestOrderPojoReport.getLastName());
        labTestOrderReportHashMap.put("firstName", labTestOrderPojoReport.getFirstName());
        labTestOrderReportHashMap.put("currencyType", labTestOrderPojoReport.getCurrencyType());
        labTestOrderReportHashMap.put("phoneNumber", labTestOrderPojoReport.getPhoneNumber());
        labTestOrderReportHashMap.put("orderId", labTestOrderPojoReport.getCode());
        labTestOrderReportHashMap.put("cashCollected", labTestOrderPojoReport.getCashCollected());
        labTestOrderReportHashMap.put("labTestDetailsDataSource", labTestOrderPojoReport.getLabTestDetailsDataSource());
        labTestOrderReportHashMapArray.add(labTestOrderReportHashMap);
        return labTestOrderReportHashMapArray;
    }

    @Transactional
    @Override
    public OrdersModel create(PaymentTransaction paymentTransaction, PaymentTransactionDto paymentTransactionDto, PortalUser portalUser) {

        OrdersModel ordersModel = new OrdersModel();

        if (CurrencyTypeConstant.valueOf(paymentTransactionDto.getCurrencyTypeConstant()).equals(CurrencyTypeConstant.NGN)) {
            ordersModel.setPrice(Utils.nairaToKobo(paymentTransactionDto.getAmount()));
        } else if (CurrencyTypeConstant.valueOf(paymentTransactionDto.getCurrencyTypeConstant()).equals(CurrencyTypeConstant.USD)) {
            ordersModel.setPrice(Long.valueOf(paymentTransactionDto.getAmount()));
        } else if (CurrencyTypeConstant.valueOf(paymentTransactionDto.getCurrencyTypeConstant()).equals(CurrencyTypeConstant.EUR)) {
            ordersModel.setPrice(Long.valueOf(paymentTransactionDto.getAmount()));
        }

        if (!PaymentMethodConstant.valueOf(paymentTransactionDto.getPaymentMethodConstant()).equals(PaymentMethodConstant.CASH)) {
            ordersModel.setCashCollected(true);
        }

        ordersModel.setCurrencyType(CurrencyTypeConstant.valueOf(paymentTransactionDto.getCurrencyTypeConstant()));


        List<LabTest> labTestList = new ArrayList<>();


        if (paymentTransactionDto.getListOfTestsSelected() != null) {
            for (OrderDetailsDto orderDetailsDto : paymentTransactionDto.getListOfTestsSelected()) {
                Optional<LabTest> optionalLabTest = this.labTestDao.findById(orderDetailsDto.getId());
                optionalLabTest.ifPresent(labTestList::add);
            }
        }

        Set labTestSet = new HashSet(labTestList);
        ordersModel.setLabTests(labTestSet);
        ordersModel.setCode(paymentTransactionDto.getTransactionRef());
        ordersModel.setPaymentTransactionModel(paymentTransaction);
        ordersModel.setPortalUser(portalUser);

        ordersModel = this.labTestOrderDao.save(ordersModel);

        if (paymentTransactionDto.getListOfTestsSelected() != null) {
            for (OrderDetailsDto orderDetailsDto : paymentTransactionDto.getListOfTestsSelected()) {
                Optional<LabTest> optionalLabTest = this.labTestDao.findById(orderDetailsDto.getId());


                String orderDetailsUniqueId = String.format("ODU_%04d%02d%02d%05d",
                        LocalDate.now().getYear(),
                        LocalDate.now().getMonthValue(),
                        LocalDate.now().getDayOfMonth(),
                        orderDetailsUniqueIdSequenceService.getNextId()
                );

                if (optionalLabTest.isPresent()) {
                    LabTest labTest = optionalLabTest.get();
                    LabTestOrderDetail labTestOrderDetail = new LabTestOrderDetail();
                    labTestOrderDetail.setLabTest(labTest);
                    labTestOrderDetail.setUniqueId(orderDetailsUniqueId);
                    labTestOrderDetail.setOrdersModel(ordersModel);
                    labTestOrderDetail.setName(labTest.getName());
                    labTestOrderDetail.setQuantity(Long.valueOf(orderDetailsDto.getQuantity()));

                    if(portalUser != null) {
                        labTestOrderDetail.setPatient(portalUser);
                    }

                    if (CurrencyTypeConstant.valueOf(paymentTransactionDto.getCurrencyTypeConstant()).equals(CurrencyTypeConstant.NGN)) {
                        labTestOrderDetail.setPrice(Utils.nairaToKobo(orderDetailsDto.getPrice()));
                        labTestOrderDetail.setTotal(Utils.nairaToKobo(orderDetailsDto.getTotal()));
                    } else if (CurrencyTypeConstant.valueOf(paymentTransactionDto.getCurrencyTypeConstant()).equals(CurrencyTypeConstant.USD)) {
                        labTestOrderDetail.setPrice(Long.valueOf(orderDetailsDto.getPrice()));
                        labTestOrderDetail.setTotal(Long.valueOf(orderDetailsDto.getTotal()));
                    } else if (CurrencyTypeConstant.valueOf(paymentTransactionDto.getCurrencyTypeConstant()).equals(CurrencyTypeConstant.EUR)) {
                        labTestOrderDetail.setPrice(Long.valueOf(orderDetailsDto.getPrice()));
                        labTestOrderDetail.setTotal(Long.valueOf(orderDetailsDto.getTotal()));
                    }

                    this.labTestOrderDetailDao.save(labTestOrderDetail);

                    optionalLabTest.ifPresent(labTestList::add);
                }
            }
        }

        return ordersModel;
    }

    @Override
    public Number countNumberOfOrdersForLoggedInUser(PortalUser portalUser) {
        return this.labTestOrderDao.countByPortalUser(portalUser);
    }


    private String convertDateToHumanReadableDate(Date date) {

        String dateString = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
            dateString = sdf.format(date); // formats to 09/23/2009 13:53:28.238
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateString;
    }
}

