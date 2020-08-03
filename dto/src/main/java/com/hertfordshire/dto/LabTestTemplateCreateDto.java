package com.hertfordshire.dto;

import javax.validation.constraints.NotBlank;

public class LabTestTemplateCreateDto {

    @NotBlank
    private String title;

    @NotBlank
    private String data;

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
