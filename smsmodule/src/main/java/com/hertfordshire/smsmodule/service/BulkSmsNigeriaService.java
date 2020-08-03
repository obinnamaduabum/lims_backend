package com.hertfordshire.smsmodule.service;

import org.springframework.stereotype.Service;

@Service
public class BulkSmsNigeriaService {

//    @Value("${bulk.sms.nigeria.api}")
//    private String bulkSmsNigeriaApi;
//
//    @Value("${from.name.bulk.sms.nigeria.sms}")
//    private String fromNameBulkSmsNigeria;
//
//
//    @Autowired
//    private OkHttpUtil okHttpUtil;
//
//    public void sendSms(SendSmsPojo sendSmsPojo) {
//
//        String url = "https://www.bulksmsnigeria.com/api/v1/sms/create?api_token="
//                + this.bulkSmsNigeriaApi+"&from="+ this.fromNameBulkSmsNigeria
//                +"&to=" +sendSmsPojo.getToNumber() +"&body="+sendSmsPojo.getMessageBody();
//
//        System.out.println(url);
//        try {
//            String response = okHttpUtil.get(url);
//            System.out.println(response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Async("smsNigeriaExecutor")
//    @Transactional
//    public void sendActualSms (SendSmsPojo sendSmsPojo) {
//        this.sendSms(sendSmsPojo);
//    }
}