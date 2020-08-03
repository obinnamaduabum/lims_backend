package com.hertfordshire.dao.psql;

import com.hertfordshire.model.psql.PaymentMethodInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentMethodInfoDao extends JpaRepository<PaymentMethodInfo, Long> {

    Optional<PaymentMethodInfo> findByPaymentMethodName(String paymentMethodName);
}
