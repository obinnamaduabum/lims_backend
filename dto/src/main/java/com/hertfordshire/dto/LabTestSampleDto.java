package com.hertfordshire.dto;

import javax.validation.constraints.NotBlank;

public class LabTestSampleDto {

    @NotBlank
    private String uniqueId;

    private Long labTestId;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Long getLabTestId() {
        return labTestId;
    }

    public void setLabTestId(Long labTestId) {
        this.labTestId = labTestId;
    }
}
