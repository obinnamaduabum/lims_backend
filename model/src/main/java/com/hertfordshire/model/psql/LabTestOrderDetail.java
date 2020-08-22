package com.hertfordshire.model.psql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "lab_test_order_details")
public class LabTestOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lab_test_id", referencedColumnName = "id")
    private LabTest labTest;

    @NotBlank
    private String name;

    @NotBlank
    private String uniqueId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "test_orders_id", referencedColumnName = "id")
    private OrdersModel ordersModel;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private PortalUser patient;

    private Long quantity;

    private Long total;

    private Long price;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_collected_id", referencedColumnName = "id")
    @JsonIgnore
    private SampleCollectedModel sampleCollected;

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

    public OrdersModel getOrdersModel() {
        return ordersModel;
    }

    public void setOrdersModel(OrdersModel ordersModel) {
        this.ordersModel = ordersModel;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LabTest getLabTest() {
        return labTest;
    }

    public void setLabTest(LabTest labTest) {
        this.labTest = labTest;
    }

    public SampleCollectedModel getSampleCollected() {
        return sampleCollected;
    }

    public void setSampleCollected(SampleCollectedModel sampleCollected) {
        this.sampleCollected = sampleCollected;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public PortalUser getPatient() {
        return patient;
    }

    public void setPatient(PortalUser patient) {
        this.patient = patient;
    }
}
