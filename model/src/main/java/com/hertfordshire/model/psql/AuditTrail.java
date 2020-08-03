package com.hertfordshire.model.psql;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditTrail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private transient Long id;

    @Column()
    @CreatedDate
    private LocalDateTime dateCreated;

    @Column()
    @LastModifiedDate
    private LocalDateTime dateModified;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    private PortalUser createdBy;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    private PortalUser modifiedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified = dateModified;
    }

    public PortalUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(PortalUser createdBy) {
        this.createdBy = createdBy;
    }

    public PortalUser getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(PortalUser modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditTrail that = (AuditTrail) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}

