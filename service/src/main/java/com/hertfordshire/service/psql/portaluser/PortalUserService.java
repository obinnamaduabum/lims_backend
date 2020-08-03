package com.hertfordshire.service.psql.portaluser;


import com.hertfordshire.dto.ChangePasswordDto;
import com.hertfordshire.dto.CheckOldPasswordDto;
import com.hertfordshire.dto.EmployeeSearchDto;
import com.hertfordshire.dto.PortalUserDto;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.utils.lhenum.PortalUserTypeConstant;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PortalUserService {

    List<PortalUser> findAll();

    PortalUser findPortalUserByEmail(String email);

    PortalUser findPortalUserById(Long id);

    PortalUser findPortalUserByPhoneNumber(String phoneNumber);

    PortalUser findByEmail(String email);

    PortalUser findById(Long id);

    PortalUser findByCode(String code);

    PortalUser findByPortalUserCode(String code);

    PortalUser findByPhoneNumber(String phoneNumber);

    PortalUser updateUserProfile(Object portalUserDto);

    boolean checkIfPasswordsmatch(CheckOldPasswordDto checkOldPasswordDto, PortalUser portalUser);

    boolean changePassword(ChangePasswordDto changePasswordDto, PortalUser portalUser);

    PaginationResponsePojo findAllByDefaultPortalAccountCode(EmployeeSearchDto employeeSearchDto, PortalAccount lookUpPortalAccount, Pageable pageable);

    PaginationResponsePojo findAllByPortalUsers(EmployeeSearchDto employeeSearchDto, Pageable pageable);

    Long countAllByPortalUsers(EmployeeSearchDto employeeSearchDto, Pageable pageable);

    Long countByDefaultPortalAccountCode(EmployeeSearchDto employeeSearchDto, PortalAccount lookUpPortalAccount, Pageable pageable);

    PaginationResponsePojo findAllByDefaultPortalAccountCodeForEmployee(EmployeeSearchDto employeeSearchDto, PortalAccount lookUpPortalAccount, Pageable pageable);

    Long countByDefaultPortalAccountCodeForEmployee(EmployeeSearchDto employeeSearchDto, PortalAccount lookUpPortalAccount, Pageable pageable);

    List<PortalUser> findByRoleType(Long roleId);
}
