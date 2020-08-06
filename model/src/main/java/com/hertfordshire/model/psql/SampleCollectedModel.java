package com.hertfordshire.model.psql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hertfordshire.utils.lhenum.SampleTypeConstant;

import javax.validation.constraints.NotNull;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "sample_collected")
public class SampleCollectedModel {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SampleTypeConstant sampleCollected;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_test_order_detail_id", referencedColumnName = "id")
    @JsonIgnore
    private LabTestOrderDetail labTestOrderDetail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_by", referencedColumnName = "id")
    @JsonIgnore
    private PortalUser collectedBy;

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

    public SampleTypeConstant getSampleCollected() {
        return sampleCollected;
    }

    public void setSampleCollected(SampleTypeConstant sampleCollected) {
        this.sampleCollected = sampleCollected;
    }

    public PortalUser getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(PortalUser collectedBy) {
        this.collectedBy = collectedBy;
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

    public LabTestOrderDetail getLabTestOrderDetail() {
        return labTestOrderDetail;
    }

    public void setLabTestOrderDetail(LabTestOrderDetail labTestOrderDetail) {
        this.labTestOrderDetail = labTestOrderDetail;
    }
}
