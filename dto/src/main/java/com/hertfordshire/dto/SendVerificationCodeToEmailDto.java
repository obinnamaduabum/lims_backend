package com.hertfordshire.dto;

import javax.validation.constraints.NotBlank;

public class SendVerificationCodeToEmailDto {

    @NotBlank
    private String email;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
