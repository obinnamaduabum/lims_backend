package com.hertfordshire.service.firebase.devices;


import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.model.psql.firebase.FireBaseDeviceGroup;
import com.hertfordshire.model.psql.firebase.FireBaseDevices;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FirebaseDeviceService {

    FireBaseDeviceGroup findFireBaseDeviceGroupByPortalUser(PortalUser portalUser);

    FireBaseDeviceGroup saveFireBaseDeviceGroupByPortalUser(PortalUser portalUser, String groupId);

    void saveForGroup(String token, PortalUser portalUser);

    FireBaseDevices findDeviceByRegistrationId(String token, PortalUser portalUser);

    List<FireBaseDevices> findAllDeviceByRegistrationIdAndSubscribed(PortalUser portalUser, boolean status);

    List<FireBaseDevices> findFireBaseDevicesByPortalUser(PortalUser portalUser);

    FireBaseDevices saveDeviceRegistrationId(String token, PortalUser portalUser);

    FireBaseDeviceGroup createDeviceGroup(PortalUser portalUser, List<String> regIds);

    boolean updateDeviceGroup(PortalUser portalUser, List<String> regIds, FireBaseDeviceGroup fireBaseDeviceGroup);


    boolean updateFireBaseDevice(PortalUser portalUser, FireBaseDevices fireBaseDevices, boolean subscriptionStatus);

    boolean updateFireBaseGroupSubscription(FireBaseDeviceGroup fireBaseDeviceGroup, boolean subscriptionStatus);

    String retrieveNotificationKey(PortalUser portalUser);

    void pushMessageToGroup(PortalUser portalUser, String title, String message, String userUrl, String groupId);

    void pushMessageToTopic(String topic, String title, String message, String code, String userUrl, String groupId);

    boolean subscribeOrUnsubscribeTopic(String topic, List<String> regIds, boolean subscribe);
}
