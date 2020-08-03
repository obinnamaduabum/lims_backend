package com.hertfordshire.service.psql.payment_method_config;


import com.hertfordshire.dto.paymentConfig.PaymentConfigDto;
import com.hertfordshire.model.psql.PaymentMethodInfo;

import java.util.List;

public interface PaymentMethodConfigService {

    PaymentMethodInfo save(PaymentConfigDto paymentConfigDto);

    List<PaymentMethodInfo> findAll();

    List<PaymentMethodInfo> findAllOrderById();

    PaymentMethodInfo findByPaymentMethodName(String value);
}
