package com.hertfordshire.service.psql.employee;


import com.hertfordshire.dto.EmployeeDto;
import com.hertfordshire.dto.PortalAccountDto;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.service.sequence.portal_account_id.PortalAccountSequenceService;
import org.springframework.stereotype.Service;

@Service
public interface EmployeeService {

   PortalUser updateEmployee(EmployeeDto employeeDto, PortalUser portalUser, PortalAccount foundPortalAccount);

   PortalUser createEmployee(EmployeeDto employeeDto,
                             PortalAccountDto portalAccountDto,
                             PortalAccount portalAccount,
                             PortalUser createdBy,
                             PortalAccountSequenceService portalAccountSequenceService);
}
