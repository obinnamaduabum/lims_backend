package com.hertfordshire.pojo.report;

public class LabTestDetailsReportPojo {


    private Long id;

    private String labTestId;

    private String name;

    private String labTestOrderId;

    private String quantity;

    private String total;

    private String price;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabTestId() {
        return labTestId;
    }

    public void setLabTestId(String labTestId) {
        this.labTestId = labTestId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabTestOrderId() {
        return labTestOrderId;
    }

    public void setLabTestOrderId(String labTestOrderId) {
        this.labTestOrderId = labTestOrderId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
