package com.hertfordshire.dto;

import javax.validation.constraints.NotBlank;

public class SettingsDto {

    @NotBlank
    private boolean isPaymentLive;

    public boolean isPaymentLive() {
        return isPaymentLive;
    }

    public void setPaymentLive(boolean paymentLive) {
        isPaymentLive = paymentLive;
    }

}

