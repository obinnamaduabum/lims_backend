package com.hertfordshire.service.psql.test_order;

import com.hertfordshire.dto.LabTestsOrderSearchDto;
import com.hertfordshire.dto.PaymentTransactionDto;
import com.hertfordshire.dto.ReferredByDoesNotExistDto;
import com.hertfordshire.model.psql.OrdersModel;
import com.hertfordshire.model.psql.PaymentTransaction;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.pojo.LabTestOrdersPojo;
import com.hertfordshire.pojo.PaginationResponsePojo;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TestOrderService {

    OrdersModel findByCode(String code);

    Optional<OrdersModel> findByIdAndPortalUser(Long id, PortalUser portalUser);

    Optional<OrdersModel> findById(Long id);

    List<OrdersModel> findByPortalUser(PortalUser portalUser);

    PaginationResponsePojo findByPortalUserWithPagination(PortalUser portalUser, LabTestsOrderSearchDto labTestsOrderSearchDto, Pageable pageable);

    PaginationResponsePojo findByWithPagination(LabTestsOrderSearchDto labTestsOrderSearchDto, Pageable pageable);

    Long countByPortalUserWithPagination(PortalUser portalUser, LabTestsOrderSearchDto labTestsOrderSearchDto);

    Long countByWithPagination(LabTestsOrderSearchDto labTestsOrderSearchDto);

    List<Map<String, Object>> generateReceipt(LabTestOrdersPojo labTestOrdersPojo);

    @Transactional
    OrdersModel create(PaymentTransaction paymentTransaction, PaymentTransactionDto paymentTransactionDto, ReferredByDoesNotExistDto referredByDoesNotExistDto, PortalUser portalUser);

    Number countNumberOfOrdersForLoggedInUser(PortalUser portalUser);
}
