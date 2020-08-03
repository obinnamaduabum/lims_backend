package com.hertfordshire.dto;

public class PaymentTransAndReferredByDoesNotExistDto {

    private PaymentTransactionDto paymentTransactionDto;

    private ReferredByDoesNotExistDto referredByDoesNotExistDto;

    public PaymentTransactionDto getPaymentTransactionDto() {
        return paymentTransactionDto;
    }

    public void setPaymentTransactionDto(PaymentTransactionDto paymentTransactionDto) {
        this.paymentTransactionDto = paymentTransactionDto;
    }

    public ReferredByDoesNotExistDto getReferredByDoesNotExistDto() {
        return referredByDoesNotExistDto;
    }

    public void setReferredByDoesNotExistDto(ReferredByDoesNotExistDto referredByDoesNotExistDto) {
        this.referredByDoesNotExistDto = referredByDoesNotExistDto;
    }
}
