package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.SettingsForPortalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetttingsForPortalUserDao extends JpaRepository<SettingsForPortalUser, Long> {


}