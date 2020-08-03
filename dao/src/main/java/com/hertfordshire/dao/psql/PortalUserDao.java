package com.hertfordshire.dao.psql;

import com.hertfordshire.model.psql.PortalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PortalUserDao extends JpaRepository<PortalUser, Long> {

    @Query("SELECT p FROM PortalUser as p WHERE lower(p.email) = ?1")
    PortalUser findPortalUserByEmail(String name);

    PortalUser findByEmail(String email);

    PortalUser findByCode(String code);

    @Query("SELECT p FROM PortalUser as p WHERE lower(p.phoneNumber) = ?1")
    PortalUser findByPhoneNumber(String phoneNumber);
}
