package com.hertfordshire.dto;

import com.hertfordshire.utils.lhenum.TypeOfPhoneNumberVerification;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;

public class PhoneNumberVerificationDto {

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String code;

    @Enumerated(EnumType.STRING)
    private TypeOfPhoneNumberVerification type;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TypeOfPhoneNumberVerification getType() {
        return type;
    }

    public void setType(TypeOfPhoneNumberVerification type) {
        this.type = type;
    }
}
