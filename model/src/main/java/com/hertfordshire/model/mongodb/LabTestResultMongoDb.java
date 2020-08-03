package com.hertfordshire.model.mongodb;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class LabTestResultMongoDb {

    @Id
    private ObjectId _id;

    private String code;

    private String data;

    private String testOrderSampleUniqueId;

    private String labTestResultTemplateId;

    private Date dateCreated;

    private Date dateUpdated;


    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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

    public String getTestOrderSampleUniqueId() {
        return testOrderSampleUniqueId;
    }

    public void setTestOrderSampleUniqueId(String testOrderSampleUniqueId) {
        this.testOrderSampleUniqueId = testOrderSampleUniqueId;
    }

    public String getLabTestResultTemplateId() {
        return labTestResultTemplateId;
    }

    public void setLabTestResultTemplateId(String labTestResultTemplateId) {
        this.labTestResultTemplateId = labTestResultTemplateId;
    }
}
