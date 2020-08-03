package com.hertfordshire.dto;


import com.hertfordshire.utils.pojo.PhoneCodesPojo;

public class OrderDetailsDto {

    private Long id;

    private String name;

    private String quantity;

    private String price;

    private String total;

    private String fileNumber;

    private String lastName;

    private String firstName;

    private String otherName;

    private String phoneNumber;

    private PhoneCodesPojo selectedPhoneNumber;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PhoneCodesPojo getSelectedPhoneNumber() {
        return selectedPhoneNumber;
    }

    public void setSelectedPhoneNumber(PhoneCodesPojo selectedPhoneNumber) {
        this.selectedPhoneNumber = selectedPhoneNumber;
    }
}
