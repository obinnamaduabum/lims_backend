package com.hertfordshire.model.psql;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity()
@Table(name = "lab_test_template")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class LabTestTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private String content;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "created_by_id", referencedColumnName = "id")
    private PortalUser createdBy;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "portal_user_lab_test_template",
            joinColumns = { @JoinColumn(name = "portal_user_id") },
            inverseJoinColumns = { @JoinColumn(name = "lab_test_template_id") })
    private Set<PortalUser> assignedByList = new HashSet<>();


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LabTest getLabTest() {
        return labTest;
    }

    public void setLabTest(LabTest labTest) {
        this.labTest = labTest;
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

    public PortalUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(PortalUser createdBy) {
        this.createdBy = createdBy;
    }

    public Set<PortalUser> getAssignedByList() {
        return assignedByList;
    }

    public void setAssignedByList(Set<PortalUser> assignedByList) {
        this.assignedByList = assignedByList;
    }
}