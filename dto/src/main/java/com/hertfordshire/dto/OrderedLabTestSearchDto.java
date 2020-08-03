package com.hertfordshire.dto;



import com.hertfordshire.utils.pojo.PhoneCodesPojo;

import java.util.Date;

public class OrderedLabTestSearchDto {

    private String code;

    private String fullName;

    private String phoneNumber;

    private String email;

    private PhoneCodesPojo selectedPhoneNumber;

    private Date startDate;

    private Date endDate;

    private String orderId;

    private String sampleCollectedStatus;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PhoneCodesPojo getSelectedPhoneNumber() {
        return selectedPhoneNumber;
    }

    public void setSelectedPhoneNumber(PhoneCodesPojo selectedPhoneNumber) {
        this.selectedPhoneNumber = selectedPhoneNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSampleCollectedStatus() {
        return sampleCollectedStatus;
    }

    public void setSampleCollectedStatus(String sampleCollectedStatus) {
        this.sampleCollectedStatus = sampleCollectedStatus;
    }
}
