package com.hertfordshire.service.psql.lab_test_order_details;

import com.google.gson.Gson;
import com.hertfordshire.dao.psql.LabScientistTestResultDao;
import com.hertfordshire.dao.psql.LabTestDao;
import com.hertfordshire.dao.psql.LabTestOrderDetailDao;
import com.hertfordshire.dao.psql.SampleCollectedDao;
import com.hertfordshire.dto.OrderedLabTestSearchDto;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.pojo.*;
import com.hertfordshire.service.psql.portalaccount.PortalAccountService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.test_order.TestOrderService;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.lhenum.LabScientistStatusConstant;
import com.hertfordshire.utils.lhenum.SampleTypeConstant;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LabTestOrderDetailsServiceImpl implements LabTestOrderDetailsService {

    private final Logger logger = LoggerFactory.getLogger(LabTestOrderDetailsServiceImpl.class.getSimpleName());

    @Autowired
    private TestOrderService testOrderService;

    @Autowired
    private LabTestOrderDetailDao labTestOrderDetailDao;

    @Autowired
    private LabTestDao labTestDao;


    private Gson gson;

    @Autowired
    private SampleCollectedDao sampleCollectedDao;

    @Autowired
    private LabScientistTestResultDao labScientistTestResultDao;

    @Autowired
    private PortalAccountService portalAccountService;

    @Autowired
    private PortalUserService portalUserService;

    @PersistenceContext
    private EntityManager entityManager;

    private String dateSource = "2000-09-09";

    public LabTestOrderDetailsServiceImpl() {
        gson = new Gson();
    }

    @Override
    public SampleCollectedModel findSampleCollectedById(Long id) {
        Optional<SampleCollectedModel> optionalSampleCollectedModel = this.sampleCollectedDao.findById(id);
        return optionalSampleCollectedModel.orElse(null);
    }

    @Override
    public List<LabTestOrderDetail> findByOrdersModelAndLabTest(OrdersModel ordersModel, LabTest labTest) {
        return this.labTestOrderDetailDao.findByOrdersModelAndLabTest(ordersModel, labTest);
    }

    @Override
    public List<LabTestOrderDetail> findByOrdersModel(OrdersModel ordersModel) {
        return this.labTestOrderDetailDao.findByOrdersModel(ordersModel);
    }

    @Transactional
    @Override
    public SampleCollectedModel updateSampleCollectionStatus(PortalUser portalUser, LabTestOrderDetail labTestOrderDetail) {
        SampleCollectedModel sampleCollectedModel = new SampleCollectedModel();
        sampleCollectedModel.setCollectedBy(portalUser);
        sampleCollectedModel.setSampleCollected(SampleTypeConstant.SAMPLE_COLLECTED);
        SampleCollectedModel newlyCreatedSampleCollection =
        this.sampleCollectedDao.save(sampleCollectedModel);

        labTestOrderDetail.setSampleCollected(newlyCreatedSampleCollection);
        this.labTestOrderDetailDao.save(labTestOrderDetail);


        LabScientistTestResultModel labScientistTestResultModel =
                returnLabScientistTestResultModel(newlyCreatedSampleCollection);



        Optional<LabTest> optionalLabTest = this.labTestDao.findById(labTestOrderDetail.getLabTest().getId());
        optionalLabTest.ifPresent(labScientistTestResultModel::setLabTest);
        this.labScientistTestResultDao.save(labScientistTestResultModel);

        return newlyCreatedSampleCollection;
    }


    private LabScientistTestResultModel returnLabScientistTestResultModel(SampleCollectedModel newlyCreatedSampleCollection) {
        LabScientistTestResultModel labScientistTestResultModel = new LabScientistTestResultModel();
        labScientistTestResultModel.setMedicalLabScientist(null);
        labScientistTestResultModel.setLabScientistStatusConstant(LabScientistStatusConstant.PENDING);
        labScientistTestResultModel.setSampleCollectedModel(newlyCreatedSampleCollection);
        return labScientistTestResultModel;
    }

    @Transactional
    @Override
    public boolean updateSampleCollectionDbWithDataOnceCashIsCollected(PortalUser portalUser, OrdersModel ordersModel) {

        try {
            // List<SampleCollectedModel> sampleCollectedModelList = new ArrayList<>();

            List<LabTestOrderDetail> labTestOrderDetailList = this.labTestOrderDetailDao.findByOrdersModel(ordersModel);

            // List<LabTestOrderDetail> newLabTestOrderDetailList = new ArrayList<>();

            for (LabTestOrderDetail labTestOrderDetail : labTestOrderDetailList) {
                SampleCollectedModel sampleCollectedModel = new SampleCollectedModel();
                sampleCollectedModel.setCollectedBy(portalUser);
                sampleCollectedModel.setLabTestOrderDetail(labTestOrderDetail);
                sampleCollectedModel.setSampleCollected(SampleTypeConstant.SAMPLE_NOT_COLLECTED);
                //sampleCollectedModelList.add(sampleCollectedModel);
                SampleCollectedModel newlySampleCollectedModel = this.sampleCollectedDao.save(sampleCollectedModel);
                labTestOrderDetail.setSampleCollected(newlySampleCollectedModel);
                this.labTestOrderDetailDao.save(labTestOrderDetail);
            }
            // this.sampleCollectedDao.saveAll(sampleCollectedModelList);


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public SampleCollectedModel findSampleCollectionStatusByPortalUserAndLabTestOrderDetail(PortalUser portalUser, LabTestOrderDetail labTestOrderDetail) {

        if (labTestOrderDetail.getSampleCollected() != null) {
            Long sampleCollectedId = labTestOrderDetail.getSampleCollected().getId();
            if (sampleCollectedId != 0L) {
                Optional<SampleCollectedModel> optionalSampleCollectedModel =
                        this.sampleCollectedDao.findById(labTestOrderDetail.getSampleCollected().getId());
                return optionalSampleCollectedModel.orElse(null);
            } else {
                return null;
            }
        }

        return null;
    }


    @Transactional
    @Override
    public SampleCollectedModel updateSampleCollectionStatus(LabTestOrderDetail labTestOrderDetail, PortalUser collectedBy) {

        SampleCollectedModel sampleCollectedModel = this.sampleCollectedDao.findByLabTestOrderDetail(labTestOrderDetail);

        if (sampleCollectedModel != null) {
            sampleCollectedModel.setSampleCollected(SampleTypeConstant.SAMPLE_COLLECTED);
            sampleCollectedModel.setCollectedBy(collectedBy);
            sampleCollectedModel.setDateUpdated(new Date());
            SampleCollectedModel newlySavedSampleCollection = this.sampleCollectedDao.save(sampleCollectedModel);

            LabScientistTestResultModel labScientistTestResultModel =
                    returnLabScientistTestResultModel(newlySavedSampleCollection);


            Optional<LabTest> optionalLabTest = this.labTestDao.findById(labTestOrderDetail.getLabTest().getId());
            if(optionalLabTest.isPresent()) {
                LabTest labTest = optionalLabTest.get();
                labScientistTestResultModel.setLabTest(labTest);
                labScientistTestResultModel.setLabResultId(labTest.getResultTemplateId());
                this.labScientistTestResultDao.save(labScientistTestResultModel);
            } else {
                throw new RuntimeException();
            }


            return newlySavedSampleCollection;
        }

        return null;
    }

    @Override
    public LabTestOrderDetail findByOrdersModelAndLabTestAndUniqueId(OrdersModel ordersModel, LabTest labTest, String uniqueId) {
        return this.labTestOrderDetailDao.findByOrdersModelAndLabTestAndUniqueId(ordersModel, labTest, uniqueId.toUpperCase());
    }

    @Override
    public PaginationResponsePojo findAllByLabTestsOrdered(OrderedLabTestSearchDto orderedLabTestSearchDto, Pageable pageable) {

        InternalSearchResponsePojo internalSearchResponsePojo = responseInternalSearchResponsePojo(orderedLabTestSearchDto, pageable);

        //List<PortalUser> portalUserList = this.portalUserDao.findByDefaultPortalAccountCode(lookUpPortalAccount.getCode().toLowerCase(), email, fullName, startDate, endDate, pageable);

        Query query = this.entityManager.createQuery(
                "select distinct "
                        + "la, "
                        + "p, "
                        + "s, "
                        + "o"
                        + ", "
                        + "pui"
                        + " from LabTestOrderDetail as la"
                        + " LEFT JOIN OrdersModel o ON o.id = la.ordersModel.id"
                        + " LEFT JOIN PortalUser p ON ((p.id = la.patient.id) or p.id is null)"
                        + " LEFT JOIN PortalUserInstitutionLabTestOrderDetail pui"
                        + " ON ((pui.id = la.portalUserInstitutionLabTestOrderDetail.id) or pui.id is null)"
                        + " LEFT JOIN SampleCollectedModel s ON s.id = la.sampleCollected.id"

                        + " where o.cashCollected = true"
                        + " and (:orderId is null or lower(o.code) = :orderId)"

                        + " and (:sampleCollectedStatus is null or s.sampleCollected = :sampleCollectedStatus)"
                        + " and (:code is null or lower(la.uniqueId) = :code)"

                        //p
                        +" and (:email is null or lower(p.email) like :email)"
                        + " and (("
                        + "(:phoneNumber is null or lower(p.phoneNumber) like :phoneNumber)"
                        +" and (:fullName is null or lower(p.firstName) like :fullName)"
                        +" or (:fullName is null or lower(p.lastName) like :fullName)"
                        +" or (:fullName is null or lower(p.otherName) like :fullName)"
                        +")"

                        //// pui
                        + " or ((:phoneNumber is null or lower(pui.phoneNumber) like :phoneNumber)"
                        + " and ((:fullName is null or lower(pui.firstName) like :fullName)" +
                        " or (:fullName is null or lower(pui.lastName) like :fullName)"
                        + " or (:fullName is null or lower(pui.otherName) like :fullName))"
                        +"))"
                        + " and s.dateUpdated between :startDate and :endDate order by s.dateUpdated desc");

        query.setFirstResult((internalSearchResponsePojo.getPageNumber()) * internalSearchResponsePojo.getPageSize());

        myQueryBuilder(query, internalSearchResponsePojo);

        this.entityManager.close();

//        logger.info(this.gson.toJson(query.getResultList().size()));


        List<Object[]> rows = query.getResultList();

        PaginationResponsePojo paginationResponsePojo = new PaginationResponsePojo();
//
        List<LabTestsOrderedPojo> orderedPojos = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
//
            LabTestsOrderedPojo labTestsOrderedPojo = new LabTestsOrderedPojo();
//            query.getResultList() portalUserResponsePojo = new PortalUserResponsePojo();
//
            if (internalSearchResponsePojo.getPageNumber() == 0) {
                labTestsOrderedPojo.setPosition((long) (i + 1));
            } else {
                labTestsOrderedPojo.setPosition((long) (i +
                        internalSearchResponsePojo.getPageSize() +
                        internalSearchResponsePojo.getPageNumber()));
            }

            LabTestPojo labTestPojo = new LabTestPojo();

            LabTestOrderDetail labTestOrderDetail = (LabTestOrderDetail) rows.get(i)[0];

            if (labTestOrderDetail != null) {
                labTestPojo.setName(labTestOrderDetail.getName());
                labTestPojo.setId(labTestOrderDetail.getLabTest().getId());
                labTestsOrderedPojo.setUniqueId(labTestOrderDetail.getUniqueId());
                labTestsOrderedPojo.setDateCreated(labTestOrderDetail.getDateCreated());
                labTestsOrderedPojo.setDateUpdated(labTestOrderDetail.getDateUpdated());


                try {

                    OrdersModel order = (OrdersModel) rows.get(i)[3];

                    labTestsOrderedPojo.setOrderId(order.getId());

                    PortalUser portalUser =
                            this.portalUserService.findPortalUserById(order.getPortalUser().getId());

                    PortalAccount portalAccount =
                            portalUser.getPortalAccounts().stream().findFirst().orElse(null);

                    if (portalAccount != null) {
                        labTestsOrderedPojo.setAccountType(
                                portalAccount.getPortalAccountType().name());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                labTestsOrderedPojo.setLabTest(labTestPojo);
            }

            //portal user
            PortalUser patient = (PortalUser) rows.get(i)[1];

            if (patient != null) {
                labTestsOrderedPojo.setRegisteredPatient(true);
                PortalUserPojo portalUserPojo = new PortalUserPojo();
                portalUserPojo.setCode(patient.getCode());
                portalUserPojo.setFirstName(patient.getFirstName());
                portalUserPojo.setLastName(patient.getLastName());
                portalUserPojo.setOtherName(patient.getOtherName());
                portalUserPojo.setPhoneNumber(patient.getPhoneNumber());
                labTestsOrderedPojo.setPatient(portalUserPojo);
            }

//
            PortalUserInstitutionLabTestOrderDetail unregisteredPatient =
                    (PortalUserInstitutionLabTestOrderDetail) rows.get(i)[4];


            if (unregisteredPatient != null) {
                PortalUserPojo portalUserPojo = new PortalUserPojo();
                portalUserPojo.setFirstName(unregisteredPatient.getFirstName());
                portalUserPojo.setLastName(unregisteredPatient.getLastName());
                portalUserPojo.setOtherName(unregisteredPatient.getOtherName());
                portalUserPojo.setPhoneNumber(unregisteredPatient.getPhoneNumber());
                labTestsOrderedPojo.setPatient(portalUserPojo);
            }


            SampleCollectedModel sampleCollectedModel = (SampleCollectedModel) rows.get(i)[2];
            SampleCollectionPojo sampleCollectionPojo = new SampleCollectionPojo();

            //PortalUser collectedBy = this.portalUserService.findPortalUserById(sampleCollectedModel.getCollectedBy().getId());


            if (sampleCollectedModel != null) {
                sampleCollectionPojo.setSampleCollected(sampleCollectedModel.getSampleCollected().name().toUpperCase());

                if (sampleCollectedModel.getCollectedBy() != null) {
                    if (sampleCollectedModel.getCollectedBy().getId() != null) {
                        sampleCollectionPojo.setCollectedBy(sampleCollectedModel.getCollectedBy().getId());
                    }
                }

                if (sampleCollectedModel.getDateCreated() != null) {
                    sampleCollectionPojo.setDateCreated(sampleCollectedModel.getDateCreated());
                }

                if (sampleCollectedModel.getDateUpdated() != null) {
                    sampleCollectionPojo.setDateUpdated(sampleCollectedModel.getDateUpdated());
                }

                labTestsOrderedPojo.setSampleCollected(sampleCollectionPojo);
            }

            orderedPojos.add(labTestsOrderedPojo);
        }

        paginationResponsePojo.setDataList(orderedPojos);
        paginationResponsePojo.setPageNumber((long) internalSearchResponsePojo.getPageNumber());
        paginationResponsePojo.setPageSize((long) internalSearchResponsePojo.getPageSize());

//        logger.info(this.gson.toJson(paginationResponsePojo));
        return paginationResponsePojo;
    }

    @Override
    public Long countAllByLabTestOrdered(OrderedLabTestSearchDto orderedLabTestSearchDto) {

        //List<PortalUser> portalUserList = this.portalUserDao.findByDefaultPortalAccountCode(lookUpPortalAccount.getCode().toLowerCase(), email, fullName, startDate, endDate, pageable);

        InternalSearchResponsePojo internalSearchResponsePojo = responseInternalSearchResponsePojo(orderedLabTestSearchDto, null);

        Query query = this.entityManager.createQuery(
                "select distinct count(la) from LabTestOrderDetail as la"
                        + " LEFT JOIN OrdersModel o ON o.id = la.ordersModel.id"
                        + " LEFT JOIN PortalUser p ON p.id = la.patient.id or p.id is null"
                        + " LEFT JOIN PortalUserInstitutionLabTestOrderDetail pui ON pui.id = la.portalUserInstitutionLabTestOrderDetail.id or pui.id is null"
                        + " LEFT JOIN SampleCollectedModel s ON  s.id = la.sampleCollected.id"
                        + " where o.cashCollected = true"
                        + " and (:orderId is null or lower(o.code) = :orderId)"

                        + " and (:sampleCollectedStatus is null or s.sampleCollected = :sampleCollectedStatus)"
//
                        + " and (:code is null or lower(la.uniqueId) = :code)"

                        //p
                        +" and (:email is null or lower(p.email) like :email)"
                        + " and (("
                        + "(:phoneNumber is null or lower(p.phoneNumber) like :phoneNumber)"
                        +" and (:fullName is null or lower(p.firstName) like :fullName)"
                        +" or (:fullName is null or lower(p.lastName) like :fullName)"
                        +" or (:fullName is null or lower(p.otherName) like :fullName)"
                        +")"

                        //// pui
                        + " or ((:phoneNumber is null or lower(pui.phoneNumber) like :phoneNumber)"
                        + " and ((:fullName is null or lower(pui.firstName) like :fullName)" +
                        " or (:fullName is null or lower(pui.lastName) like :fullName)"
                        + " or (:fullName is null or lower(pui.otherName) like :fullName))"
                        +"))"

                        + " and s.dateUpdated between :startDate and :endDate"
        );


        myQueryBuilder(query, internalSearchResponsePojo);

        this.entityManager.close();


        int count = ((Number) query.getSingleResult()).intValue();
        return (long) count;
    }



    private InternalSearchResponsePojo responseInternalSearchResponsePojo(OrderedLabTestSearchDto orderedLabTestSearchDto,
                                                                  Pageable pageable) {

        String email = null;
        String fullName = null;
        String phoneNumber = null;
        String orderId = null;
        String code = null;
        SampleTypeConstant sampleCollectedStatus = null;
        Date startDate;
        Date endDate;

        if (!TextUtils.isBlank(orderedLabTestSearchDto.getEmail())) {
            email = orderedLabTestSearchDto.getEmail().toLowerCase().trim();
        }

        if (!TextUtils.isBlank(orderedLabTestSearchDto.getFullName())) {
            fullName = orderedLabTestSearchDto.getFullName().toLowerCase().trim();
        }


        if (!TextUtils.isBlank(orderedLabTestSearchDto.getOrderId())) {
            orderId = orderedLabTestSearchDto.getOrderId().toLowerCase().trim();
        }


        if (!TextUtils.isBlank(orderedLabTestSearchDto.getCode())) {
            code = orderedLabTestSearchDto.getCode().toLowerCase().trim();
        }


        if (!TextUtils.isBlank(orderedLabTestSearchDto.getSampleCollectedStatus())) {
            SampleTypeConstant sampleCollectedConstant = SampleTypeConstant.valueOf(orderedLabTestSearchDto.getSampleCollectedStatus().toUpperCase().trim());

            if (SampleTypeConstant.ALL.equals(sampleCollectedConstant)) {
                sampleCollectedStatus = null;
            }

            if (SampleTypeConstant.SAMPLE_COLLECTED.equals(sampleCollectedConstant)) {
                sampleCollectedStatus = SampleTypeConstant.SAMPLE_COLLECTED;
            }

            if (SampleTypeConstant.SAMPLE_NOT_COLLECTED.equals(sampleCollectedConstant)) {
                sampleCollectedStatus = SampleTypeConstant.SAMPLE_NOT_COLLECTED;
            }
        }


        if (!TextUtils.isBlank(orderedLabTestSearchDto.getPhoneNumber())) {
            phoneNumber = orderedLabTestSearchDto.getPhoneNumber().trim();
        }

        if (orderedLabTestSearchDto.getStartDate() != null) {
            startDate = Utils.atStartOfDay(orderedLabTestSearchDto.getStartDate());
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

        if (orderedLabTestSearchDto.getEndDate() != null) {
            endDate = Utils.atEndOfDay(orderedLabTestSearchDto.getEndDate());
        } else {
            Date date = new Date();
            endDate = Utils.atEndOfDay(date);
        }



        int pageNumber = 0;
        int pageSize = 10;

        if(pageable != null){
            pageNumber = pageable.getPageNumber();
            pageSize = pageable.getPageSize();
        }


        InternalSearchResponsePojo internalSearchResponsePojo =new InternalSearchResponsePojo();
        internalSearchResponsePojo.setCode(code);
        internalSearchResponsePojo.setEmail(email);
        internalSearchResponsePojo.setEndDate(endDate);
        internalSearchResponsePojo.setFullName(fullName);
        internalSearchResponsePojo.setOrderId(orderId);
        internalSearchResponsePojo.setPhoneNumber(phoneNumber);
        internalSearchResponsePojo.setSampleCollectedStatus(sampleCollectedStatus);
        internalSearchResponsePojo.setStartDate(startDate);
        internalSearchResponsePojo.setPageNumber(pageNumber);
        internalSearchResponsePojo.setPageSize(pageSize);

        return internalSearchResponsePojo;
    }

    private void myQueryBuilder(Query query, InternalSearchResponsePojo internalSearchResponsePojo){
        if (StringUtils.isNotBlank(internalSearchResponsePojo.getEmail())) {
            query.setParameter("email", "%" + internalSearchResponsePojo.getEmail() + "%");
        } else {
            query.setParameter("email", internalSearchResponsePojo.getEmail());
        }

        query.setParameter("orderId", internalSearchResponsePojo.getOrderId());

        query.setParameter("code", internalSearchResponsePojo.getCode());

        if (!TextUtils.isBlank(internalSearchResponsePojo.getFullName())) {
            query.setParameter("fullName", "%" + internalSearchResponsePojo.getFullName() + "%");
        } else {
            query.setParameter("fullName", internalSearchResponsePojo.getFullName());
        }

        query.setParameter("sampleCollectedStatus", internalSearchResponsePojo.getSampleCollectedStatus());


        if (!TextUtils.isBlank(internalSearchResponsePojo.getPhoneNumber())) {
            query.setParameter("phoneNumber", "%" + internalSearchResponsePojo.getPhoneNumber() + "%");
        } else {
            query.setParameter("phoneNumber", internalSearchResponsePojo.getPhoneNumber());
        }
        query.setParameter("startDate", internalSearchResponsePojo.getStartDate());
        query.setParameter("endDate", internalSearchResponsePojo.getEndDate());
    }
}
