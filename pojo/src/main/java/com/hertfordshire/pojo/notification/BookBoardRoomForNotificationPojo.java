package com.hertfordshire.pojo.notification;

import java.util.Date;

public class BookBoardRoomForNotificationPojo {

    private Long id;

    private String bookBoardRoomId;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private Date startDate;

    private Date endDate;

    private boolean wasPaymentSuccessful;

    private int numberOfDays;

    private String amount;

    private String transactionRef;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookBoardRoomId() {
        return bookBoardRoomId;
    }

    public void setBookBoardRoomId(String bookBoardRoomId) {
        this.bookBoardRoomId = bookBoardRoomId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public boolean isWasPaymentSuccessful() {
        return wasPaymentSuccessful;
    }

    public void setWasPaymentSuccessful(boolean wasPaymentSuccessful) {
        this.wasPaymentSuccessful = wasPaymentSuccessful;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }
}
