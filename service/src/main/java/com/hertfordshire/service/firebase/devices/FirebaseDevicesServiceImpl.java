package com.hertfordshire.service.firebase.devices;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gson.Gson;
import com.hertfordshire.dto.firebase.FireBaseDeviceGroupDto;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.model.psql.firebase.FireBaseDeviceGroup;
import com.hertfordshire.model.psql.firebase.FireBaseDevices;
import com.hertfordshire.dao.psql.FireBaseDeviceGroupDao;
import com.hertfordshire.dao.psql.FireBaseDevicesDao;
import com.hertfordshire.utils.OkHttpUtil;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.http.util.TextUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class FirebaseDevicesServiceImpl implements FirebaseDeviceService {

    private Logger logger = LoggerFactory.getLogger(FirebaseDevicesServiceImpl.class);

    @Value("${default.domainUrlTwo}")
    private String defaultDomainUrlTwo;

    @Value("${default.domainUrlOne}")
    private String domainUrlOne;

    @Value("${default.domainUrlTwo}")
    private String domainUrlTwo;

    @Autowired
    private FireBaseDeviceGroupDao fireBaseDeviceGroupDao;

    @Autowired
    private FireBaseDevicesDao fireBaseDevicesDao;

    @Autowired
    private OkHttpUtil okHttpUtil;

    private Gson gson;

    @Value("${fcm.server.key}")
    private String fcmServerKey;

    @Value("${fcm.sender.id}")
    private String fireBaseSenderId;

    @Value("${fcm.access.token}")
    private String fireBaseAccessToken;

    @Value("${fcm.project.id}")
    private String fireBaseProjectId;

    @Autowired
    public FirebaseDevicesServiceImpl() {
        this.gson = new Gson();
    }

    @Override
    public FireBaseDeviceGroup findFireBaseDeviceGroupByPortalUser(PortalUser portalUser) {
        return this.fireBaseDeviceGroupDao.findByPortalUser(portalUser);
    }

    @Override
    public FireBaseDeviceGroup saveFireBaseDeviceGroupByPortalUser(PortalUser portalUser, String groupId) {

        FireBaseDeviceGroup fireBaseDeviceGroup = new FireBaseDeviceGroup();
        fireBaseDeviceGroup.setPortalUser(portalUser);
        fireBaseDeviceGroup.setGroupId(groupId);
        return this.fireBaseDeviceGroupDao.save(fireBaseDeviceGroup);
    }

    @Override
    public void saveForGroup(String token, PortalUser portalUser) {

    }

    @Override
    public FireBaseDevices findDeviceByRegistrationId(String token, PortalUser portalUser) {
        return fireBaseDevicesDao.findByPortalUserAndRegistrationId(portalUser, token);
    }

    @Override
    public List<FireBaseDevices> findAllDeviceByRegistrationIdAndSubscribed(PortalUser portalUser, boolean status) {
        return fireBaseDevicesDao.findByPortalUserAndSubscribed(portalUser, status);
    }

    @Override
    public List<FireBaseDevices> findFireBaseDevicesByPortalUser(PortalUser portalUser) {
        return fireBaseDevicesDao.findByPortalUser(portalUser);
    }

    @Override
    public FireBaseDevices saveDeviceRegistrationId(String token, PortalUser portalUser) {

        FireBaseDevices fireBaseDevices = new FireBaseDevices();
        fireBaseDevices.setPortalUser(portalUser);
        fireBaseDevices.setRegistrationId(token);
        return fireBaseDevicesDao.save(fireBaseDevices);
    }

    @Override
    public FireBaseDeviceGroup createDeviceGroup(PortalUser portalUser, List<String> regIds) {

        String url = "https://fcm.googleapis.com/fcm/notification";
        String headerKey = "Authorization";
        String headerValue = "key=" + this.fcmServerKey;
        String headerTwoKey = "project_id";
        String headerTwoValue = this.fireBaseSenderId;
        JSONObject jsonObject = new JSONObject();
        String responseString = "";

        jsonObject.put("operation", "create");
        jsonObject.put("notification_key_name", portalUser.getCode());
        String[] array = regIds.toArray(new String[0]);
        jsonObject.put("registration_ids", array);
        String json = jsonObject.toString();

        try {

            Response response = okHttpUtil.postWithMultipleHeader(url, json, headerKey, headerValue, headerTwoKey, headerTwoValue);

            //logger.info(response.toString());
            //logger.info("" + response.code());

            if (response.code() == 400) {
                // logger.info(response.toString());

                FireBaseDeviceGroupDto fireBaseDeviceGroupDto = this.gson.fromJson(this.retrieveNotificationKey(portalUser), FireBaseDeviceGroupDto.class);

                if (fireBaseDeviceGroupDto != null) {
                    FireBaseDeviceGroup fireBaseDeviceGroup = new FireBaseDeviceGroup();
                    fireBaseDeviceGroup.setGroupId(fireBaseDeviceGroupDto.getNotification_key());
                    fireBaseDeviceGroup.setPortalUser(portalUser);
                    // logger.info(responseString);
                    // logger.info("else added regId to group");
                    return this.fireBaseDeviceGroupDao.save(fireBaseDeviceGroup);
                }

                return null;

            } else {

                if (response.body() != null) {
                    responseString = response.body().string();

                    if (!TextUtils.isBlank(responseString)) {
                        FireBaseDeviceGroupDto fireBaseDeviceGroupDto = this.gson.fromJson(responseString, FireBaseDeviceGroupDto.class);
                        FireBaseDeviceGroup fireBaseDeviceGroup = new FireBaseDeviceGroup();
                        fireBaseDeviceGroup.setGroupId(fireBaseDeviceGroupDto.getNotification_key());
                        fireBaseDeviceGroup.setPortalUser(portalUser);
                        // logger.info(responseString);
                        // logger.info("added regId to group");
                        return this.fireBaseDeviceGroupDao.save(fireBaseDeviceGroup);
                    }
                } else {
                    logger.info("response empty");
                }


                // FireBaseDeviceGroupDto fireBaseDeviceGroupDto = this.gson.fromJson(responseString, FireBaseDeviceGroupDto.class);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean updateDeviceGroup(PortalUser portalUser, List<String> regIds, FireBaseDeviceGroup fireBaseDeviceGroup) {

        String[] array = regIds.toArray(new String[0]);

        String url = "https://fcm.googleapis.com/fcm/notification";
        String headerKey = "Authorization";
        String headerValue = "key=" + this.fcmServerKey;
        String headerTwoKey = "project_id";
        String headerTwoValue = this.fireBaseSenderId;
        JSONObject jsonObject = new JSONObject();

        String responseString = "";

        jsonObject.put("operation", "add");
        jsonObject.put("notification_key_name", portalUser.getCode());
        jsonObject.put("notification_key", fireBaseDeviceGroup.getGroupId());
        jsonObject.put("registration_ids", array);

        String json = jsonObject.toString();
        try {

            Response response = okHttpUtil.postWithMultipleHeader(url, json, headerKey, headerValue, headerTwoKey, headerTwoValue);
            // logger.info(response.header("code"));

            // logger.info(response.toString());
            if (response.body() != null) {
                responseString = response.body().string();
            }

            if (TextUtils.isBlank(responseString)) {
                FireBaseDeviceGroupDto fireBaseDeviceGroupDto = this.gson.fromJson(responseString, FireBaseDeviceGroupDto.class);
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }

    @Override
    public boolean updateFireBaseDevice(PortalUser portalUser, FireBaseDevices fireBaseDevices, boolean subscriptionStatus) {

        FireBaseDevices savedFireBaseDevices = null;
        fireBaseDevices.setSubscribed(subscriptionStatus);
        this.fireBaseDevicesDao.save(fireBaseDevices);
        return true;
    }

    @Override
    public boolean updateFireBaseGroupSubscription(FireBaseDeviceGroup fireBaseDeviceGroup, boolean subscriptionStatus) {
        fireBaseDeviceGroup.setSubscribed(subscriptionStatus);
        this.fireBaseDeviceGroupDao.save(fireBaseDeviceGroup);
        return false;
    }


    @Override
    public String retrieveNotificationKey(PortalUser portalUser) {

        String url = "https://fcm.googleapis.com/fcm/notification?notification_key_name=" + portalUser.getCode();
        String headerKey = "Authorization";
        String headerValue = "key=" + this.fcmServerKey;
        String headerTwoKey = "project_id";
        String headerTwoValue = this.fireBaseSenderId;
        String responseString = "";
        try {

            Response response = okHttpUtil.getWithJsonResponseWithMultipleHeader(url, headerKey, headerValue, headerTwoKey, headerTwoValue);
            // logger.info(response.header("code"));

            // logger.info(response.toString());
            if (response.body() != null) {
                responseString = response.body().string();
               // logger.info(responseString);
                return responseString;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void pushMessageToGroup(PortalUser portalUser, String title, String message, String userUrl, String groupId) {

        String url = "https://fcm.googleapis.com/fcm/send";
        String headerKey = "Authorization";
        String headerValue = "key=" + this.fcmServerKey;

        JSONObject messageData = new JSONObject();
        messageData.put("url", userUrl);
        messageData.put("title", title);
        messageData.put("message", message);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("to", groupId);
        jsonObject.put("notification", messageData);

        try {

            //logger.info(jsonObject.toString());
            Response response = okHttpUtil.postWithASingleHeader(url, jsonObject.toString(), headerKey, headerValue);
            if (response != null) {
                //logger.info(response.toString());
                ResponseBody body = response.body();
                assert body != null;
                String bodyString = body.string();
                //logger.info(bodyString);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void pushMessageToTopic(String topic, String title, String message, String code, String userUrl, String groupId) {

        // commented out this function =>
        String accessToken = this.getAccessToken();
        String url = "https://fcm.googleapis.com//v1/projects/" + this.fireBaseProjectId + "/messages:send";
        String headerKey = "Authorization";
        String headerValue = "Bearer " + accessToken;


        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        JSONObject fcmOptionsData = new JSONObject();
        fcmOptionsData.put("link", this.defaultDomainUrlTwo + "/main#" + userUrl);
        // fcmOptionsData.put("icon", this.domainUrlOne + "/assets/png/logo-colored.png");

        JSONObject webPushData = new JSONObject();
        webPushData.put("fcm_options", fcmOptionsData);


        JSONObject notificationData = new JSONObject();
        notificationData.put("body", message);
        notificationData.put("title", title);
        // notificationData.put("icon", this.domainUrlOne + "/assets/png/logo-colored.png");
        notificationData.put("image", this.domainUrlOne + "/assets/png/logo-colored.png");
        // notificationData.put("icon", );

        JSONObject customData = new JSONObject();
        customData.put("dateCreated", dateFormat.format(date));
        customData.put("dateUpdated", dateFormat.format(date));
        customData.put("url", userUrl);
        customData.put("code", code);
        customData.put("imageUrl", this.domainUrlOne + "/assets/png/logo-colored.png");

        JSONObject messageData = new JSONObject();
        messageData.put("topic", topic);
        messageData.put("notification", notificationData);
        messageData.put("webpush", webPushData);
        messageData.put("data", customData);


        JSONObject requestData = new JSONObject();

        requestData.put("message", messageData);

        //logger.info(requestData.toString());
        try {

            //logger.info(requestData.toString());
            Response response = okHttpUtil.postWithASingleHeader(url, requestData.toString(), headerKey, headerValue);
            if (response != null) {
                //logger.info(response.toString());
                ResponseBody body = response.body();
                assert body != null;
                String bodyString = body.string();
                //logger.info(bodyString);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getAccessToken() {


        //FirebaseInstanceId.getInstance().

        List<String> arrayList = Arrays.asList(
                "https://www.googleapis.com/auth/firebase.database",
                "https://www.googleapis.com/auth/userinfo.email",
                "https://www.googleapis.com/auth/firebase.messaging"
        );


        GoogleCredential googleCredential = null;
        try {
            String fileUrl = "/firebase/firebase.json";
            googleCredential = GoogleCredential
                    .fromStream(new ClassPathResource(fileUrl).getInputStream())
                    .createScoped(arrayList);

            googleCredential.refreshToken();
            //logger.info(googleCredential.getAccessToken());

            return googleCredential.getAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public boolean subscribeOrUnsubscribeTopic(String topic, List<String> regIds, boolean subscribe) {

        String url = "";
        // commented out this function =>
        //String accessToken = this.getAccessToken();

        if(subscribe) {
            url = "https://iid.googleapis.com/iid/v1:batchAdd";
        } else {
            url = "https://iid.googleapis.com/iid/v1:batchRemove";
        }

        String headerKey = "Authorization";
        String headerValue = "key=" + this.fcmServerKey;


        JSONObject requestData = new JSONObject();
        requestData.put("to", "/topics/" + topic);
        requestData.put("registration_tokens", regIds);

        try {

           // logger.info(requestData.toString());
            Response response = okHttpUtil.postWithASingleHeader(url, requestData.toString(), headerKey, headerValue);
            if (response != null) {
               // logger.info(response.toString());

                ResponseBody body = response.body();
                assert body != null;
                String bodyString = body.string();
                if (response.code() == 200) {
                  //  logger.info(bodyString);
                    return true;
                } else if(response.code() == 400) {
                   // logger.info(bodyString);
                    return false;
                } else {
                   // logger.info(bodyString);
                    return false;
                }

            } else {
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
