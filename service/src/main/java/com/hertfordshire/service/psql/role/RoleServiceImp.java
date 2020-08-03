package com.hertfordshire.service.psql.role;

import com.google.gson.Gson;
import com.hertfordshire.dto.RolesDto;
import com.hertfordshire.model.psql.PortalAccountAndPortalUserRoleMappper;
import com.hertfordshire.model.psql.Privilege;
import com.hertfordshire.model.psql.Role;
import com.hertfordshire.dao.psql.RolesDao;
import com.hertfordshire.service.psql.portal_account_role_mapper.PortalAccountAndPortalUserRoleMapperService;
import com.hertfordshire.service.psql.privileges.PrivilegeService;
import com.hertfordshire.utils.lhenum.RoleTypeConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class RoleServiceImp implements RolesService {

    private static Logger logger = Logger.getLogger(RoleServiceImp.class.getSimpleName());

//    @Autowired
//    private Gson gson;

    private RolesDao rolesDao;

    private PortalAccountAndPortalUserRoleMapperService portalAccountAndPortalUserRoleMapperService;

    private PrivilegeService privilegeService;


    @Autowired
    public RoleServiceImp(RolesDao rolesDao,
                          PrivilegeService privilegeService,
                          PortalAccountAndPortalUserRoleMapperService portalAccountAndPortalUserRoleMapperService) {
        this.privilegeService = privilegeService;
        this.rolesDao = rolesDao;
        this.portalAccountAndPortalUserRoleMapperService = portalAccountAndPortalUserRoleMapperService;
    }

    @Override
    public Role save(RolesDto rolesDto) {


        //logger.info(gson.toJson(rolesDto));

        Role role = new Role();
        role.setRoleName(rolesDto.getName());
        role.setRoleType(RoleTypeConstant.valueOf(rolesDto.getRoleType()));


        rolesDto.getPrivileges().forEach(privilegeDto -> {
            Privilege privilege = this.privilegeService.findName(privilegeDto.getName());
            role.getPrivileges().add(privilege);
        });

        this.rolesDao.save(role);
        return role;
    }

    @Override
    public List<Role> findAll() {
        return this.rolesDao.findAll();
    }

    @Override
    public Role findByRoleType(RoleTypeConstant roleType) {
        return rolesDao.findByRoleType(roleType);
    }

    @Override
    public Role findById(Long id) {
        Optional<Role> roleOptional = rolesDao.findById(id);
        return roleOptional.orElse(null);
    }

    @Override
    public List<Role> findRoleByPortalAccountAndPortalUser(String portalAccountCode, String portalUserCode) {

        List<Role> roles = new ArrayList<>();
        List<PortalAccountAndPortalUserRoleMappper> portalAccountAndPortalUserRoleMapppers =
                this.portalAccountAndPortalUserRoleMapperService.findByPortalAccountCodeAndPortalUserCode(portalAccountCode, portalUserCode);

        portalAccountAndPortalUserRoleMapppers.forEach(portalAccountAndPortalUserRoleMappper -> {
            Optional<Role> roleOptional = this.rolesDao.findById(portalAccountAndPortalUserRoleMappper.getRoleId());
            if(roleOptional.isPresent()){
                roles.add(roleOptional.get());
            }
        });

        return roles;
    }
}
