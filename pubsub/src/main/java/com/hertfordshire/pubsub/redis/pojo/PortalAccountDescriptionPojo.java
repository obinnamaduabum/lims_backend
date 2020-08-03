package com.hertfordshire.pubsub.redis.pojo;


import java.io.Serializable;
import java.util.List;

public class PortalAccountDescriptionPojo implements Serializable {

    private String portalAccountTypeConstant;

    private String portalUserTypeConstant;

    private List<String> roleName;


    public String getPortalAccountTypeConstant() {
        return portalAccountTypeConstant;
    }

    public void setPortalAccountTypeConstant(String portalAccountTypeConstant) {
        this.portalAccountTypeConstant = portalAccountTypeConstant;
    }

    public String getPortalUserTypeConstant() {
        return portalUserTypeConstant;
    }

    public void setPortalUserTypeConstant(String portalUserTypeConstant) {
        this.portalUserTypeConstant = portalUserTypeConstant;
    }

    public List<String> getRoleName() {
        return roleName;
    }

    public void setRoleName(List<String> roleName) {
        this.roleName = roleName;
    }
}