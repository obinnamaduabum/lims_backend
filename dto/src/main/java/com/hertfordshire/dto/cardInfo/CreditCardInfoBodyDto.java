package com.hertfordshire.dto.cardInfo;

public class CreditCardInfoBodyDto {

    private String message;
    private boolean status;
    private CreditCardInfoDataDto data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public CreditCardInfoDataDto getData() {
        return data;
    }

    public void setData(CreditCardInfoDataDto data) {
        this.data = data;
    }
}
