package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.PortalAccountAndPortalUserRoleMappper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortalAccountAndPortalUserRoleMapperDao extends JpaRepository<PortalAccountAndPortalUserRoleMappper, Long> {

    @Query("SELECT pa FROM PortalAccountAndPortalUserRoleMappper as pa WHERE pa.id = ?1")
    List<PortalAccountAndPortalUserRoleMappper> findPortalAccountRoleMappperById(Long Id);

    @Query("SELECT pa FROM PortalAccountAndPortalUserRoleMappper as pa WHERE lower(pa.portalUserCode) = ?1")
    List<PortalAccountAndPortalUserRoleMappper> findPortalAccountRoleMappperByPortalAccountCode(String code);

    @Query("SELECT pa FROM PortalAccountAndPortalUserRoleMappper as pa WHERE lower(pa.portalAccountCode) = ?1 and lower(pa.portalUserCode) = ?2")
    List<PortalAccountAndPortalUserRoleMappper> findByPortalAccountCodeAndPortalUserCode(String portalAccountCode, String portalUserCode);
}