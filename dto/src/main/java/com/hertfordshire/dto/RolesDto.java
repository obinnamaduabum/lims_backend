package com.hertfordshire.dto;



import javax.validation.constraints.NotBlank;
import java.util.ArrayList;

public class RolesDto {


    @NotBlank
    private String name;

    @NotBlank
    private String roleType;

    private ArrayList<PrivilegeDto> privileges;

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<PrivilegeDto> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(ArrayList<PrivilegeDto> privileges) {
        this.privileges = privileges;
    }
}