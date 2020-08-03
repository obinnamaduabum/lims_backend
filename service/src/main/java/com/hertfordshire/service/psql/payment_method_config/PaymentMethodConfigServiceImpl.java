package com.hertfordshire.service.psql.payment_method_config;


import com.hertfordshire.dto.paymentConfig.PaymentConfigDto;
import com.hertfordshire.model.psql.PaymentMethodInfo;
import com.hertfordshire.dao.psql.PaymentMethodInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodConfigServiceImpl implements PaymentMethodConfigService {


    private PaymentMethodInfoDao paymentMethodInfoDao;

    @Autowired
    public PaymentMethodConfigServiceImpl(PaymentMethodInfoDao paymentMethodInfoDao) {
        this.paymentMethodInfoDao = paymentMethodInfoDao;
    }

    @Override
    @Transactional
    public PaymentMethodInfo save(PaymentConfigDto paymentConfigDto) {


        PaymentMethodInfo paymentMethodInfo = new PaymentMethodInfo();

        paymentMethodInfo.setPaymentMethodName(paymentConfigDto.getPaymentMethodName());
        paymentMethodInfo.setLivePublicKey(paymentConfigDto.getLive().getPublicKey());
        paymentMethodInfo.setLiveSecret(paymentConfigDto.getLive().getSecret());
        paymentMethodInfo.setLiveVerifyUrl(paymentConfigDto.getLive().getVerifyUrl());

        paymentMethodInfo.setTestingPublicKey(paymentConfigDto.getTesting().getPublicKey());
        paymentMethodInfo.setTestingSecret(paymentConfigDto.getTesting().getSecret());
        paymentMethodInfo.setTestingVerifyUrl(paymentConfigDto.getTesting().getVerifyUrl());

        paymentMethodInfo.setEnabled(paymentConfigDto.isEnabled());
        paymentMethodInfo.setLive(paymentConfigDto.isLive());

        paymentMethodInfoDao.save(paymentMethodInfo);

        return paymentMethodInfo;

    }

    @Override
    public List<PaymentMethodInfo> findAll() {

        return this.paymentMethodInfoDao.findAll();
    }

    @Override
    public List<PaymentMethodInfo> findAllOrderById() {
        return this.paymentMethodInfoDao.findAll(Sort.by(Sort.Order.asc("id")));
    }

    @Override
    public PaymentMethodInfo findByPaymentMethodName(String value) {
        Optional<PaymentMethodInfo> optionalPaymentMethodInfo = paymentMethodInfoDao.findByPaymentMethodName(value);
        return optionalPaymentMethodInfo.orElse(null);
    }
}
