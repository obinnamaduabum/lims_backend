package com.hertfordshire.pojo;



import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;

import java.util.Date;

public class OrderModelPojo {

    private Long id;

    private String code;

    private CurrencyTypeConstant currencyType;

    private Long price;

    private boolean cashCollected;

//    private PortalUser portalUser;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "payment_transaction_id", referencedColumnName = "id")
//    private PaymentTransaction paymentTransactionModel;


//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "cash_collected_by", referencedColumnName = "id")
//    private PortalUser cashCollectedBy;

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
}
