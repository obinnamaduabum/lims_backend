package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.PortalUserInstitutionLabTestOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortalUserInstitutionLabTestOrderDetailDao extends JpaRepository<PortalUserInstitutionLabTestOrderDetail, Long> {
}
