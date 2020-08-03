package com.hertfordshire.model.mongodb;


import com.hertfordshire.utils.lhenum.LabTestAssignmentStatusType;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Document
public class LabTestTemplateAssignmentHistoryMongoDb {

    @Id
    private ObjectId _id;

    private String labTestTemplateId;

    private String labTestTemplateName;

    private String actualLabTestId;

    @Enumerated(EnumType.STRING)
    private LabTestAssignmentStatusType LabTestAssignmentStatusType;

    private Date dateCreated;

    private Date dateUpdated;


    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getLabTestTemplateId() {
        return labTestTemplateId;
    }

    public void setLabTestTemplateId(String labTestTemplateId) {
        this.labTestTemplateId = labTestTemplateId;
    }

    public String getLabTestTemplateName() {
        return labTestTemplateName;
    }

    public void setLabTestTemplateName(String labTestTemplateName) {
        this.labTestTemplateName = labTestTemplateName;
    }

    public String getActualLabTestId() {
        return actualLabTestId;
    }

    public void setActualLabTestId(String actualLabTestId) {
        this.actualLabTestId = actualLabTestId;
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

    public LabTestAssignmentStatusType getLabTestAssignmentStatusType() {
        return LabTestAssignmentStatusType;
    }

    public void setLabTestAssignmentStatusType(LabTestAssignmentStatusType labTestAssignmentStatusType) {
        LabTestAssignmentStatusType = labTestAssignmentStatusType;
    }
}
