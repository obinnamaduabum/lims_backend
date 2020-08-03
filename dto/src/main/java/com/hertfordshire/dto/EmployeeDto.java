package com.hertfordshire.dto;


import com.hertfordshire.utils.pojo.PhoneCodesPojo;

import javax.validation.constraints.NotBlank;
import java.util.Date;

public class EmployeeDto {

    private Long employeeId;

    @NotBlank
    private String email;

    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String phoneNumber;

    private String otherPhoneNumber;

    private Date dob;

    private String gender;

    private String address;

    private PhoneCodesPojo selectedPhoneNumber;

    private PhoneCodesPojo selectedOtherPhoneNumber;

    private String nextOfKinFirstName;

    private String nextOfKinLastName;

    private String nextOfKinPhoneNumber;

    private boolean isEmailVerified;

    private boolean isEmailOrPhoneNumberIsVerified;

    private String[] roles;

    private boolean accountBlockedByAdmin;

    private String userStatus;

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNextOfKinFirstName() {
        return nextOfKinFirstName;
    }

    public void setNextOfKinFirstName(String nextOfKinFirstName) {
        this.nextOfKinFirstName = nextOfKinFirstName;
    }

    public String getNextOfKinLastName() {
        return nextOfKinLastName;
    }

    public void setNextOfKinLastName(String nextOfKinLastName) {
        this.nextOfKinLastName = nextOfKinLastName;
    }

    public String getNextOfKinPhoneNumber() {
        return nextOfKinPhoneNumber;
    }

    public void setNextOfKinPhoneNumber(String nextOfKinPhoneNumber) {
        this.nextOfKinPhoneNumber = nextOfKinPhoneNumber;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public boolean isEmailOrPhoneNumberIsVerified() {
        return isEmailOrPhoneNumberIsVerified;
    }

    public void setEmailOrPhoneNumberIsVerified(boolean emailOrPhoneNumberIsVerified) {
        isEmailOrPhoneNumberIsVerified = emailOrPhoneNumberIsVerified;
    }

    public PhoneCodesPojo getSelectedPhoneNumber() {
        return selectedPhoneNumber;
    }

    public void setSelectedPhoneNumber(PhoneCodesPojo selectedPhoneNumber) {
        this.selectedPhoneNumber = selectedPhoneNumber;
    }

    public PhoneCodesPojo getSelectedOtherPhoneNumber() {
        return selectedOtherPhoneNumber;
    }

    public void setSelectedOtherPhoneNumber(PhoneCodesPojo selectedOtherPhoneNumber) {
        this.selectedOtherPhoneNumber = selectedOtherPhoneNumber;
    }

    public boolean isAccountBlockedByAdmin() {
        return accountBlockedByAdmin;
    }

    public void setAccountBlockedByAdmin(boolean accountBlockedByAdmin) {
        this.accountBlockedByAdmin = accountBlockedByAdmin;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}
