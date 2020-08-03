package com.hertfordshire.dto;

public class LabTestDto {

    private String priceInNaira;

    private String priceInUSD;

    private String priceInEuro;

    private String categoryName;

    private String name;

    public String getPriceInNaira() {
        return priceInNaira;
    }

    public void setPriceInNaira(String priceInNaira) {
        this.priceInNaira = priceInNaira;
    }

    public String getPriceInUSD() {
        return priceInUSD;
    }

    public void setPriceInUSD(String priceInUSD) {
        this.priceInUSD = priceInUSD;
    }

    public String getPriceInEuro() {
        return priceInEuro;
    }

    public void setPriceInEuro(String priceInEuro) {
        this.priceInEuro = priceInEuro;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
