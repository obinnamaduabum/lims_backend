package com.hertfordshire.pojo;



import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;
import com.hertfordshire.utils.lhenum.PaymentMethodConstant;
import com.hertfordshire.utils.lhenum.TransactionTypeConstant;

import javax.validation.constraints.NotBlank;

public class PaymentTransactionPojo {

    private Long id;

    @NotBlank
    private String transactionRef;

    private boolean cashCollected;

    private CurrencyTypeConstant currencyTypeConstant;

    private Long amount;

    private boolean wasPaymentSuccessful;

    private boolean isPaymentVerified;

    private PaymentMethodConstant paymentMethodConstant;

    private TransactionTypeConstant transactionTypeConstant;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public boolean isCashCollected() {
        return cashCollected;
    }

    public void setCashCollected(boolean cashCollected) {
        this.cashCollected = cashCollected;
    }

    public CurrencyTypeConstant getCurrencyTypeConstant() {
        return currencyTypeConstant;
    }

    public void setCurrencyTypeConstant(CurrencyTypeConstant currencyTypeConstant) {
        this.currencyTypeConstant = currencyTypeConstant;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public boolean isWasPaymentSuccessful() {
        return wasPaymentSuccessful;
    }

    public void setWasPaymentSuccessful(boolean wasPaymentSuccessful) {
        this.wasPaymentSuccessful = wasPaymentSuccessful;
    }

    public boolean isPaymentVerified() {
        return isPaymentVerified;
    }

    public void setPaymentVerified(boolean paymentVerified) {
        isPaymentVerified = paymentVerified;
    }

    public PaymentMethodConstant getPaymentMethodConstant() {
        return paymentMethodConstant;
    }

    public void setPaymentMethodConstant(PaymentMethodConstant paymentMethodConstant) {
        this.paymentMethodConstant = paymentMethodConstant;
    }

    public TransactionTypeConstant getTransactionTypeConstant() {
        return transactionTypeConstant;
    }

    public void setTransactionTypeConstant(TransactionTypeConstant transactionTypeConstant) {
        this.transactionTypeConstant = transactionTypeConstant;
    }
}
