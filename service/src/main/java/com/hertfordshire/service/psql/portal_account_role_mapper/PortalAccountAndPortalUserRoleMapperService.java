package com.hertfordshire.service.psql.portal_account_role_mapper;


import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalAccountAndPortalUserRoleMappper;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.model.psql.Role;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PortalAccountAndPortalUserRoleMapperService {

    PortalAccountAndPortalUserRoleMappper save(Long portalAccountId, Role role);

    List<PortalAccountAndPortalUserRoleMappper> findAll();

    List<PortalAccountAndPortalUserRoleMappper> findByPortalAccountCodeAndPortalUserCode(String portalAccountCode, String portalUserCode);

    void delete(PortalAccountAndPortalUserRoleMappper portalAccountAndPortalUserRoleMappper);

    void saveUserBeingRegisteredsRoles(PortalAccount portalAccount, PortalUser portalUser, String[] roles);

    @Transactional
    void removeAllRoles(PortalAccount portalAccount, PortalUser portalUser);
}
