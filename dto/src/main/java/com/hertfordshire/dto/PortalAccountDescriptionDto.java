package com.hertfordshire.dto;



import com.hertfordshire.utils.lhenum.PortalAccountTypeConstant;
import com.hertfordshire.utils.lhenum.PortalUserTypeConstant;

import java.util.List;

public class PortalAccountDescriptionDto {

    private PortalAccountTypeConstant portalAccountTypeConstant;

    private PortalUserTypeConstant portalUserTypeConstant;

    private List<String> roleName;


    public PortalUserTypeConstant getPortalUserTypeConstant() {
        return portalUserTypeConstant;
    }

    public void setPortalUserTypeConstant(PortalUserTypeConstant portalUserTypeConstant) {
        this.portalUserTypeConstant = portalUserTypeConstant;
    }

    public List<String> getRoleName() {
        return roleName;
    }

    public void setRoleName(List<String> roleName) {
        this.roleName = roleName;
    }

    public PortalAccountTypeConstant getPortalAccountTypeConstant() {
        return portalAccountTypeConstant;
    }

    public void setPortalAccountTypeConstant(PortalAccountTypeConstant portalAccountTypeConstant) {
        this.portalAccountTypeConstant = portalAccountTypeConstant;
    }
}
