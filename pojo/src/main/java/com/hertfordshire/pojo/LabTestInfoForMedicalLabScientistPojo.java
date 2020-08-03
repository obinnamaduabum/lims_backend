package com.hertfordshire.pojo;

import java.util.Date;

public class LabTestInfoForMedicalLabScientistPojo {

    private Long id;

    private Long orderId;

    private boolean registeredPatient;

    private String accountType;

    private Long position;

    private LabTestPojo labTest;

    private String name;

    private String uniqueId;

    private String labScientistStatusConstant;

    private OrderModelPojo ordersModel;

    private PortalUserPojo patient;

    private SampleCollectionPojo sampleCollected;

    private String labTestFormId;

    private PortalUserPojo medicalLabScientist;

    private Long medicalLabScientistSampleCollectedId;

    private Date dateCreated;

    private Date dateUpdated;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public boolean isRegisteredPatient() {
        return registeredPatient;
    }

    public void setRegisteredPatient(boolean registeredPatient) {
        this.registeredPatient = registeredPatient;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public LabTestPojo getLabTest() {
        return labTest;
    }

    public void setLabTest(LabTestPojo labTest) {
        this.labTest = labTest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public OrderModelPojo getOrdersModel() {
        return ordersModel;
    }

    public void setOrdersModel(OrderModelPojo ordersModel) {
        this.ordersModel = ordersModel;
    }

    public PortalUserPojo getPatient() {
        return patient;
    }

    public void setPatient(PortalUserPojo patient) {
        this.patient = patient;
    }

    public SampleCollectionPojo getSampleCollected() {
        return sampleCollected;
    }

    public void setSampleCollected(SampleCollectionPojo sampleCollected) {
        this.sampleCollected = sampleCollected;
    }

    public String getLabTestFormId() {
        return labTestFormId;
    }

    public void setLabTestFormId(String labTestFormId) {
        this.labTestFormId = labTestFormId;
    }

    public PortalUserPojo getMedicalLabScientist() {
        return medicalLabScientist;
    }

    public void setMedicalLabScientist(PortalUserPojo medicalLabScientist) {
        this.medicalLabScientist = medicalLabScientist;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Long getMedicalLabScientistSampleCollectedId() {
        return medicalLabScientistSampleCollectedId;
    }

    public void setMedicalLabScientistSampleCollectedId(Long medicalLabScientistSampleCollectedId) {
        this.medicalLabScientistSampleCollectedId = medicalLabScientistSampleCollectedId;
    }

    public String getLabScientistStatusConstant() {
        return labScientistStatusConstant;
    }

    public void setLabScientistStatusConstant(String labScientistStatusConstant) {
        this.labScientistStatusConstant = labScientistStatusConstant;
    }
}
