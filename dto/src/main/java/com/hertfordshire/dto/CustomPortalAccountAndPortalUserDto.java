package com.hertfordshire.dto;

public class CustomPortalAccountAndPortalUserDto {

    private PortalAccountDto portalAccount;

    private EmployeeDto portalUser;

    public PortalAccountDto getPortalAccount() {
        return portalAccount;
    }

    public void setPortalAccount(PortalAccountDto portalAccount) {
        this.portalAccount = portalAccount;
    }

    public EmployeeDto getPortalUser() {
        return portalUser;
    }

    public void setPortalUser(EmployeeDto portalUser) {
        this.portalUser = portalUser;
    }
}
