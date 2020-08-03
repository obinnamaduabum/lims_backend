package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.AdminSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminSettingsDao extends JpaRepository<AdminSettings, Long> {

}
