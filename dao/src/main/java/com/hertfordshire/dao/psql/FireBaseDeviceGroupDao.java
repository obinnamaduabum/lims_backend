package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.model.psql.firebase.FireBaseDeviceGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FireBaseDeviceGroupDao extends JpaRepository<FireBaseDeviceGroup, Long> {

    FireBaseDeviceGroup findByPortalUser(PortalUser portalUser);
}
