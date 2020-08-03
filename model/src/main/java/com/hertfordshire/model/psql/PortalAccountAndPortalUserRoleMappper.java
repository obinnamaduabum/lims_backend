package com.hertfordshire.model.psql;


import com.hertfordshire.utils.lhenum.PortalUserTypeConstant;
import com.hertfordshire.utils.lhenum.RoleTypeConstant;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "portal_account_and_portal_user_role_mapper")
public class PortalAccountAndPortalUserRoleMappper implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private String roleName;

    private Long roleId;

    @Enumerated(EnumType.STRING)
    private RoleTypeConstant roleTypeConstant;

    private String portalAccountCode;

    private String portalUserCode;

    @Enumerated(EnumType.STRING)
    private PortalUserTypeConstant userType;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    private Date dateCreated = new Date();

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_updated")
    private Date dateUpdated  = new Date();


    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPortalUserCode() {
        return portalUserCode;
    }

    public void setPortalUserCode(String portalUserCode) {
        this.portalUserCode = portalUserCode;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getPortalAccountCode() {
        return portalAccountCode;
    }

    public void setPortalAccountCode(String portalAccountCode) {
        this.portalAccountCode = portalAccountCode;
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

    public PortalUserTypeConstant getUserType() {
        return userType;
    }

    public void setUserType(PortalUserTypeConstant userType) {
        this.userType = userType;
    }

    public RoleTypeConstant getRoleTypeConstant() {
        return roleTypeConstant;
    }

    public void setRoleTypeConstant(RoleTypeConstant roleTypeConstant) {
        this.roleTypeConstant = roleTypeConstant;
    }

    @Override
    public String toString() {
        return "PortalAccountAndPortalUserRoleMappper{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", roleId=" + roleId +
                ", portalAccountCode='" + portalAccountCode + '\'' +
                ", portalUserCode='" + portalUserCode + '\'' +
                ", userType=" + userType +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                '}';
    }
}
