package com.hertfordshire.model.psql;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity()
@Table(name = "lab_test")
public class LabTest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labTests", nullable = false)
    private LabTestCategory labTestCategory;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lab_test_template_id", referencedColumnName = "id")
    private LabTestTemplate labTestTemplate;

    private Long priceInNaira;

    private Long priceInUSD;

    private Long priceInEuro;

    private String resultTemplateId;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    private Date dateCreated = new Date();

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_updated")
    private Date dateUpdated = new Date();


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

    public LabTestCategory getLabTestCategory() {
        return labTestCategory;
    }

    public void setLabTestCategory(LabTestCategory labTestCategory) {
        this.labTestCategory = labTestCategory;
    }

    public Long getPriceInNaira() {
        return priceInNaira;
    }

    public void setPriceInNaira(Long priceInNaira) {
        this.priceInNaira = priceInNaira;
    }

    public Long getPriceInUSD() {
        return priceInUSD;
    }

    public void setPriceInUSD(Long priceInUSD) {
        this.priceInUSD = priceInUSD;
    }

    public Long getPriceInEuro() {
        return priceInEuro;
    }

    public void setPriceInEuro(Long priceInEuro) {
        this.priceInEuro = priceInEuro;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResultTemplateId() {
        return resultTemplateId;
    }

    public void setResultTemplateId(String resultTemplateId) {
        this.resultTemplateId = resultTemplateId;
    }

    public LabTestTemplate getLabTestTemplate() {
        return labTestTemplate;
    }

    public void setLabTestTemplate(LabTestTemplate labTestTemplate) {
        this.labTestTemplate = labTestTemplate;
    }

    //    public Set<OrdersModel> getOrdersModels() {
//        return ordersModels;
//    }
//
//    public void setOrdersModels(Set<OrdersModel> ordersModels) {
//        this.ordersModels = ordersModels;
//    }
}
