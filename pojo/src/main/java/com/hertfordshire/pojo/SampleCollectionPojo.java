package com.hertfordshire.pojo;

import java.util.Date;

public class SampleCollectionPojo {

    private Long id;

    private String sampleCollected;

    private Long collectedBy;

    private PortalUserPojo collectedByPortalUserPojo;

    private Date dateCreated;

    private Date dateUpdated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(Long collectedBy) {
        this.collectedBy = collectedBy;
    }

    public PortalUserPojo getCollectedByPortalUserPojo() {
        return collectedByPortalUserPojo;
    }

    public void setCollectedByPortalUserPojo(PortalUserPojo collectedByPortalUserPojo) {
        this.collectedByPortalUserPojo = collectedByPortalUserPojo;
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

    public String getSampleCollected() {
        return sampleCollected;
    }

    public void setSampleCollected(String sampleCollected) {
        this.sampleCollected = sampleCollected;
    }
}
