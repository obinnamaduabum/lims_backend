package com.hertfordshire.model.psql;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity()
public class KafkaSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portal_user_id", referencedColumnName = "id")
    private PortalUser portalUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kafka_topic_id", referencedColumnName = "id")
    private KafkaTopicModel kafkaTopicModel;

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

    public PortalUser getPortalUser() {
        return portalUser;
    }

    public void setPortalUser(PortalUser portalUser) {
        this.portalUser = portalUser;
    }

    public KafkaTopicModel getKafkaTopicModel() {
        return kafkaTopicModel;
    }

    public void setKafkaTopicModel(KafkaTopicModel kafkaTopicModel) {
        this.kafkaTopicModel = kafkaTopicModel;
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
}
