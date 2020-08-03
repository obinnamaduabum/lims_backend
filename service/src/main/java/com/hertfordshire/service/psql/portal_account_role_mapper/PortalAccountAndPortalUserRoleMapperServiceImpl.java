package com.hertfordshire.service.psql.portal_account_role_mapper;

import com.google.gson.Gson;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalAccountAndPortalUserRoleMappper;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.model.psql.Role;
import com.hertfordshire.dao.psql.PortalAccountAndPortalUserRoleMapperDao;
import com.hertfordshire.dao.psql.RolesDao;
import com.hertfordshire.utils.lhenum.RoleTypeConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PortalAccountAndPortalUserRoleMapperServiceImpl implements PortalAccountAndPortalUserRoleMapperService {

    @Autowired
    private PortalAccountAndPortalUserRoleMapperDao portalAccountAndPortalUserRoleMapperDao;

//    @Autowired
    private Gson gson;

//    @Autowired
    private RolesDao rolesDao;

    public PortalAccountAndPortalUserRoleMapperServiceImpl(PortalAccountAndPortalUserRoleMapperDao portalAccountAndPortalUserRoleMapperDao, RolesDao rolesDao) {
        this.portalAccountAndPortalUserRoleMapperDao = portalAccountAndPortalUserRoleMapperDao;
        this.rolesDao = rolesDao;
    }


    @Override
    public PortalAccountAndPortalUserRoleMappper save(Long portalAccountId, Role role) {
        return null;
    }

    @Override
    public List<PortalAccountAndPortalUserRoleMappper> findAll() {
        return null;
    }

    @Override
    public List<PortalAccountAndPortalUserRoleMappper> findByPortalAccountCodeAndPortalUserCode(
            String portalAccountCode, String portalUserCode) {

        return this.portalAccountAndPortalUserRoleMapperDao.findByPortalAccountCodeAndPortalUserCode(
                portalAccountCode.toLowerCase(), portalUserCode.toLowerCase());
    }

    @Override
    public void delete(PortalAccountAndPortalUserRoleMappper portalAccountAndPortalUserRoleMappper) {
        this.portalAccountAndPortalUserRoleMapperDao.delete(portalAccountAndPortalUserRoleMappper);
    }


    @Transactional
    @Override
    public void saveUserBeingRegisteredsRoles(PortalAccount portalAccount, PortalUser portalUser, String[] roles) {

        if (roles.length > 0) {

            for (String role : roles) {
                Role newRole = rolesDao.findByRoleType(
                        RoleTypeConstant.valueOf(role.toUpperCase()));
                PortalAccountAndPortalUserRoleMappper portalAccountAndPortalUserRoleMappper
                        = new PortalAccountAndPortalUserRoleMappper();
                portalAccountAndPortalUserRoleMappper.setRoleName(newRole.getRoleName());
                portalAccountAndPortalUserRoleMappper.setPortalAccountCode(portalAccount.getCode());
                portalAccountAndPortalUserRoleMappper.setRoleId(newRole.getId());
                portalAccountAndPortalUserRoleMappper.setRoleTypeConstant(newRole.getRoleType());
                portalAccountAndPortalUserRoleMappper.setPortalUserCode(portalUser.getCode());

                try {
                    portalAccountAndPortalUserRoleMapperDao.save(portalAccountAndPortalUserRoleMappper);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Transactional
    @Override
    public void removeAllRoles(PortalAccount portalAccount, PortalUser portalUser) {
        List<PortalAccountAndPortalUserRoleMappper> portalAccountAndPortalUserRoleMappers =
                portalAccountAndPortalUserRoleMapperDao.findByPortalAccountCodeAndPortalUserCode(
                        portalAccount.getCode().toLowerCase().trim(),
                        portalUser.getCode().toLowerCase().trim());

        for (PortalAccountAndPortalUserRoleMappper portalAccountAndPortalUserRoleMapper: portalAccountAndPortalUserRoleMappers) {
            portalAccountAndPortalUserRoleMapperDao.deleteById(portalAccountAndPortalUserRoleMapper.getId());
        }

       // portalAccountAndPortalUserRoleMappers.forEach(this::delete);
    }



}
