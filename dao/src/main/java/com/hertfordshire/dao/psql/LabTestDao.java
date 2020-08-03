package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.model.psql.LabTestCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabTestDao extends JpaRepository<LabTest, Long> {

    @Query("SELECT l FROM LabTest as l WHERE lower(l.name) = ?1")
    LabTest findByName(String name);

    @Query("SELECT l FROM LabTest as l WHERE lower(l.name) = ?1")
    List<LabTest> findByName(String name, Pageable pageable);

    List<LabTest> findByLabTestCategory(LabTestCategory labTestCategory);

    LabTest findByResultTemplateId(String resultTemplateId);
}
