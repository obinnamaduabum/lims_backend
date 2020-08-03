package com.hertfordshire.model.psql;


import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;

import javax.persistence.*;

import javax.persistence.*;

@Entity
public class AdminSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private boolean dropBoxActive;

    private boolean dataStorageProduction;

    @Enumerated(EnumType.STRING)
    private CurrencyTypeConstant currencyType;

    @Column(nullable = false, columnDefinition = "int default 3")
    private int accountVerificationSmsCount;

    @Column(nullable = false, columnDefinition = "int default 24")
    private int afterHoursPermitSendingOfSms;

    @Column(nullable = false, columnDefinition = "int default 24")
    private int afterHoursPermitUserLoginAfterFailedAttempts;

    @Column(nullable = false, columnDefinition = "int default 960")
    private int multiTexterNumberOfUnits;

    @Column(nullable = false, columnDefinition = "int default 2")
    private int multiTexterUnitsPerText;

    @Column(nullable = false, columnDefinition = "int default 3")
    private int numberOfLoginAttemptsAllowedForAUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isDropBoxActive() {
        return dropBoxActive;
    }

    public void setDropBoxActive(boolean dropBoxActive) {
        this.dropBoxActive = dropBoxActive;
    }

    public boolean isDataStorageProduction() {
        return dataStorageProduction;
    }

    public void setDataStorageProduction(boolean dataStorageProduction) {
        this.dataStorageProduction = dataStorageProduction;
    }

    public CurrencyTypeConstant getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CurrencyTypeConstant currencyType) {
        this.currencyType = currencyType;
    }

    public int getAccountVerificationSmsCount() {
        return accountVerificationSmsCount;
    }

    public void setAccountVerificationSmsCount(int accountVerificationSmsCount) {
        this.accountVerificationSmsCount = accountVerificationSmsCount;
    }

    public int getAfterHoursPermitSendingOfSms() {
        return afterHoursPermitSendingOfSms;
    }

    public void setAfterHoursPermitSendingOfSms(int afterHoursPermitSendingOfSms) {
        this.afterHoursPermitSendingOfSms = afterHoursPermitSendingOfSms;
    }

    public int getMultiTexterUnitsPerText() {
        return multiTexterUnitsPerText;
    }

    public void setMultiTexterUnitsPerText(int multiTexterUnitsPerText) {
        this.multiTexterUnitsPerText = multiTexterUnitsPerText;
    }

    public int getNumberOfLoginAttemptsAllowedForAUser() {
        return numberOfLoginAttemptsAllowedForAUser;
    }

    public void setNumberOfLoginAttemptsAllowedForAUser(int numberOfLoginAttemptsAllowedForAUser) {
        this.numberOfLoginAttemptsAllowedForAUser = numberOfLoginAttemptsAllowedForAUser;
    }

    public int getAfterHoursPermitUserLoginAfterFailedAttempts() {
        return afterHoursPermitUserLoginAfterFailedAttempts;
    }

    public void setAfterHoursPermitUserLoginAfterFailedAttempts(int afterHoursPermitUserLoginAfterFailedAttempts) {
        this.afterHoursPermitUserLoginAfterFailedAttempts = afterHoursPermitUserLoginAfterFailedAttempts;
    }

    public int getMultiTexterNumberOfUnits() {
        return multiTexterNumberOfUnits;
    }

    public void setMultiTexterNumberOfUnits(int multiTexterNumberOfUnits) {
        this.multiTexterNumberOfUnits = multiTexterNumberOfUnits;
    }
}
