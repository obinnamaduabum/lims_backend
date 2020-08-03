package com.hertfordshire.smsmodule.service;

import com.hertfordshire.smsmodule.pojo.SendSmsPojo;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.sms.SmsSubmissionResult;
import com.nexmo.client.sms.messages.TextMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class NexmoSmsService {

    @Value("${nexmo.sms.key}")
    private String apiKey;

    @Value("${nexmo.sms.secret}")
    private String apiSecret;


    public NexmoSmsService() {}

    public void sendSms(SendSmsPojo sendSmsPojo) {

        System.out.println(apiKey);
        System.out.println(apiSecret);
        AuthMethod auth = new TokenAuthMethod(apiKey, apiSecret);
        NexmoClient client = new NexmoClient(auth);

        SmsSubmissionResult[] responses = new SmsSubmissionResult[0];

        try {
            responses = client.getSmsClient().submitMessage(new TextMessage(
                    sendSmsPojo.getFromNumber(),
                    sendSmsPojo.getToNumber(),
                    sendSmsPojo.getMessageBody()));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NexmoClientException e) {
            e.printStackTrace();
        }
        for (SmsSubmissionResult response : responses) {
            System.out.println(response);
        }
    }



    @Async("mailExecutor")
    @Transactional
    public void sendActualSms (SendSmsPojo sendSmsPojo) {
        this.sendSms(sendSmsPojo);
    }
}
