package com.hertfordshire.dto;

import javax.validation.constraints.NotBlank;

public class PatientResultDto {

    @NotBlank
    private String id;

    @NotBlank
    private Object data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
