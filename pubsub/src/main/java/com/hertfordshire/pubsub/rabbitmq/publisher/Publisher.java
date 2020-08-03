package com.hertfordshire.pubsub.rabbitmq.publisher;

//@Component
//public class Publisher {
//
//    @Autowired
//    private AmqpTemplate amqpTemplate;
//
//    @Value("${jsa.rabbitmq.exchange}")
//    private String exchange;
//
//    public void produceMsg(String msg){
//        amqpTemplate.convertAndSend(exchange, "",msg);
//        System.out.println("Send msg = " + msg);
//    }
//}
