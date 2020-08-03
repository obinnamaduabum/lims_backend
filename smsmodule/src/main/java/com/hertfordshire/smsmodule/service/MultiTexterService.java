package com.hertfordshire.smsmodule.service;

import com.hertfordshire.smsmodule.pojo.SendSmsPojo;
import com.hertfordshire.utils.OkHttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class MultiTexterService {


    @Value("${multitexter.sms.username}")
    private String username;

    @Value("${multitexter.sms.password}")
    private String password;


    @Autowired
    private OkHttpUtil okHttpUtil;

    public void sendSms(SendSmsPojo sendSmsPojo) {

//        String url = "http://login.betasms.com.ng/api/?username="+this.username +"&password="+ password+
//                "&message="+sendSmsPojo.getMessageBody()+"&sender=welcome&mobiles="+ sendSmsPojo.getToNumber();

        String url = null;
        try {
            url = "https://www.MultiTexter.com/tools/geturl/Sms.php?username="+username
                    +"&password="+password+"&sender="+sendSmsPojo.getFromNumber()+"" +
                    "&message="+ URLEncoder.encode(sendSmsPojo.getMessageBody(), "UTF-8")+"&flash=0&sendtime=2009-10-18%2006:30&listname=friends&recipients="+sendSmsPojo.getToNumber();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        System.out.println(url);
        try {
            String response = okHttpUtil.get(url);
            System.out.println(response);
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