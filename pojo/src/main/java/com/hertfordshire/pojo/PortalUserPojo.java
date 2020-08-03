package com.hertfordshire.pojo;




import com.hertfordshire.utils.pojo.ProperPhoneNumberPojo;

import java.util.List;

public class PortalUserPojo {

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String otherName;

    private String username;

    private boolean isEmailVerified;

    private String signUpType;

    private String[] roles;

    private List<RolePojo> rolesPojo;

    private String code;

    private String portalAccountCode;

    private boolean isPhoneNumberVerified;

    private String phoneNumber;

    private ProperPhoneNumberPojo phoneNumberObj;

    private String otherPhoneNumber;

    private ProperPhoneNumberPojo otherPhoneNumberObj;

    private boolean accountBlockedByAdmin;

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

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ProperPhoneNumberPojo getPhoneNumberObj() {
        return phoneNumberObj;
    }

    public void setPhoneNumberObj(ProperPhoneNumberPojo phoneNumberObj) {
        this.phoneNumberObj = phoneNumberObj;
    }

    public ProperPhoneNumberPojo getOtherPhoneNumberObj() {
        return otherPhoneNumberObj;
    }

    public void setOtherPhoneNumberObj(ProperPhoneNumberPojo otherPhoneNumberObj) {
        this.otherPhoneNumberObj = otherPhoneNumberObj;
    }

    public List<RolePojo> getRolesPojo() {
        return rolesPojo;
    }

    public void setRolesPojo(List<RolePojo> rolesPojo) {
        this.rolesPojo = rolesPojo;
    }

    public boolean isAccountBlockedByAdmin() {
        return accountBlockedByAdmin;
    }

    public void setAccountBlockedByAdmin(boolean accountBlockedByAdmin) {
        this.accountBlockedByAdmin = accountBlockedByAdmin;
    }
}
