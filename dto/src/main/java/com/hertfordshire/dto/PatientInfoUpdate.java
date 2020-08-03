package com.hertfordshire.dto;

public class PatientInfoUpdate {

    private Long employeeId;

    private String[] roles;

    private boolean accountBlockedByAdmin;

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public boolean isAccountBlockedByAdmin() {
        return accountBlockedByAdmin;
    }

    public void setAccountBlockedByAdmin(boolean accountBlockedByAdmin) {
        this.accountBlockedByAdmin = accountBlockedByAdmin;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}
