package com.hertfordshire.dto;

import org.springframework.beans.factory.annotation.Autowired;

public class LabTestAssignmentDto {

    @Autowired
    private Long actualLabTestId;

    @Autowired
    private String labTestTemplateId;

    public Long getActualLabTestId() {
        return actualLabTestId;
    }

    public void setActualLabTestId(Long actualLabTestId) {
        this.actualLabTestId = actualLabTestId;
    }

    public String getLabTestTemplateId() {
        return labTestTemplateId;
    }

    public void setLabTestTemplateId(String labTestTemplateId) {
        this.labTestTemplateId = labTestTemplateId;
    }
}
