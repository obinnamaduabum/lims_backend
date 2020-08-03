package com.hertfordshire.dto.paymentConfig;


public class PaymentConfigDto {

    private String paymentMethodName;

    private PaymentConfigDetailsDto testing;

    private PaymentConfigDetailsDto live;

    private boolean isLive;

    private boolean enabled;


    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    public PaymentConfigDetailsDto getTesting() {
        return testing;
    }

    public void setTesting(PaymentConfigDetailsDto testing) {
        this.testing = testing;
    }

    public PaymentConfigDetailsDto getLive() {
        return live;
    }

    public void setLive(PaymentConfigDetailsDto live) {
        this.live = live;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}


