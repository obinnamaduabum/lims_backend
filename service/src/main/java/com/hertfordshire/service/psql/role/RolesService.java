package com.hertfordshire.service.psql.role;


import com.hertfordshire.dto.RolesDto;
import com.hertfordshire.model.psql.Role;
import com.hertfordshire.utils.lhenum.RoleTypeConstant;

import java.util.List;

public interface RolesService {

    Role save(RolesDto rolesDto);

    List<Role> findAll();

    Role findByRoleType(RoleTypeConstant roleType);

    Role findById(Long id);

    List<Role> findRoleByPortalAccountAndPortalUser(String portalAccountCode, String portalUserCode);

}
