package com.hertfordshire.pojo;

import com.hertfordshire.pojo.report.LabTestDetailsReportPojo;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.List;

public class LabTestOrderPojoReport {

    private Long id;

    private Long position;

    private String code;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private List<LabTestDetailsReportPojo> labTestDetailsReportPojoList;

    private JRBeanCollectionDataSource labTestDetailsDataSource;

    private String currencyType;

    private String price;

    private String cashCollected;

    private PaymentTransactionPojo paymentTransactionModel;

    private String dateCreated;

    private String dateUpdated;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<LabTestDetailsReportPojo> getLabTestDetailsReportPojoList() {
        return labTestDetailsReportPojoList;
    }

    public void setLabTestDetailsReportPojoList(List<LabTestDetailsReportPojo> labTestDetailsReportPojoList) {
        this.labTestDetailsReportPojoList = labTestDetailsReportPojoList;
    }

    public PaymentTransactionPojo getPaymentTransactionModel() {
        return paymentTransactionModel;
    }

    public void setPaymentTransactionModel(PaymentTransactionPojo paymentTransactionModel) {
        this.paymentTransactionModel = paymentTransactionModel;
    }

    public JRBeanCollectionDataSource getLabTestDetailsDataSource() {
        return new JRBeanCollectionDataSource(labTestDetailsReportPojoList, false);
    }

    public void setLabTestDetailsDataSource(JRBeanCollectionDataSource labTestDetailsDataSource) {
        this.labTestDetailsDataSource = labTestDetailsDataSource;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCashCollected() {
        return cashCollected;
    }

    public void setCashCollected(String cashCollected) {
        this.cashCollected = cashCollected;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
