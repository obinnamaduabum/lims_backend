package com.hertfordshire.model.psql;

import com.hertfordshire.utils.models.AuditModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class Settings extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotBlank
    private boolean isPaymentLive;

    public boolean isPaymentLive() {
        return isPaymentLive;
    }

    public void setPaymentLive(boolean paymentLive) {
        isPaymentLive = paymentLive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
