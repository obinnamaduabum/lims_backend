package com.hertfordshire.model.psql;


import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity()
@Table(name = "lab_result_test")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class LabTestResultModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private String content;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lab_scientist_test_result_id", referencedColumnName = "id")
    private LabScientistTestResultModel labScientistTestResult;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LabScientistTestResultModel getLabScientistTestResult() {
        return labScientistTestResult;
    }

    public void setLabScientistTestResult(LabScientistTestResultModel labScientistTestResult) {
        this.labScientistTestResult = labScientistTestResult;
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
}
