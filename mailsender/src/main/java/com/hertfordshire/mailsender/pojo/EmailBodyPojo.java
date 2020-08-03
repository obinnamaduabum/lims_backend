package com.hertfordshire.mailsender.pojo;

import java.util.List;

public class EmailBodyPojo {

    private String header;
    private String body;
    private String firstName;
    private String lastName;
    private String phonenumber;
    private String email;
    private String subject;
    private String url;
    private List<String> importantInfo;
    private String urlName;
    private boolean hasImportantInfo;
    private String importantInfoHeader;
    private String validityDuration;
    private String username;
    private Long requestId;
    private double amount;
    private String totalWalletAmount;
    private String bitcoinAddress;
    private String amountToBeWithdrawn;
    private String amountInString;
    private int totalProfit;
    private String totalProfitInBTC;


    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getImportantInfo() {
        return importantInfo;
    }

    public void setImportantInfo(List<String> importantInfo) {
        this.importantInfo = importantInfo;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public boolean isHasImportantInfo() {
        return hasImportantInfo;
    }

    public void setHasImportantInfo(boolean hasImportantInfo) {
        this.hasImportantInfo = hasImportantInfo;
    }

    public String getImportantInfoHeader() {
        return importantInfoHeader;
    }

    public void setImportantInfoHeader(String importantInfoHeader) {
        this.importantInfoHeader = importantInfoHeader;
    }

    public String getValidityDuration() {
        return validityDuration;
    }

    public void setValidityDuration(String validityDuration) {
        this.validityDuration = validityDuration;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTotalWalletAmount() {
        return totalWalletAmount;
    }

    public void setTotalWalletAmount(String totalWalletAmount) {
        this.totalWalletAmount = totalWalletAmount;
    }

    public String getBitcoinAddress() {
        return bitcoinAddress;
    }

    public void setBitcoinAddress(String bitcoinAddress) {
        this.bitcoinAddress = bitcoinAddress;
    }

    public String getAmountToBeWithdrawn() {
        return amountToBeWithdrawn;
    }

    public void setAmountToBeWithdrawn(String amountToBeWithdrawn) {
        this.amountToBeWithdrawn = amountToBeWithdrawn;
    }

    public String getAmountInString() {
        return amountInString;
    }

    public void setAmountInString(String amountInString) {
        this.amountInString = amountInString;
    }

    public int getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(int totalProfit) {
        this.totalProfit = totalProfit;
    }

    public String getTotalProfitInBTC() {
        return totalProfitInBTC;
    }

    public void setTotalProfitInBTC(String totalProfitInBTC) {
        this.totalProfitInBTC = totalProfitInBTC;
    }
}
