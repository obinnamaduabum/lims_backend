package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.OrdersModel;
import com.hertfordshire.model.psql.PortalUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface LabTestOrderDao extends JpaRepository<OrdersModel, Long> {

    @Query("SELECT o FROM OrdersModel as o WHERE lower(o.code) = ?1")
    OrdersModel findByCode(String code);

    Optional<OrdersModel> findByIdAndPortalUser(Long id, PortalUser portalUser);

    List<OrdersModel> findByPortalUser(PortalUser portalUser);

    @Query(value = "select o from OrdersModel o where o.portalUser  = :portalUser " +
            "and o.dateCreated BETWEEN :startDate AND :endDate")
    Page<OrdersModel> findByPortalUserAndDateCreatedBetween(@Param("portalUser") PortalUser portalUser,
                                                            @Param("startDate") Date startDate,
                                                            @Param("endDate") Date endDate,
                                                            Pageable pageable);


    @Query(value = "select count(o) from OrdersModel o where o.portalUser  = :portalUser " +
            "and o.dateCreated BETWEEN :startDate AND :endDate")
    Number countByPortalUserAndDateCreatedBetween(@Param("portalUser") PortalUser portalUser,
                                                  @Param("startDate") Date startDate,
                                                  @Param("endDate") Date endDate);


    Number countByPortalUser(PortalUser portalUser);
}
