package com.hertfordshire.dto;

import javax.validation.constraints.NotBlank;

public class CheckOldPasswordDto {

    @NotBlank
    private String oldPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
