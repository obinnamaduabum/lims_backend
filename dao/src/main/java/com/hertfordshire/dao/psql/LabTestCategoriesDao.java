package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.LabTestCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LabTestCategoriesDao extends JpaRepository<LabTestCategory, Long> {

    @Query("SELECT l FROM LabTestCategory as l WHERE lower(l.name) = ?1")
    LabTestCategory findByName(String name);
}
