package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.LabTestOrderDetail;
import com.hertfordshire.model.psql.SampleCollectedModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleCollectedDao extends JpaRepository<SampleCollectedModel, Long> {

    SampleCollectedModel findByLabTestOrderDetail(LabTestOrderDetail labTestOrderDetail);
}