package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.KafkaTopicModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KafkaTopicDao extends JpaRepository<KafkaTopicModel, Long> {

    @Query("SELECT k FROM KafkaTopicModel as k WHERE lower(k.name) = ?1")
    KafkaTopicModel findByName(String name);
}
