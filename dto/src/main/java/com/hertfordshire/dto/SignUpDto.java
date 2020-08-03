package com.hertfordshire.dto;

public class SignUpDto {

    private PortalAccountDto portalAccount;

    private PortalUserDto portalUser;

    public PortalAccountDto getPortalAccount() {
        return portalAccount;
    }

    public void setPortalAccount(PortalAccountDto portalAccount) {
        this.portalAccount = portalAccount;
    }

    public PortalUserDto getPortalUser() {
        return portalUser;
    }

    public void setPortalUser(PortalUserDto portalUser) {
        this.portalUser = portalUser;
    }
}
