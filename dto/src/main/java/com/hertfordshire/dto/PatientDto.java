package com.hertfordshire.dto;

import com.hertfordshire.utils.pojo.PhoneCodesPojo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

public class PatientDto {

    @NotBlank
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    private String email;

    private String otherName;

    @NotBlank
    private String phoneNumber;

    private String otherPhoneNumber;

    private boolean twoFactor;

    @NotBlank
    private String gender;

    private String[] roles;

    private PhoneCodesPojo phoneNumberObj;

    private PhoneCodesPojo otherPhoneNumberObj;

    private PhoneCodesPojo nextOFKinPhoneNumberObj;

    private Date dob;

    private String address;

    private String nextOFKinFirstName;

    private String nextOFKinLastName;

    private String nextOFKinPhoneNumber;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getNextOFKinFirstName() {
        return nextOFKinFirstName;
    }

    public void setNextOFKinFirstName(String nextOFKinFirstName) {
        this.nextOFKinFirstName = nextOFKinFirstName;
    }

    public String getNextOFKinLastName() {
        return nextOFKinLastName;
    }

    public void setNextOFKinLastName(String nextOFKinLastName) {
        this.nextOFKinLastName = nextOFKinLastName;
    }

    public String getNextOFKinPhoneNumber() {
        return nextOFKinPhoneNumber;
    }

    public void setNextOFKinPhoneNumber(String nextOFKinPhoneNumber) {
        this.nextOFKinPhoneNumber = nextOFKinPhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
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

    public PhoneCodesPojo getNextOFKinPhoneNumberObj() {
        return nextOFKinPhoneNumberObj;
    }

    public void setNextOFKinPhoneNumberObj(PhoneCodesPojo nextOFKinPhoneNumberObj) {
        this.nextOFKinPhoneNumberObj = nextOFKinPhoneNumberObj;
    }

    public boolean isTwoFactor() {
        return twoFactor;
    }

    public void setTwoFactor(boolean twoFactor) {
        this.twoFactor = twoFactor;
    }
}
