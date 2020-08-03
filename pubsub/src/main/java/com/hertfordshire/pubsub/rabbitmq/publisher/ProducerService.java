package com.hertfordshire.pubsub.rabbitmq.publisher;

//@Service
//public class ProducerService {
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    private static final String ORDER_EXCHANGE_NAME = "order_exchange";
//
//    private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);
//
////    private String[] routeKeys = new String[]{
////            "lab_test_order_queue"
////    };
//
//    public void produce(String routingKey, String name) {
////        for (String routingKey : routeKeys) {
//        logger.info(" sending the message with routing key {}", routingKey);
//
//        NotificationPojo notificationPojo = new NotificationPojo();
//        notificationPojo.setName(name);
//        //  Car car = new Car(routingKey);
//        rabbitTemplate.convertAndSend(ORDER_EXCHANGE_NAME, "", notificationPojo);
//        //  }
//    }
//
//    public void createQueue(String name, FanoutExchange fanoutExchange, DirectExchange directExchange, String exchangeType) {
//        Queue queue = new Queue(name);
//        if (exchangeType.equalsIgnoreCase("fanout")) {
//          //return BindingBuilder.bind(queue).to(fanoutExchange).;
//        }
//    }
//
//
//    public FanoutExchange fanOutExchange(String name) {
//        return new FanoutExchange(name);
//    }
//}
