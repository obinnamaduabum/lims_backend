package com.hertfordshire.dto;

import com.hertfordshire.utils.pojo.PhoneCodesPojo;

import javax.validation.constraints.NotBlank;

public class ReferredByDoesNotExistDto {


    @NotBlank
    private String fullName;

    @NotBlank
    private String phoneNumber;

    private String otherPhoneNumber;


    private PhoneCodesPojo phoneNumberObj;

    private PhoneCodesPojo otherPhoneNumberObj;

    @NotBlank
    private String clinicAddress;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtherPhoneNumber() {
        return otherPhoneNumber;
    }

    public void setOtherPhoneNumber(String otherPhoneNumber) {
        this.otherPhoneNumber = otherPhoneNumber;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public PhoneCodesPojo getPhoneNumberObj() {
        return phoneNumberObj;
    }

    public void setPhoneNumberObj(PhoneCodesPojo phoneNumberObj) {
        this.phoneNumberObj = phoneNumberObj;
    }

    public PhoneCodesPojo getOtherPhoneNumberObj() {
        return otherPhoneNumberObj;
    }

    public void setOtherPhoneNumberObj(PhoneCodesPojo otherPhoneNumberObj) {
        this.otherPhoneNumberObj = otherPhoneNumberObj;
    }
}
