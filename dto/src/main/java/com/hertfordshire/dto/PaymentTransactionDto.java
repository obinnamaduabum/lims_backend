package com.hertfordshire.dto;


import javax.validation.constraints.NotBlank;
import java.util.List;

public class PaymentTransactionDto {

    @NotBlank
    private String transactionRef;

    private boolean cashCollected;

    @NotBlank
    private String currencyTypeConstant;

    @NotBlank
    private String amount;

    @NotBlank
    private String paymentMethodConstant;

    @NotBlank
    private String transactionTypeConstant;

    private List<OrderDetailsDto> listOfTestsSelected;

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

    public String getCurrencyTypeConstant() {
        return currencyTypeConstant;
    }

    public void setCurrencyTypeConstant(String currencyTypeConstant) {
        this.currencyTypeConstant = currencyTypeConstant;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymentMethodConstant() {
        return paymentMethodConstant;
    }

    public void setPaymentMethodConstant(String paymentMethodConstant) {
        this.paymentMethodConstant = paymentMethodConstant;
    }

    public String getTransactionTypeConstant() {
        return transactionTypeConstant;
    }

    public void setTransactionTypeConstant(String transactionTypeConstant) {
        this.transactionTypeConstant = transactionTypeConstant;
    }

    public List<OrderDetailsDto> getListOfTestsSelected() {
        return listOfTestsSelected;
    }

    public void setListOfTestsSelected(List<OrderDetailsDto> listOfTestsSelected) {
        this.listOfTestsSelected = listOfTestsSelected;
    }
}
