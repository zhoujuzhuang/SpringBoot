//package com.kimleysoft.activemq;
//
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class QueueMessageConsumer {
//
//	@JmsListener(destination= "${spring.activemq.queuename}",containerFactory = "jmsListenerContainerQueue")    //用这个注解去监听 监听的队列
//    public void receiver(String msg) {
//        System.out.println("消费者成功获取到生产者的点对点消息"+msg);
//    }
//}
