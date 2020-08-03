package com.hertfordshire.dto;

import javax.validation.constraints.NotBlank;

public class DeactivateEmployeeDto {

    @NotBlank
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
