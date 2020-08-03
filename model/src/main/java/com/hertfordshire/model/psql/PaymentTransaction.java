package com.hertfordshire.model.psql;


import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;
import com.hertfordshire.utils.lhenum.PaymentMethodConstant;
import com.hertfordshire.utils.lhenum.TransactionTypeConstant;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "payment_transaction")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String transactionRef;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "portal_user_id", referencedColumnName = "id")
    private PortalUser portalUser;

    private boolean cashCollected;

    @Enumerated(EnumType.STRING)
    private CurrencyTypeConstant currencyTypeConstant;

    private Long amount;

    private boolean wasPaymentSuccessful;

    private boolean isPaymentVerified;

    @Enumerated(EnumType.STRING)
    private PaymentMethodConstant paymentMethodConstant;

    @Enumerated(EnumType.STRING)
    private TransactionTypeConstant transactionTypeConstant;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    private Date dateCreated = new Date();


    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_updated")
    private Date dateUpdated = new Date();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public PaymentMethodConstant getPaymentMethodConstant() {
        return paymentMethodConstant;
    }

    public void setPaymentMethodConstant(PaymentMethodConstant paymentMethodConstant) {
        this.paymentMethodConstant = paymentMethodConstant;
    }

    public CurrencyTypeConstant getCurrencyTypeConstant() {
        return currencyTypeConstant;
    }

    public void setCurrencyTypeConstant(CurrencyTypeConstant currencyTypeConstant) {
        this.currencyTypeConstant = currencyTypeConstant;
    }

    public TransactionTypeConstant getTransactionTypeConstant() {
        return transactionTypeConstant;
    }

    public void setTransactionTypeConstant(TransactionTypeConstant transactionTypeConstant) {
        this.transactionTypeConstant = transactionTypeConstant;
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

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public boolean isCashCollected() {
        return cashCollected;
    }

    public void setCashCollected(boolean cashCollected) {
        this.cashCollected = cashCollected;
    }

    public PortalUser getPortalUser() {
        return portalUser;
    }

    public void setPortalUser(PortalUser portalUser) {
        this.portalUser = portalUser;
    }

    public boolean isWasPaymentSuccessful() {
        return wasPaymentSuccessful;
    }

    public void setWasPaymentSuccessful(boolean wasPaymentSuccessful) {
        this.wasPaymentSuccessful = wasPaymentSuccessful;
    }

    public boolean isPaymentVerified() {
        return isPaymentVerified;
    }

    public void setPaymentVerified(boolean paymentVerified) {
        isPaymentVerified = paymentVerified;
    }
}
