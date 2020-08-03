package com.hertfordshire.pojo;

import java.util.List;

public class LabTestCategoryPojo {


    private Long id;

   private String name;

   private List<LabTestPojo> labTestPojos;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LabTestPojo> getLabTestPojos() {
        return labTestPojos;
    }

    public void setLabTestPojos(List<LabTestPojo> labTestPojos) {
        this.labTestPojos = labTestPojos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
