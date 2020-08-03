package com.hertfordshire.payment.dto;

import javax.validation.constraints.NotBlank;

public class PaymentTransactionDto {

    @NotBlank
    private String transactionReference;

    @NotBlank
    private String currency;

    @NotBlank
    private String paymentMethod;

    @NotBlank
    private String email;

    @NotBlank
    private String amountInNaira;

    @NotBlank
    private String transactionTypeConstant;

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAmountInNaira() {
        return amountInNaira;
    }

    public void setAmountInNaira(String amountInNaira) {
        this.amountInNaira = amountInNaira;
    }

    public String getTransactionTypeConstant() {
        return transactionTypeConstant;
    }

    public void setTransactionTypeConstant(String transactionTypeConstant) {
        this.transactionTypeConstant = transactionTypeConstant;
    }
}
