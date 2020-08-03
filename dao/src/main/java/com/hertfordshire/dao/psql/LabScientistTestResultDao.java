package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.LabScientistTestResultModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabScientistTestResultDao extends JpaRepository<LabScientistTestResultModel, Long> {

}
