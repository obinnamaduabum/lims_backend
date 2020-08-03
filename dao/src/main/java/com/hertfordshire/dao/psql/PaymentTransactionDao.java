package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionDao extends JpaRepository<PaymentTransaction, Long> {

    @Query("SELECT p FROM PaymentTransaction as p WHERE lower(p.transactionRef) = ?1")
    PaymentTransaction findByTransactionRef(String transRef);
}
