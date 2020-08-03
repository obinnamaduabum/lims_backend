package com.hertfordshire.dao.psql;

import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Repository
public interface PortalAccountDao extends JpaRepository<PortalAccount, Long> {

    PortalAccount findPortalAccountByCode(String portalAccountId);

    @Query("SELECT pa FROM PortalAccount as pa WHERE lower(pa.name) = ?1")
    PortalAccount findPortalAccountByPortalAccountName(String name);

    @Query( value = "SELECT count(p) FROM portal_account_and_portal_account_types as pa, portal_user_portal_account p " +
            "WHERE pa.portal_account_type_id = ?1" +
            " and pa.portal_account_id = p.portal_account_id", nativeQuery = true)
    Long countByPortalAccountByPortalAccountTypes(Long id);


    @Query( value = "SELECT p.id FROM portal_account_and_portal_account_types as pa, portal_account p" +
            " WHERE pa.portal_account_type_id = ?1" +
            " and pa.portal_account_id = p.id", nativeQuery = true)
    List<BigInteger> findPortalAccountByPortalAccountType(Long id);


    @Query( value = "SELECT p.email FROM portal_user_portal_account as pa, portal_user p" +
            " WHERE pa.portal_account_id = ?1" +
            " and pa.portal_user_id = p.id", nativeQuery = true)
    List<String> findPortalUsersByPortalAccount(Long id);


    @Query( value = "SELECT p FROM portal_account_and_portal_account_types as pa," +
            " portal_user_portal_account p, portal_user "+
            "WHERE pa.portal_account_type_id = ?1" +
            " and pa.portal_account_id = p.portal_account_id" +
            " and portal_user", nativeQuery = true)
    List<PortalUser> findByPortalUserByPortalAccountTypes(Long id);



    @Query(value = "select count(p) from portal_user_portal_account as pa, portal_user p where pa.portal_account_id = :lookUpPortalAccount" +
            " and p.id = pa.portal_user_id" +
            " and (:email is null or lower(p.email) LIKE CONCAT('%',:email,'%')) and (:fullName is null or lower(p.first_name) LIKE CONCAT('%',:fullName,'%'))" +
                " or (:fullName is null or lower(p.last_name) LIKE CONCAT('%',:fullName,'%'))" +
                " and p.date_created between :startDate and :endDate", nativeQuery = true)
    Long findByPortalAccountId(@Param("lookUpPortalAccount") Long lookUpPortalAccountId,
                               @Param("email") String email,
                               @Param("fullName") String fullName,
                               @Param("startDate") Date startDate,
                               @Param("endDate") Date endDate);
}
