package com.hertfordshire.access.config.dto;


import com.google.gson.Gson;
import com.hertfordshire.dto.PortalAccountDescriptionDto;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.model.psql.Role;
import com.hertfordshire.utils.lhenum.DefaultProfileTypeConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

import static graphql.Assert.assertNotNull;

public class UserDetailsDto extends User {

    public Long id;
    public String lastName;
    public String firstName;
    public String otherName;
    public String phoneNumber;
    public String profileImage;
    public String email;
    public String portalAccountId;
    public boolean enabled;
    public boolean accountNonExpired = true;
    public boolean credentialsNonExpired = true;
    public boolean accountNonLocked = true;
    public boolean twoFactorEnabled;
    public List<Role> roles;
    public DefaultProfileTypeConstant defaultProfileTypeConstant;
    public String otherPhoneNumber;
    public List<PortalAccountDescriptionDto> portalAccountDescriptionDtoList;

    @Autowired
    private Gson gson;


    public UserDetailsDto(String username, String password,
                          boolean enabled, boolean accountNonExpired,
                          boolean credentialsNonExpired, boolean accountNonLocked,
                          Collection<? extends GrantedAuthority> authorities) {

        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.enabled = enabled;
    }



    public void setUserDetails(PortalUser portalUser, PortalAccount portalAccount, List<PortalAccountDescriptionDto> portalAccountTypesAndRoleDtos) {
        this.id = portalUser.getId();
        this.lastName = portalUser.getLastName();
        this.firstName = portalUser.getFirstName();
        this.otherName = portalUser.getOtherName();
        this.phoneNumber = portalUser.getPhoneNumber();
        this.accountNonLocked = portalUser.isLockedOut();
        this.enabled = portalUser.isEmailVerified();
        this.email = portalUser.getEmail();
        this.otherPhoneNumber = portalUser.getOtherPhoneNumber();
        this.portalAccountId = portalAccount.getCode();
        this.portalAccountDescriptionDtoList = portalAccountTypesAndRoleDtos;

        System.out.println("two-fa: " + portalUser.isTwoFactor());
        this.twoFactorEnabled = portalUser.isTwoFactor();
        assertNotNull(portalAccount);
    }

    public Long getId() {
        return this.id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPortalAccountId() {
        return portalAccountId;
    }

    public void setPortalAccountId(String portalAccountId) {
        this.portalAccountId = portalAccountId;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
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

    public List<PortalAccountDescriptionDto> getPortalAccountDescriptionDtoList() {
        return portalAccountDescriptionDtoList;
    }

    public void setPortalAccountDescriptionDtoList(List<PortalAccountDescriptionDto> portalAccountDescriptionDtoList) {
        this.portalAccountDescriptionDtoList = portalAccountDescriptionDtoList;
    }
}
