package com.hertfordshire.pubsub.redis.model;

import java.io.Serializable;
import java.util.Date;

public class PaymentMethodInfoModel implements Serializable {

    private Long id;

    private String paymentMethodName;

    private String testingPublicKey;

    private String testingSecret;

    private String testingVerifyUrl;

    private String livePublicKey;

    private String liveSecret;

    private String liveVerifyUrl;

    private boolean isLive;

    private boolean liveActive;

    private boolean enabled;

    private Date dateCreated;

    private Date dateUpdated;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    public String getTestingPublicKey() {
        return testingPublicKey;
    }

    public void setTestingPublicKey(String testingPublicKey) {
        this.testingPublicKey = testingPublicKey;
    }

    public String getTestingSecret() {
        return testingSecret;
    }

    public void setTestingSecret(String testingSecret) {
        this.testingSecret = testingSecret;
    }

    public String getTestingVerifyUrl() {
        return testingVerifyUrl;
    }

    public void setTestingVerifyUrl(String testingVerifyUrl) {
        this.testingVerifyUrl = testingVerifyUrl;
    }

    public String getLivePublicKey() {
        return livePublicKey;
    }

    public void setLivePublicKey(String livePublicKey) {
        this.livePublicKey = livePublicKey;
    }

    public String getLiveSecret() {
        return liveSecret;
    }

    public void setLiveSecret(String liveSecret) {
        this.liveSecret = liveSecret;
    }

    public String getLiveVerifyUrl() {
        return liveVerifyUrl;
    }

    public void setLiveVerifyUrl(String liveVerifyUrl) {
        this.liveVerifyUrl = liveVerifyUrl;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public boolean isLiveActive() {
        return liveActive;
    }

    public void setLiveActive(boolean liveActive) {
        this.liveActive = liveActive;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    @Override
    public String toString() {
        return "PaymentMethodInfoModel{" +
                "id=" + id +
                ", paymentMethodName='" + paymentMethodName + '\'' +
                ", testingPublicKey='" + testingPublicKey + '\'' +
                ", testingSecret='" + testingSecret + '\'' +
                ", testingVerifyUrl='" + testingVerifyUrl + '\'' +
                ", livePublicKey='" + livePublicKey + '\'' +
                ", liveSecret='" + liveSecret + '\'' +
                ", liveVerifyUrl='" + liveVerifyUrl + '\'' +
                ", isLive=" + isLive +
                ", liveActive=" + liveActive +
                ", enabled=" + enabled +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                '}';
    }
}
