package com.hertfordshire.smsmodule.service;

import com.hertfordshire.smsmodule.pojo.SendSmsPojo;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TwilioSmsService {

    private static final Logger logger = LoggerFactory.getLogger(TwilioSmsService.class.getSimpleName());


    @Value("${twilio.auth.token}")
    private String appAuthToken;

    @Value("${twilio.ssid}")
    private String apiSID;


    private void sendSms(SendSmsPojo sendSmsPojo) {

        logger.info(apiSID);
       logger.info(appAuthToken);

        Twilio.init(this.apiSID, this.appAuthToken);

        Message message = Message
                .creator(new PhoneNumber(sendSmsPojo.getToNumber()), // to
                        new PhoneNumber(sendSmsPojo.getFromNumber()), // from
                        sendSmsPojo.getMessageBody())
                .create();

        System.out.println(message.getSid());
    }

    @Async("smsGlobalExecutor")
    @Transactional
    public void sendActualSms (SendSmsPojo sendSmsPojo) {
        this.sendSms(sendSmsPojo);
    }
}
