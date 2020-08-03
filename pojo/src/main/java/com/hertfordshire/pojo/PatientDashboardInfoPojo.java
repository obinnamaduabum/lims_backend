package com.hertfordshire.pojo;

public class PatientDashboardInfoPojo {

    private boolean doesUserHaveEmail;

    private Number numberOfTestsOrdered;

    public boolean isDoesUserHaveEmail() {
        return doesUserHaveEmail;
    }

    public void setDoesUserHaveEmail(boolean doesUserHaveEmail) {
        this.doesUserHaveEmail = doesUserHaveEmail;
    }

    public Number getNumberOfTestsOrdered() {
        return numberOfTestsOrdered;
    }

    public void setNumberOfTestsOrdered(Number numberOfTestsOrdered) {
        this.numberOfTestsOrdered = numberOfTestsOrdered;
    }
}
