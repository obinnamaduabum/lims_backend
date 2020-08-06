package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.LabTestTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface LabTestTemplateDao extends JpaRepository<LabTestTemplate, Long> {

    @Query("SELECT l FROM LabTestTemplate as l WHERE lower(l.name) = ?1")
    LabTestTemplate findByName(String name);


    @Query("SELECT l FROM LabTestTemplate as l WHERE lower(l.code) = ?1")
    LabTestTemplate findByCode(String code);
}
