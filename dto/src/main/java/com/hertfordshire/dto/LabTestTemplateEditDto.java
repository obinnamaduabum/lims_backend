package com.hertfordshire.dto;

import javax.validation.constraints.NotBlank;

public class LabTestTemplateEditDto {

    @NotBlank
    private String code;

    @NotBlank
    private String title;

    @NotBlank
    private String data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
