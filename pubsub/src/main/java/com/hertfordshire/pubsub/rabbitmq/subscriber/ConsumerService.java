package com.hertfordshire.pubsub.rabbitmq.subscriber;


//@Service
//public class ConsumerService {
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);
//
//    private static final String QUEUE_NAME = "lab_test_order_queue";
//
//    @Scheduled(fixedRate = 5000)
//    public void receive()
//    {
//
//        logger.info("i got here");
//        Object object = rabbitTemplate.receiveAndConvert(QUEUE_NAME);
//
//        if (object != null) {
//          //  NotificationPojo notificationPojo = (NotificationPojo) object;
//            logger.info(" received the message [" + object.toString() + "] ");
//        }
//    }
//}
