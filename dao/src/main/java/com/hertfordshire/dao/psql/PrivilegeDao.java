package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PrivilegeDao extends JpaRepository<Privilege, Long> {

    @Query("SELECT p FROM Privilege as p WHERE lower(p.name) = ?1")
    Privilege findPrivilegeByName(String name);

}