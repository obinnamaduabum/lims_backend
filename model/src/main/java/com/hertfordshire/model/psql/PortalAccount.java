package com.hertfordshire.model.psql;


import com.hertfordshire.utils.lhenum.PortalAccountTypeConstant;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "portal_account")
public class PortalAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String code;


    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }, mappedBy = "portalAccounts")
    private Set<PortalUser> portalUsers = new HashSet<>();


    @Enumerated(EnumType.STRING)
    private PortalAccountTypeConstant portalAccountType;

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

    public Set<PortalUser> getPortalUsers() {
        return portalUsers;
    }

    public void setPortalUsers(Set<PortalUser> portalUsers) {
        this.portalUsers = portalUsers;
    }

    public PortalAccountTypeConstant getPortalAccountType() {
        return portalAccountType;
    }

    public void setPortalAccountType(PortalAccountTypeConstant portalAccountType) {
        this.portalAccountType = portalAccountType;
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

    @Override
    public String toString() {
        return "PortalAccount{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", portalUsers=" + portalUsers +
                ", portalAccountType=" + portalAccountType +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                '}';
    }
}
