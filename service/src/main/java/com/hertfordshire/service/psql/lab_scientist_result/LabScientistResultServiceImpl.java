package com.hertfordshire.service.psql.lab_scientist_result;

import com.google.gson.Gson;
import com.hertfordshire.dto.OrderedLabTestSearchDto;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.pojo.*;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.lhenum.SampleTypeConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LabScientistResultServiceImpl implements LabScientistResultService {

    private final Logger logger = LoggerFactory.getLogger(LabScientistResultServiceImpl.class.getSimpleName());

    @Autowired
    private PortalUserService portalUserService;

    private Gson gson;

    @PersistenceContext
    private EntityManager entityManager;

    String dateSource = "2000-09-09";

    public LabScientistResultServiceImpl() {
        this.gson = new Gson();
    }

    @Override
    public PaginationResponsePojo findByLabScientistResultWithPagination(OrderedLabTestSearchDto orderedLabTestSearchDto, Pageable pageable) {


        //this.logger.info(this.gson.toJson(orderedLabTestSearchDto));

        InternalSearchResponsePojo internalSearchResponsePojo
        = responseInternalLabTestResultBuilder(orderedLabTestSearchDto, pageable);

        Query query;

        query = this.entityManager.createQuery(
                "select distinct "
                        + "la, "
                        + "p, "
                        + "s, "
                        + "o,"
                        + "ls"
                        + " from LabScientistTestResultModel as ls"
                        + " LEFT JOIN SampleCollectedModel as s ON s.id = ls.sampleCollectedModel.id"
                        + " LEFT JOIN LabTestOrderDetail la ON la.id = s.labTestOrderDetail.id"
                        + " LEFT JOIN OrdersModel o ON o.id = la.ordersModel.id"
                        + " LEFT JOIN PortalUser p ON ((p.id = la.patient.id) or p.id is null)"

                        + " where o.cashCollected = true"
                        + " and (:orderId is null or lower(o.code) = :orderId)"

                        + " and (:code is null or lower(la.uniqueId) = :code)"

                        //p
                        +" and (:email is null or lower(p.email) like :email)"
                        + " and (("
                        + "(:phoneNumber is null or lower(p.phoneNumber) like :phoneNumber)"
                        +" and (:fullName is null or lower(p.firstName) like :fullName)"
                        +" or (:fullName is null or lower(p.lastName) like :fullName)"
                        +" or (:fullName is null or lower(p.otherName) like :fullName)"
                        +"))"
                        + " and s.dateUpdated between :startDate and :endDate order by s.dateUpdated desc"

        );

         queryBuilder(query, internalSearchResponsePojo);

         query.setMaxResults(internalSearchResponsePojo.getPageSize());

        this.entityManager.close();

        List<Object[]> rows = query.getResultList();

        logger.info("rows: " +rows.size());

        PaginationResponsePojo paginationResponsePojo = new PaginationResponsePojo();
//
        List<LabTestInfoForMedicalLabScientistPojo> orderedPojos = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
//
            LabTestInfoForMedicalLabScientistPojo labTestsOrderedPojo = new LabTestInfoForMedicalLabScientistPojo();
//            query.getResultList() portalUserResponsePojo = new PortalUserResponsePojo();
//
            if (internalSearchResponsePojo.getPageNumber() == 0) {
                labTestsOrderedPojo.setPosition((long) (i + 1));
            } else {
                labTestsOrderedPojo.setPosition((long) (i + internalSearchResponsePojo.getPageSize() + internalSearchResponsePojo.getPageNumber()));
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
                        labTestsOrderedPojo.setAccountType(portalAccount.getPortalAccountType().name());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                labTestsOrderedPojo.setLabTest(labTestPojo);
            }

            // portal user
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

            LabScientistTestResultModel labScientistTestResultModel = (LabScientistTestResultModel) rows.get(i)[4];
            labTestsOrderedPojo.setLabTestFormId(labScientistTestResultModel.getLabResultId());
            labTestsOrderedPojo.setMedicalLabScientistSampleCollectedId(labScientistTestResultModel.getId());

            labTestsOrderedPojo.setLabScientistStatusConstant(labScientistTestResultModel.getLabScientistStatusConstant().toString());
            orderedPojos.add(labTestsOrderedPojo);
        }

        paginationResponsePojo.setDataList(orderedPojos);
        paginationResponsePojo.setPageNumber((long) internalSearchResponsePojo.getPageNumber());
        paginationResponsePojo.setPageSize((long) internalSearchResponsePojo.getPageSize());
        paginationResponsePojo.setLength(this.countByLabScientistResultWithPagination(orderedLabTestSearchDto));

        // logger.info(this.gson.toJson(paginationResponsePojo));
        return paginationResponsePojo;
    }


    @Override
    public Long countByLabScientistResultWithPagination(OrderedLabTestSearchDto orderedLabTestSearchDto) {


        InternalSearchResponsePojo internalSearchResponsePojo =
        responseInternalLabTestResultBuilder(orderedLabTestSearchDto, null);


        Query query = this.entityManager.createQuery(
                "select distinct count(ls)"
                        + " from LabScientistTestResultModel as ls"
                        + " LEFT JOIN SampleCollectedModel as s ON s.id = ls.sampleCollectedModel.id"
                        + " LEFT JOIN LabTestOrderDetail la ON la.id = s.labTestOrderDetail.id"
                        + " LEFT JOIN OrdersModel o ON o.id = la.ordersModel.id"
                        + " LEFT JOIN PortalUser p ON ((p.id = la.patient.id) or p.id is null)"

                        + " where o.cashCollected = true"
                        + " and (:orderId is null or lower(o.code) = :orderId)"

                        + " and (:code is null or lower(la.uniqueId) = :code)"

                        //p
                        + " and (:email is null or lower(p.email) like :email)"
                        + " and (("
                        + "(:phoneNumber is null or lower(p.phoneNumber) like :phoneNumber)"
                        + " and (:fullName is null or lower(p.firstName) like :fullName)"
                        + " or (:fullName is null or lower(p.lastName) like :fullName)"
                        + " or (:fullName is null or lower(p.otherName) like :fullName)"
                        + "))"
                        + " and s.dateUpdated between :startDate and :endDate"

        );

        queryBuilder(query, internalSearchResponsePojo);

        this.entityManager.close();
        //logger.info(this.gson.toJson(query.getResultList().size()));

        int count = ((Number) query.getSingleResult()).intValue();
        return (long) count;
    }


    private void queryBuilder(Query query, InternalSearchResponsePojo internalSearchResponsePojo) {

        if (StringUtils.isNotBlank(internalSearchResponsePojo.getEmail())) {
            query.setParameter("email", "%" + internalSearchResponsePojo.getEmail() + "%");
        } else {
            query.setParameter("email", internalSearchResponsePojo.getEmail());
        }

        query.setParameter("orderId", internalSearchResponsePojo.getOrderId());

        query.setParameter("code", internalSearchResponsePojo.getCode());

        if (StringUtils.isNotBlank(internalSearchResponsePojo.getFullName())) {
            query.setParameter("fullName", "%" + internalSearchResponsePojo.getFullName() + "%");
        } else {
            query.setParameter("fullName", internalSearchResponsePojo.getFullName());
        }

        if (!TextUtils.isBlank(internalSearchResponsePojo.getPhoneNumber())) {
            query.setParameter("phoneNumber", "%" + internalSearchResponsePojo.getPhoneNumber() + "%");
        } else {
            query.setParameter("phoneNumber", internalSearchResponsePojo.getPhoneNumber());
        }

        query.setParameter("startDate", internalSearchResponsePojo.getStartDate());
        query.setParameter("endDate", internalSearchResponsePojo.getEndDate());
    }


    private InternalSearchResponsePojo responseInternalLabTestResultBuilder(
            OrderedLabTestSearchDto orderedLabTestSearchDto,
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
        if(pageable != null) {
            pageNumber = pageable.getPageNumber();
            pageSize = pageable.getPageSize();
        }

        InternalSearchResponsePojo internalSearchResponsePojo = new InternalSearchResponsePojo();
        internalSearchResponsePojo.setEmail(email);
        internalSearchResponsePojo.setStartDate(startDate);
        internalSearchResponsePojo.setEndDate(endDate);
        internalSearchResponsePojo.setCode(code);
        internalSearchResponsePojo.setPageSize(pageSize);
        internalSearchResponsePojo.setPageNumber(pageNumber);
        internalSearchResponsePojo.setFullName(fullName);
        internalSearchResponsePojo.setSampleCollectedStatus(sampleCollectedStatus);
        internalSearchResponsePojo.setPhoneNumber(phoneNumber);
        internalSearchResponsePojo.setOrderId(orderId);

        return internalSearchResponsePojo;
    }

}
