package com.hertfordshire.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PatientRestDto {

    @NotBlank
    private String restTemplateId;

    @NotBlank
    private String patientSampleId;

    private Long medicalLabScientistSampleCollectedId;

    @NotNull
    private Object data;

    public String getRestTemplateId() {
        return restTemplateId;
    }

    public void setRestTemplateId(String restTemplateId) {
        this.restTemplateId = restTemplateId;
    }

    public String getPatientSampleId() {
        return patientSampleId;
    }

    public void setPatientSampleId(String patientSampleId) {
        this.patientSampleId = patientSampleId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getMedicalLabScientistSampleCollectedId() {
        return medicalLabScientistSampleCollectedId;
    }

    public void setMedicalLabScientistSampleCollectedId(Long medicalLabScientistSampleCollectedId) {
        this.medicalLabScientistSampleCollectedId = medicalLabScientistSampleCollectedId;
    }
}
