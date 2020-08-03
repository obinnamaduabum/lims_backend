package com.hertfordshire.pojo;

public class LabTestDetailsPojo {

    private Long id;

    private Long labTestId;

    private String name;

    private Long labTestOrderId;

    private Long quantity;

    private Long total;

    private Long price;

    private String uniqueId;

    private InstitutionPatientInfoPojo institutionPatientInfoPojo;

    private SampleCollectionPojo sampleCollectionPojo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLabTestId() {
        return labTestId;
    }

    public void setLabTestId(Long labTestId) {
        this.labTestId = labTestId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLabTestOrderId() {
        return labTestOrderId;
    }

    public void setLabTestOrderId(Long labTestOrderId) {
        this.labTestOrderId = labTestOrderId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public SampleCollectionPojo getSampleCollectionPojo() {
        return sampleCollectionPojo;
    }

    public void setSampleCollectionPojo(SampleCollectionPojo sampleCollectionPojo) {
        this.sampleCollectionPojo = sampleCollectionPojo;
    }

    public InstitutionPatientInfoPojo getInstitutionPatientInfoPojo() {
        return institutionPatientInfoPojo;
    }

    public void setInstitutionPatientInfoPojo(InstitutionPatientInfoPojo institutionPatientInfoPojo) {
        this.institutionPatientInfoPojo = institutionPatientInfoPojo;
    }
}
