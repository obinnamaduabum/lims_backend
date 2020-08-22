package com.hertfordshire.service.psql.lab_test_order_details;


import com.hertfordshire.dto.DateSearchDto;
import com.hertfordshire.dto.OrderedLabTestSearchDto;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.pojo.PaginationResponsePojo;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.List;

public interface LabTestOrderDetailsService {


    SampleCollectedModel findSampleCollectedById(Long id);

    List<LabTestOrderDetail> findByOrdersModelAndLabTest(OrdersModel ordersModel, LabTest labTest);

    List<LabTestOrderDetail> findByOrdersModel(OrdersModel ordersModel);

    @Transactional
    SampleCollectedModel updateSampleCollectionStatus(PortalUser portalUser,
                                                      LabTestOrderDetail labTestOrderDetail,
                                                      SampleCollectedModel sampleCollectedModel);

    @Transactional
    boolean updateSampleCollectionDbWithDataOnceCashIsCollected(PortalUser portalUser,
                                                                OrdersModel ordersModel);

    SampleCollectedModel findSampleCollectionStatusByPortalUserAndLabTestOrderDetail(PortalUser portalUser, LabTestOrderDetail labTestOrderDetail);

    @Transactional
    SampleCollectedModel updateSampleCollectionStatus(LabTestOrderDetail labTestOrderDetail, PortalUser collectedBy);

    LabTestOrderDetail findByOrdersModelAndLabTestAndUniqueId(OrdersModel ordersModel, LabTest labTest, String uniqueId);

    PaginationResponsePojo findAllByLabTestsOrdered(OrderedLabTestSearchDto orderedLabTestSearchDto, Pageable pageable);

    Long countAllByLabTestOrdered(OrderedLabTestSearchDto orderedLabTestSearchDto);

    LabTestOrderDetail findUniqueCode(String code);

    PaginationResponsePojo findAllByPatientId(PortalUser portalUser, DateSearchDto dateSearchDto, Pageable pageable);
}
