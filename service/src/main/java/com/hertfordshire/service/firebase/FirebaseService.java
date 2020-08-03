package com.hertfordshire.service.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hertfordshire.model.psql.KafkaSubscription;
import com.hertfordshire.model.psql.KafkaTopicModel;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.service.firebase.devices.FirebaseDeviceService;
import com.hertfordshire.service.psql.kafka.subscription.KafkaSubscriptionService;
import com.hertfordshire.service.psql.kafka.topic.KafkaTopicService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.utils.Utils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FirebaseService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseService.class.getSimpleName());

    @Value("${fcm.custom.sign.token.cookie.name}")
    private String fireBaseTokenCookieName;

    @Value("${fcm.custom.sign.uid.cookie.name}")
    private String fireBaseUidCookieName;


    @Value("${fire.base.device.token}")
    private String fireBaseDeviceToken;

    @Autowired
    private FirebaseDeviceService firebaseDeviceService;

    @Autowired
    private KafkaSubscriptionService kafkaSubscriptionService;

    @Autowired
    private KafkaTopicService kafkaTopicService;


//    @Autowired
//    private PortalUserService portalUserService;

    @Autowired
    private PortalUserService portalUserService;

    public void generateFireBaseCustomToken(Long id, HttpServletRequest request, HttpServletResponse response) {

        try {

            String fireBaseTokenCookieName = Utils.fetchCookie(request, this.fireBaseTokenCookieName);
            String fireBaseUidCookieName = Utils.fetchCookie(request, this.fireBaseUidCookieName);

            if (TextUtils.isBlank(fireBaseTokenCookieName) && TextUtils.isBlank(fireBaseUidCookieName)) {

                PortalUser portalUser = this.portalUserService.findPortalUserById(id);

                if (portalUser != null) {
                    String uid = portalUser.getCode();
                    Map<String, Object> additionalClaims = new HashMap<String, Object>();
                    additionalClaims.put("phoneNumber", portalUser.getPhoneNumber());

                    if (!FirebaseApp.getApps().isEmpty()) {

                        logger.info("not empty fcm app");
                    } else {
                        this.initializeDefaultApp();
                        logger.info("empty fcm app");
                    }

                    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                    String customToken = FirebaseAuth.getInstance().createCustomToken(uid, additionalClaims);
                    //logger.info("custom: " + customToken);

                    HashMap<String, String> info = new HashMap<>();
                    info.put(this.fireBaseTokenCookieName, customToken);
                    info.put(this.fireBaseUidCookieName, uid);

                    for (Map.Entry<String, String> entry : info.entrySet()) {
                        // System.out.println(entry.getKey() + " = " + entry.getValue());

                        if (!TextUtils.isBlank(customToken)) {
                            if (!TextUtils.isBlank(Utils.fetchCookie(request, entry.getKey()))) {
                                Cookie cookie = new Cookie(entry.getKey(), null);
                                cookie.setMaxAge(0);
                                cookie.setPath("/");
                                cookie.setHttpOnly(false);
                                response.addCookie(cookie);
                            }
                            Cookie cookie = new Cookie(entry.getKey(), entry.getValue());
                            cookie.setMaxAge(60 * 60 * 24 * 365);
                            cookie.setPath("/");
                            cookie.setHttpOnly(false);
                            response.addCookie(cookie);
                        }
                    }

                } else {
                    logger.info("user not found");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Send token back to client
    }

    public void clearFireBaseCookies(HttpServletResponse response) {

        List<String> cookies = new ArrayList<>();
        cookies.add(this.fireBaseTokenCookieName);
        cookies.add(this.fireBaseUidCookieName);

        for (String cookieName : cookies) {
            Cookie cookie = new Cookie(cookieName, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
    }

    private void initializeDefaultApp() {

        try {
            String fileUrl = "/firebase/firebase.json";
            String databaseUrl = "https://projectmerlin-2e62d.firebaseio.com";
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(fileUrl).getInputStream()))
                    .setDatabaseUrl(databaseUrl)
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Async("unsubscribeFromFCMExecutor")
    @Transactional
    public void unsubscribeFromFCM(Long portalUserId,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   String cookieValue) {


        try {

            if (!TextUtils.isBlank(cookieValue)) {

                List<String> regIds = new ArrayList<>();

                regIds.add(cookieValue);

                PortalUser portalUser = null;

                portalUser = this.portalUserService.findPortalUserById(portalUserId);

                List<KafkaSubscription> kafkaSubscriptions = this.kafkaSubscriptionService.findAll(portalUser);

                for (KafkaSubscription kafkaSubscription : kafkaSubscriptions) {
                    KafkaTopicModel kafkaTopicModel = this.kafkaTopicService.findById(kafkaSubscription.getKafkaTopicModel().getId());
                    this.firebaseDeviceService.subscribeOrUnsubscribeTopic(kafkaTopicModel.getName(), regIds, false);
                }


            } else {
                logger.info("cookie is empty...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
