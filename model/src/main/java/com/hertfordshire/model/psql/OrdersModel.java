package com.hertfordshire.model.psql;

import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "test_orders")
public class OrdersModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String code;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "test_orders_payment_transaction",
            joinColumns = { @JoinColumn(name = "test_orders_id") },
            inverseJoinColumns = { @JoinColumn(name = "lab_test_id") })
    private Set<LabTest> labTests = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private CurrencyTypeConstant currencyType;

    private Long price;

    private boolean cashCollected;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "portal_user_id", referencedColumnName = "id")
    private PortalUser portalUser;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_transaction_id", referencedColumnName = "id")
    private PaymentTransaction paymentTransactionModel;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cash_collected_by", referencedColumnName = "id")
    private PortalUser cashCollectedBy;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    private Date dateCreated = new Date();


    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_updated")
    private Date dateUpdated = new Date();


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
    }

    public Set<LabTest> getLabTests() {
        return labTests;
    }

    public void setLabTests(Set<LabTest> labTests) {
        this.labTests = labTests;
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

    public PaymentTransaction getPaymentTransactionModel() {
        return paymentTransactionModel;
    }

    public void setPaymentTransactionModel(PaymentTransaction paymentTransactionModel) {
        this.paymentTransactionModel = paymentTransactionModel;
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

    public PortalUser getCashCollectedBy() {
        return cashCollectedBy;
    }

    public void setCashCollectedBy(PortalUser cashCollectedBy) {
        this.cashCollectedBy = cashCollectedBy;
    }
}
