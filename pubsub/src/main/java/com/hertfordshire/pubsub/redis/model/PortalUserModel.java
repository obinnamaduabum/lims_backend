package com.hertfordshire.pubsub.redis.model;


import com.hertfordshire.model.psql.Role;
import com.hertfordshire.pubsub.redis.pojo.PortalAccountDescriptionPojo;
import com.hertfordshire.utils.lhenum.DefaultProfileTypeConstant;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;


@RedisHash("PortalUserModel")
public class PortalUserModel implements Serializable {

    private Long id;
    private String lastName;
    private String firstName;
    private String otherName;
    private String phoneNumber;
    private String profileImage;
    private String email;
    private String portalAccountId;
    private boolean enabled;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonLocked = true;
    private List<Role> roles;
    private DefaultProfileTypeConstant defaultProfileTypeConstant;
    private String otherPhoneNumber;
    private List<PortalAccountDescriptionPojo> portalAccountDescriptionDtoList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPortalAccountId() {
        return portalAccountId;
    }

    public void setPortalAccountId(String portalAccountId) {
        this.portalAccountId = portalAccountId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public DefaultProfileTypeConstant getDefaultProfileTypeConstant() {
        return defaultProfileTypeConstant;
    }

    public void setDefaultProfileTypeConstant(DefaultProfileTypeConstant defaultProfileTypeConstant) {
        this.defaultProfileTypeConstant = defaultProfileTypeConstant;
    }

    public String getOtherPhoneNumber() {
        return otherPhoneNumber;
    }

    public void setOtherPhoneNumber(String otherPhoneNumber) {
        this.otherPhoneNumber = otherPhoneNumber;
    }

    public List<PortalAccountDescriptionPojo> getPortalAccountDescriptionDtoList() {
        return portalAccountDescriptionDtoList;
    }

    public void setPortalAccountDescriptionDtoList(List<PortalAccountDescriptionPojo> portalAccountDescriptionDtoList) {
        this.portalAccountDescriptionDtoList = portalAccountDescriptionDtoList;
    }

    @Override
    public String toString() {
        return "PortalUserModel{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", otherName='" + otherName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", email='" + email + '\'' +
                ", portalAccountId='" + portalAccountId + '\'' +
                ", enabled=" + enabled +
                ", accountNonExpired=" + accountNonExpired +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", roles=" + roles +
                ", defaultProfileTypeConstant=" + defaultProfileTypeConstant +
                ", otherPhoneNumber='" + otherPhoneNumber + '\'' +
                ", portalAccountDescriptionPojoList=" + portalAccountDescriptionDtoList +
                '}';
    }
}
