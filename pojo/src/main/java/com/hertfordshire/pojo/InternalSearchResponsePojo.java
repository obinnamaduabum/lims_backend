package com.hertfordshire.pojo;

import com.hertfordshire.utils.lhenum.SampleTypeConstant;

import java.util.Date;

public class InternalSearchResponsePojo {

    private String email;
    private String fullName;
    private String phoneNumber;
    private String orderId;
    private String code;
    private SampleTypeConstant sampleCollectedStatus;
    private Date startDate;
    private Date endDate;
    private int pageNumber;
    private int pageSize;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SampleTypeConstant getSampleCollectedStatus() {
        return sampleCollectedStatus;
    }

    public void setSampleCollectedStatus(SampleTypeConstant sampleCollectedStatus) {
        this.sampleCollectedStatus = sampleCollectedStatus;
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

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
