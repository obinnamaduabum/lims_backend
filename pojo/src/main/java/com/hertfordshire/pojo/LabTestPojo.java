package com.hertfordshire.pojo;


import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;

public class LabTestPojo {

    private Long id;

    private String categoryName;

    private Long categoryId;

    private CurrencyTypeConstant currencyType;

    private String name;

    private String price;

    private String code;

    public String getName() {
        return name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
