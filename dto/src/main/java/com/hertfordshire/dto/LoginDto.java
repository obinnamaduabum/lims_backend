package com.hertfordshire.dto;

import javax.validation.constraints.NotBlank;

public class LoginDto {

    @NotBlank
    private String email;

    private String password;

    private String twoFactorCode;

    @NotBlank
    private String signUpTypeConstant;

    public String getSignUpTypeConstant() {
        return signUpTypeConstant;
    }

    public void setSignUpTypeConstant(String signUpTypeConstant) {
        this.signUpTypeConstant = signUpTypeConstant;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTwoFactorCode() {
        return twoFactorCode;
    }

    public void setTwoFactorCode(String twoFactorCode) {
        this.twoFactorCode = twoFactorCode;
    }
}
