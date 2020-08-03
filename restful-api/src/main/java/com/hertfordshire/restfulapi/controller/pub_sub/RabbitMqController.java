package com.hertfordshire.restfulapi.controller.pub_sub;

import com.hertfordshire.utils.controllers.PublicBaseApiController;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitMqController extends PublicBaseApiController {

//    @Autowired
//    Publisher publisher;
//
//    @Autowired
//    private ProducerService producerService;
//
//    @Autowired
//    private ConsumerService consumerService;
//
//    @RequestMapping("/default/ada/send")
//    public String sendMsg(@RequestParam("msg") String msg){
//        // publisher.produceMsg(msg);
//        this.producerService.produce("lab.test.order.*", msg);
//        return "Done";
//    }
//
//    @RequestMapping("/default/ada/receive")
//    public String receiveMsg(){
//        // publisher.produceMsg(msg);
//        this.consumerService.receive();
//        return "Done";
//    }
}
