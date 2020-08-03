package com.hertfordshire.service.firebase;

import com.hertfordshire.service.firebase.devices.FirebaseDeviceService;

import com.hertfordshire.pubsub.kafka.service.producer.Producer;
import com.hertfordshire.service.firebase.devices.FirebaseDevicesServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PushNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(PushNotificationService.class.getSimpleName());

    private final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";
    private final String FIREBASE_SERVER_KEY = "YOUR_SERVER_KEY";


//    @Autowired
//    private Producer producer;


    private FirebaseDeviceService firebaseDevicesService;

    @Autowired
    public PushNotificationService() {
        firebaseDevicesService = new FirebaseDevicesServiceImpl();
    }

//    public void sendPushNotification(List<String> keys, String messageTitle, String message) {
//
//
//        JSONObject msg = new JSONObject();
//
//        msg.put("title", messageTitle);
//        msg.put("body", message);
//        msg.put("notificationType", "Test");
//
//        keys.forEach(key -> {
//            System.out.println("\nCalling fcm Server >>>>>>>");
//            String response = callToFcmServer(msg, key);
//            System.out.println("Got response from fcm Server : " + response + "\n\n");
//        });
//
//    }

//    private String callToFcmServer(JSONObject message, String receiverFcmKey) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.set("Authorization", "key=" + FIREBASE_SERVER_KEY);
//        httpHeaders.set("Content-Type", "application/json");
//
//        JSONObject json = new JSONObject();
//
//        json.put("data", message);
//        json.put("notification", message);
//        json.put("to", receiverFcmKey);
//
//        System.out.println("Sending :" + json.toString());
//
////        HttpEntity<String> httpEntity = new HttpEntity(json.toString(), httpHeaders);
//        //return restTemplate.postForObject(FIREBASE_API_URL, httpEntity, String.class);
//
//        return null;
//    }



    @Async("fcmPushNotificationExecutor")
    @Transactional
    public void sendPushNotificationToFCMAndKafka(String title, String message, String code, String url, List<String> topics) {

            //this.producer.sendMessage(title, message, code, url, topics);
            //  publish message to kafka

            //this.firebaseDevicesService.pushMessageToGroup(null, title, message, url, "APA91bGJWnPsbtMLAJ8_gMGu9ru9recYk1YqeC7-QrrgQPF6J9-CrORFVLBD6MF7fmuMP6MzMxi8CCftUy1sq-5gMIwmgurgKxNoasI03G27Fy8A9KaR6M1qo7y45twFZ_yVlB2DvtvZ");
            // logger.info("url: " + this.defaultDomainUrlTwo + url);
            this.firebaseDevicesService.pushMessageToTopic(topics.stream().findFirst().orElse(""), title, message, code, url, null);

            //this.pushNotificationService.sendPushNotificationToTopic(topics.stream().findFirst().orElse(""), title, message, url);
            //  publish message to firebase
    }

//    public void sendPushNotificationToTopic(String topic, String title, String messageInfo, String url) {
//        // See documentation on defining a message payload.
//
//        // logger.info("topic", "/topics/" +topic);
//        Message message = Message.builder()
//                .putData("message", messageInfo)
//                .putData("title", title)
//                .putData("url", url)
//                .setTopic(topic)
//                .build();
//
//        // Send a message to the devices subscribed to the provided topic.
//        String response = null;
//        try {
//            response = FirebaseMessaging.getInstance().send(message);
//            // logger.info("Successfully sent message to  topic: " + topic +", " + response);
//        } catch (FirebaseMessagingException e) {
//            e.printStackTrace();
//        }
//    }
}
