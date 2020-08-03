package com.hertfordshire.pojo;

public class LabTestsPieChartPojo {


    private String patientsTestsAmount;

    private Long patientsNumberOfTests;

    private String institutionTestsAmount;

    private Long institutionNumberOfTests;


    public String getPatientsTestsAmount() {
        return patientsTestsAmount;
    }

    public void setPatientsTestsAmount(String patientsTestsAmount) {
        this.patientsTestsAmount = patientsTestsAmount;
    }

    public Long getPatientsNumberOfTests() {
        return patientsNumberOfTests;
    }

    public void setPatientsNumberOfTests(Long patientsNumberOfTests) {
        this.patientsNumberOfTests = patientsNumberOfTests;
    }

    public String getInstitutionTestsAmount() {
        return institutionTestsAmount;
    }

    public void setInstitutionTestsAmount(String institutionTestsAmount) {
        this.institutionTestsAmount = institutionTestsAmount;
    }

    public Long getInstitutionNumberOfTests() {
        return institutionNumberOfTests;
    }

    public void setInstitutionNumberOfTests(Long institutionNumberOfTests) {
        this.institutionNumberOfTests = institutionNumberOfTests;
    }
}
