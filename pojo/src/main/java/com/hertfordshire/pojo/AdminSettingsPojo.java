package com.hertfordshire.pojo;

public class AdminSettingsPojo {

    private boolean dropBoxActive;

    private boolean dataStorageProduction;

    private String currencyType;

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

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }
}
