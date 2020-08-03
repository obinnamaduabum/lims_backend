package com.hertfordshire.dto.payment;

import javax.validation.constraints.NotBlank;

public class FlutterWaveVerifyPaymentDto {

    @NotBlank
    private String transactionRef;

    private double amount;

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}

