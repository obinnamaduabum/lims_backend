package com.hertfordshire.dto;

import com.hertfordshire.utils.pojo.PhoneCodesPojo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class InstitutionDto {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String address;

    @NotBlank
    private String phoneNumber;

    private String otherPhoneNumber;

    private PhoneCodesPojo phoneNumberObj;

    private PhoneCodesPojo otherPhoneNumberObj;


    private String[] roles;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }
}
