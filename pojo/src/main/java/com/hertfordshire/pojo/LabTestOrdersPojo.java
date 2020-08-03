package com.hertfordshire.pojo;


import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;

import java.util.Date;
import java.util.List;

public class LabTestOrdersPojo {

    private Long id;

    private Long position;

    private String code;

    private PortalUserPojo portalUserPojo;

    private List<LabTestPojo> labTestPojoList;

    private List<LabTestDetailsPojo> labTestDetailsPojos;

    private CurrencyTypeConstant currencyType;

    private Long price;

    private boolean cashCollected;

    private PaymentTransactionPojo paymentTransactionModel;

    private String orderedByWhatTypeOfAccount;

    private Date dateCreated;

    private Date dateUpdated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<LabTestPojo> getLabTestPojoList() {
        return labTestPojoList;
    }

    public void setLabTestPojoList(List<LabTestPojo> labTestPojoList) {
        this.labTestPojoList = labTestPojoList;
    }

    public CurrencyTypeConstant getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CurrencyTypeConstant currencyType) {
        this.currencyType = currencyType;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public boolean isCashCollected() {
        return cashCollected;
    }

    public void setCashCollected(boolean cashCollected) {
        this.cashCollected = cashCollected;
    }

    public PaymentTransactionPojo getPaymentTransactionModel() {
        return paymentTransactionModel;
    }

    public void setPaymentTransactionModel(PaymentTransactionPojo paymentTransactionModel) {
        this.paymentTransactionModel = paymentTransactionModel;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public List<LabTestDetailsPojo> getLabTestDetailsPojos() {
        return labTestDetailsPojos;
    }

    public void setLabTestDetailsPojos(List<LabTestDetailsPojo> labTestDetailsPojos) {
        this.labTestDetailsPojos = labTestDetailsPojos;
    }

    public PortalUserPojo getPortalUserPojo() {
        return portalUserPojo;
    }

    public void setPortalUserPojo(PortalUserPojo portalUserPojo) {
        this.portalUserPojo = portalUserPojo;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getOrderedByWhatTypeOfAccount() {
        return orderedByWhatTypeOfAccount;
    }

    public void setOrderedByWhatTypeOfAccount(String orderedByWhatTypeOfAccount) {
        this.orderedByWhatTypeOfAccount = orderedByWhatTypeOfAccount;
    }
}
