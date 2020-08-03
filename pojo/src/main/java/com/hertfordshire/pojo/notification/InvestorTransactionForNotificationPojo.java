package com.hertfordshire.pojo.notification;

public class InvestorTransactionForNotificationPojo {

    private String fileCode;

    private String amount;

    private String transactionTypeConstant;

    private String statusConstant;

    public String getFileCode() {
        return fileCode;
    }

    public void setFileCode(String fileCode) {
        this.fileCode = fileCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionTypeConstant() {
        return transactionTypeConstant;
    }

    public void setTransactionTypeConstant(String transactionTypeConstant) {
        this.transactionTypeConstant = transactionTypeConstant;
    }

    public String getStatusConstant() {
        return statusConstant;
    }

    public void setStatusConstant(String statusConstant) {
        this.statusConstant = statusConstant;
    }
}
