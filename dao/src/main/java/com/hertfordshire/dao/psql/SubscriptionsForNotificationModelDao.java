package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.SubscriptionsForNotificationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionsForNotificationModelDao extends JpaRepository<SubscriptionsForNotificationModel, Long> {
}
