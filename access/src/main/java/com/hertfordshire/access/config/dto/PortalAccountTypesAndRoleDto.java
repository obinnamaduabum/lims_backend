package com.hertfordshire.access.config.dto;


import com.hertfordshire.dto.PortalAccountDescriptionDto;
import com.hertfordshire.utils.lhenum.PortalUserTypeConstant;

import java.util.List;

public class PortalAccountTypesAndRoleDto {

    private String portalAccountName;

    private PortalUserTypeConstant portalUserTypeConstant;

    private List<PortalAccountDescriptionDto> portalAccountDescriptionDtoList;

    public PortalUserTypeConstant getPortalUserTypeConstant() {
        return portalUserTypeConstant;
    }

    public void setPortalUserTypeConstant(PortalUserTypeConstant portalUserTypeConstant) {
        this.portalUserTypeConstant = portalUserTypeConstant;
    }

    public String getPortalAccountName() {
        return portalAccountName;
    }

    public void setPortalAccountName(String portalAccountName) {
        this.portalAccountName = portalAccountName;
    }

    public List<PortalAccountDescriptionDto> getPortalAccountDescriptionDtoList() {
        return portalAccountDescriptionDtoList;
    }

    public void setPortalAccountDescriptionDtoList(List<PortalAccountDescriptionDto> portalAccountDescriptionDtoList) {
        this.portalAccountDescriptionDtoList = portalAccountDescriptionDtoList;
    }
}
