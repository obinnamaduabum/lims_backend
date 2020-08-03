package com.hertfordshire.service.psql.portalaccount;


import com.hertfordshire.dto.EmployeeSearchDto;
import com.hertfordshire.dto.PortalAccountDto;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.service.sequence.portal_account_id.PortalAccountSequenceService;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PortalAccountService {

    PortalAccount save(PortalAccountDto portalAccountDto);

    PortalAccount saveOrUpdate(PortalAccountDto portalAccountDto);

    List<PortalAccount> findAll();

    PortalAccount findPortalAccountByPortalAccountId(String portalAccountId);

    PortalAccount findPortalAccountByName(String name);

    PaginationResponsePojo findAllByEmployee(EmployeeSearchDto employeeSearchDto, PortalAccount lookUpPortalAccount, Pageable pageable);

    Long findAllByEmployeeCount(EmployeeSearchDto employeeSearchDto, PortalAccount lookUpPortalAccount, Pageable pageable);

}
