package com.hertfordshire.service.psql.payment_transaction;


import com.hertfordshire.dto.PaymentTransactionDto;
import com.hertfordshire.model.psql.OrdersModel;
import com.hertfordshire.model.psql.PaymentMethodInfo;
import com.hertfordshire.model.psql.PaymentTransaction;
import com.hertfordshire.model.psql.PortalUser;

public interface PaymentTransactionService {

    OrdersModel save(PaymentTransactionDto paymentTransactionDto, PaymentMethodInfo paymentMethodInfo, PortalUser portalUser);

    PaymentTransaction update(PaymentTransaction paymentTransaction, boolean status);

    PaymentTransaction findByTransactionRef(String transactionRef);
}
