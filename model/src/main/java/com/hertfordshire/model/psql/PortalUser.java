package com.hertfordshire.model.psql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hertfordshire.utils.lhenum.GenericStatusConstant;
import com.hertfordshire.utils.lhenum.SignUpTypeConstant;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "portal_user")
public class PortalUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotBlank
    private String code;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String otherName;

    @Email
    private String email;

    @NotBlank
    private String password;

    private String secret;

    private boolean isEmailVerified;

    private boolean twoFactor;

    @Column(length = 32, columnDefinition = "varchar(32) default 'INACTIVE'")
    @Enumerated(EnumType.STRING)
    private GenericStatusConstant userStatus = GenericStatusConstant.INACTIVE;


    @Enumerated(EnumType.STRING)
    private SignUpTypeConstant signUpType;

    private boolean isLockedOut;

    @NotBlank
    private String phoneNumber;

    private String otherPhoneNumber;

    private boolean isPhoneNumberVerified;

    private boolean isEmailOrPhoneNumberIsVerified;

    @Column(columnDefinition = "int default 0")
    private int failedLoginAttempts;

    @NotBlank
    private String defaultPortalAccountCode;

    @Column(columnDefinition = "boolean default true")
    private boolean isAccountNonLocked;

    @Column(columnDefinition = "boolean default false")
    private boolean accountBlockedByAdmin;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "portal_user_portal_account",
            joinColumns = { @JoinColumn(name = "portal_user_id") },
            inverseJoinColumns = { @JoinColumn(name = "portal_account_id") })
    private Set<PortalAccount> portalAccounts = new HashSet<>();

    private Date dob;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", referencedColumnName = "id")
    @JsonIgnore
    private PortalUser createdBy;

    private String nextOFKinFirstName;

    private String nextOFKinLastName;

    private String nextOFKinPhoneNumber;

    @Column(name = "when_login_attempt_failed_last")
    @CreationTimestamp
    private Date whenLoginAttemptFailedLast;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    private Date dateCreated = new Date();

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_updated")
    private Date dateUpdated = new Date();

    public boolean isLockedOut() {
        return isLockedOut;
    }

    public void setLockedOut(boolean lockedOut) {
        isLockedOut = lockedOut;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public GenericStatusConstant getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(GenericStatusConstant userStatus) {
        this.userStatus = userStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtherPhoneNumber() {
        return otherPhoneNumber;
    }

    public void setOtherPhoneNumber(String otherPhoneNumber) {
        this.otherPhoneNumber = otherPhoneNumber;
    }


    public Set<PortalAccount> getPortalAccounts() {
        return portalAccounts;
    }

    public void setPortalAccounts(Set<PortalAccount> portalAccounts) {
        this.portalAccounts = portalAccounts;
    }


    public boolean isPhoneNumberVerified() {
        return isPhoneNumberVerified;
    }

    public void setPhoneNumberVerified(boolean phoneNumberVerified) {
        isPhoneNumberVerified = phoneNumberVerified;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDefaultPortalAccountCode() {
        return defaultPortalAccountCode;
    }

    public void setDefaultPortalAccountCode(String defaultPortalAccountCode) {
        this.defaultPortalAccountCode = defaultPortalAccountCode;
    }

    public PortalUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(PortalUser createdBy) {
        this.createdBy = createdBy;
    }

    public String getNextOFKinFirstName() {
        return nextOFKinFirstName;
    }

    public void setNextOFKinFirstName(String nextOFKinFirstName) {
        this.nextOFKinFirstName = nextOFKinFirstName;
    }

    public String getNextOFKinLastName() {
        return nextOFKinLastName;
    }

    public void setNextOFKinLastName(String nextOFKinLastName) {
        this.nextOFKinLastName = nextOFKinLastName;
    }

    public String getNextOFKinPhoneNumber() {
        return nextOFKinPhoneNumber;
    }

    public void setNextOFKinPhoneNumber(String nextOFKinPhoneNumber) {
        this.nextOFKinPhoneNumber = nextOFKinPhoneNumber;
    }

    public boolean isEmailOrPhoneNumberIsVerified() {
        return isEmailOrPhoneNumberIsVerified;
    }

    public void setEmailOrPhoneNumberIsVerified(boolean emailOrPhoneNumberIsVerified) {
        isEmailOrPhoneNumberIsVerified = emailOrPhoneNumberIsVerified;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    public Date getWhenLoginAttemptFailedLast() {
        return whenLoginAttemptFailedLast;
    }

    public void setWhenLoginAttemptFailedLast(Date whenLoginAttemptFailedLast) {
        this.whenLoginAttemptFailedLast = whenLoginAttemptFailedLast;
    }

    public boolean isAccountBlockedByAdmin() {
        return accountBlockedByAdmin;
    }

    public void setAccountBlockedByAdmin(boolean accountBlockedByAdmin) {
        this.accountBlockedByAdmin = accountBlockedByAdmin;
    }

    public boolean isTwoFactor() {
        return twoFactor;
    }

    public void setTwoFactor(boolean twoFactor) {
        this.twoFactor = twoFactor;
    }

    public SignUpTypeConstant getSignUpType() {
        return signUpType;
    }

    public void setSignUpType(SignUpTypeConstant signUpType) {
        this.signUpType = signUpType;
    }


    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return "PortalUser{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", otherName='" + otherName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isEmailVerified=" + isEmailVerified +
                ", userStatus=" + userStatus +
                ", isLockedOut=" + isLockedOut +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", otherPhoneNumber='" + otherPhoneNumber + '\'' +
                ", isPhoneNumberVerified=" + isPhoneNumberVerified +
                ", defaultPortalAccountCode='" + defaultPortalAccountCode + '\'' +
                ", portalAccounts=" + portalAccounts +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                '}';
    }
}
