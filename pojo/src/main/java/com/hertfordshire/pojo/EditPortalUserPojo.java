package com.hertfordshire.pojo;

import java.util.List;

public class EditPortalUserPojo {

    private String email;

    private String code;

    private String password;

    private String firstName;

    private String lastName;

    private String otherName;

    private String username;

    private boolean isEmailVerified;

    private String signUpType;

    private List<RolePojo> roles;

    private String portalAccountCode;

    private boolean isPhoneNumberVerified;

    private String phoneNumber;

    private String otherPhoneNumber;

    private InternationalPhoneNumberPojo internationalPhoneNumberPojo;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public String getSignUpType() {
        return signUpType;
    }

    public void setSignUpType(String signUpType) {
        this.signUpType = signUpType;
    }

    public List<RolePojo> getRoles() {
        return roles;
    }

    public void setRoles(List<RolePojo> roles) {
        this.roles = roles;
    }

    public String getPortalAccountCode() {
        return portalAccountCode;
    }

    public void setPortalAccountCode(String portalAccountCode) {
        this.portalAccountCode = portalAccountCode;
    }

    public boolean isPhoneNumberVerified() {
        return isPhoneNumberVerified;
    }

    public void setPhoneNumberVerified(boolean phoneNumberVerified) {
        isPhoneNumberVerified = phoneNumberVerified;
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

    public InternationalPhoneNumberPojo getInternationalPhoneNumberPojo() {
        return internationalPhoneNumberPojo;
    }

    public void setInternationalPhoneNumberPojo(InternationalPhoneNumberPojo internationalPhoneNumberPojo) {
        this.internationalPhoneNumberPojo = internationalPhoneNumberPojo;
    }
}
