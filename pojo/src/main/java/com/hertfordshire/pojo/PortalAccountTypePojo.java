package com.hertfordshire.pojo;




import com.hertfordshire.utils.lhenum.PortalAccountTypeConstant;

import java.util.Date;

public class PortalAccountTypePojo {

    private Long id;

    private String name;

    private PortalAccountTypeConstant type;

    private Date dateCreated;

    private Date dateUpdated;

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

    public PortalAccountTypeConstant getType() {
        return type;
    }

    public void setType(PortalAccountTypeConstant type) {
        this.type = type;
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
