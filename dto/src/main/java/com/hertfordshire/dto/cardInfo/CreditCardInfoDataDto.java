package com.hertfordshire.dto.cardInfo;

public class CreditCardInfoDataDto {

    private String linked_bank_id;
    private String country_code;
    private String bank;
    private String bin;
    private String sub_brand;
    private String country_name;
    private String card_type;
    private String brand;

    public String getLinked_bank_id() {
        return linked_bank_id;
    }

    public void setLinked_bank_id(String linked_bank_id) {
        this.linked_bank_id = linked_bank_id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getSub_brand() {
        return sub_brand;
    }

    public void setSub_brand(String sub_brand) {
        this.sub_brand = sub_brand;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
