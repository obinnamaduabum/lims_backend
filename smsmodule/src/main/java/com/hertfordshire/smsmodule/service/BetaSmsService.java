package com.hertfordshire.smsmodule.service;


import com.hertfordshire.smsmodule.pojo.SendSmsPojo;
import com.hertfordshire.utils.OkHttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class BetaSmsService {


    @Value("${beta.sms.username}")
    private String username;

    @Value("${beta.sms.password}")
    private String password;


    @Autowired
    private  OkHttpUtil okHttpUtil;

    public void sendSms(SendSmsPojo sendSmsPojo) {

        String url = "http://login.betasms.com.ng/api/?username="+this.username +"&password="+ password+
                "&message="+sendSmsPojo.getMessageBody()+"&sender=welcome&mobiles="+ sendSmsPojo.getToNumber();

        try {
             String response = okHttpUtil.get(url);
            // System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Async("smsNigeriaExecutor")
    @Transactional
    public void sendActualSms (SendSmsPojo sendSmsPojo) {
        this.sendSms(sendSmsPojo);
    }
}
