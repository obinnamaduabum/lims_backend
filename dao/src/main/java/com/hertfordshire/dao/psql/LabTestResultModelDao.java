package com.hertfordshire.dao.psql;

import com.hertfordshire.model.psql.LabTestResultModel;
import com.hertfordshire.model.psql.OrdersModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabTestResultModelDao extends JpaRepository<LabTestResultModel, Long> {

//    List<LabTestOrderDetail> findByLabTestId(Long id);
//
//
//    List<LabTestOrderDetail> findByOrdersModel(OrdersModel ordersModel);
//
//
//    List<LabTestOrderDetail> findByOrdersModelAndLabTest(OrdersModel ordersModel, LabTest labTest);
//
//
//    LabTestOrderDetail findByOrdersModelAndLabTestAndUniqueId(OrdersModel ordersModel, LabTest labTest, String UniqueId);

}