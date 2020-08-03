package com.hertfordshire.service.psql.payment_transaction;

import com.google.gson.Gson;
import com.hertfordshire.dto.PaymentTransactionDto;
import com.hertfordshire.model.psql.PaymentMethodInfo;
import com.hertfordshire.model.psql.PaymentTransaction;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.dao.psql.PaymentTransactionDao;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;
import com.hertfordshire.utils.lhenum.PaymentMethodConstant;
import com.hertfordshire.utils.lhenum.TransactionTypeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentTransactionServiceImpl implements PaymentTransactionService {


    private final Logger logger = LoggerFactory.getLogger(PaymentTransactionServiceImpl.class.getSimpleName());

    private Gson gson;

    @Autowired
    private PaymentTransactionDao paymentTransactionDao;

    public PaymentTransactionServiceImpl() {
        this.gson = new Gson();
    }

    @Override
    public PaymentTransaction save(PaymentTransactionDto paymentTransactionDto, PaymentMethodInfo paymentMethodInfo, PortalUser portalUser) {

        // this.logger.info(this.gson.toJson(paymentTransactionDto));

        PaymentTransaction paymentTransactionModel = new PaymentTransaction();

        if (CurrencyTypeConstant.valueOf(paymentTransactionDto.getCurrencyTypeConstant()).equals(CurrencyTypeConstant.NGN)) {
            paymentTransactionModel.setAmount(Utils.nairaToKobo(paymentTransactionDto.getAmount()));
        } else if (CurrencyTypeConstant.valueOf(paymentTransactionDto.getCurrencyTypeConstant()).equals(CurrencyTypeConstant.USD)) {
            paymentTransactionModel.setAmount(Long.valueOf(paymentTransactionDto.getAmount()));
        } else if (CurrencyTypeConstant.valueOf(paymentTransactionDto.getCurrencyTypeConstant()).equals(CurrencyTypeConstant.EUR)) {
            paymentTransactionModel.setAmount(Long.valueOf(paymentTransactionDto.getAmount()));
        }

        if (!PaymentMethodConstant.valueOf(paymentTransactionDto.getPaymentMethodConstant()).equals(PaymentMethodConstant.CASH)) {
            paymentTransactionModel.setCashCollected(true);
        }

        paymentTransactionModel.setCurrencyTypeConstant(CurrencyTypeConstant.valueOf(paymentTransactionDto.getCurrencyTypeConstant()));

        paymentTransactionModel.setPortalUser(portalUser);

        paymentTransactionModel.setPaymentMethodConstant(PaymentMethodConstant.valueOf(paymentTransactionDto.getPaymentMethodConstant()));

        if (paymentMethodInfo != null) {
            if (paymentMethodInfo.isLiveActive()) {
                paymentTransactionModel.setTransactionTypeConstant(TransactionTypeConstant.LIVE);
            } else {
                paymentTransactionModel.setTransactionTypeConstant(TransactionTypeConstant.TESTING);
            }
        }

        paymentTransactionModel.setTransactionRef(paymentTransactionDto.getTransactionRef());
        return this.paymentTransactionDao.save(paymentTransactionModel);
    }

    @Override
    public PaymentTransaction update(PaymentTransaction paymentTransaction, boolean status) {
        paymentTransaction.setWasPaymentSuccessful(status);
        return paymentTransactionDao.save(paymentTransaction);
    }

    @Override
    public PaymentTransaction findByTransactionRef(String transactionRef) {
        return paymentTransactionDao.findByTransactionRef(transactionRef.toLowerCase());
    }

}
