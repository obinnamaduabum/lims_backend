package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.model.psql.firebase.FireBaseDevices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FireBaseDevicesDao extends JpaRepository<FireBaseDevices, Long> {
    FireBaseDevices findByPortalUserAndRegistrationId(PortalUser portalUser, String regId);

    List<FireBaseDevices> findByPortalUserAndSubscribed(PortalUser portalUser, boolean status);

    List<FireBaseDevices> findByPortalUser(PortalUser portalUser);
}
