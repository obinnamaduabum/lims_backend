package com.hertfordshire.pojo;


public class LabTestResponsePojo {

    private Long id;

    private Long position;

    private String name;

    private String code;

    private String content;

    private LabTestPojo labTest;

    private boolean isSetUp;

    private boolean assigned;

    private LabTestPojo labTestName;

    private LabTestCategoryPojo labTestCategory;


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

    public LabTestPojo getLabTest() {
        return labTest;
    }

    public void setLabTest(LabTestPojo labTest) {
        this.labTest = labTest;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public boolean isSetUp() {
        return isSetUp;
    }

    public void setSetUp(boolean setUp) {
        isSetUp = setUp;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public LabTestPojo getLabTestName() {
        return labTestName;
    }

    public void setLabTestName(LabTestPojo labTestName) {
        this.labTestName = labTestName;
    }

    public LabTestCategoryPojo getLabTestCategory() {
        return labTestCategory;
    }

    public void setLabTestCategory(LabTestCategoryPojo labTestCategory) {
        this.labTestCategory = labTestCategory;
    }
}
