package com.hertfordshire.model.psql;

import com.hertfordshire.utils.lhenum.LabScientistStatusConstant;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "lab_scientist_test_result")
public class LabScientistTestResultModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sample_collected_id", referencedColumnName = "id")
    private SampleCollectedModel sampleCollectedModel;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medical_Lab_scientist_id", referencedColumnName = "id")
    private PortalUser medicalLabScientist;

    @Enumerated(EnumType.STRING)
    private LabScientistStatusConstant labScientistStatusConstant;

    @NotBlank
    private String labResultId;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lab_test_id", referencedColumnName = "id")
    private LabTest labTest;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    private Date dateCreated = new Date();

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_updated")
    private Date dateUpdated = new Date();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SampleCollectedModel getSampleCollectedModel() {
        return sampleCollectedModel;
    }

    public void setSampleCollectedModel(SampleCollectedModel sampleCollectedModel) {
        this.sampleCollectedModel = sampleCollectedModel;
    }

    public PortalUser getMedicalLabScientist() {
        return medicalLabScientist;
    }

    public void setMedicalLabScientist(PortalUser medicalLabScientist) {
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

    public LabScientistStatusConstant getLabScientistStatusConstant() {
        return labScientistStatusConstant;
    }

    public void setLabScientistStatusConstant(LabScientistStatusConstant labScientistStatusConstant) {
        this.labScientistStatusConstant = labScientistStatusConstant;
    }

    public String getLabResultId() {
        return labResultId;
    }

    public void setLabResultId(String labResultId) {
        this.labResultId = labResultId;
    }

    public LabTest getLabTest() {
        return labTest;
    }

    public void setLabTest(LabTest labTest) {
        this.labTest = labTest;
    }
}
