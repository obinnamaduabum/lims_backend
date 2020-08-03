package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.model.psql.LabTestOrderDetail;
import com.hertfordshire.model.psql.OrdersModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabTestOrderDetailDao extends JpaRepository<LabTestOrderDetail, Long> {

    List<LabTestOrderDetail> findByLabTestId(Long id);


    List<LabTestOrderDetail> findByOrdersModel(OrdersModel ordersModel);


    List<LabTestOrderDetail> findByOrdersModelAndLabTest(OrdersModel ordersModel, LabTest labTest);


    LabTestOrderDetail findByOrdersModelAndLabTestAndUniqueId(OrdersModel ordersModel, LabTest labTest, String UniqueId);

}
