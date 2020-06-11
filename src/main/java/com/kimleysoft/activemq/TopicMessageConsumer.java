//package com.kimleysoft.activemq;
//
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TopicMessageConsumer {
//
//	@JmsListener(destination= "${spring.activemq.topicname}",containerFactory = "jmsListenerContainerTopic")    
//    public void receiver(String msg) {
//        System.out.println("消费者成功获取到生产者的广播消息"+msg);
//    }
//}
