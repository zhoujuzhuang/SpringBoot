//package com.kimleysoft.template;
//
//import javax.jms.Queue;
//import javax.jms.Topic;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jms.core.JmsMessagingTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ActiveMqTemplate {
//
//	@Autowired
//	private JmsMessagingTemplate jmsMessagingTemplate;
//
//	@Autowired
//	private Queue queue;
//	@Autowired
//	private Topic topic;
//
//	public void sendQueueMessage(String msg) {
//		jmsMessagingTemplate.convertAndSend(queue, msg);
//		System.out.println("点对点消息:" + msg);
//	}
//	
//	public void sendTopicMessage(String msg) {
//		jmsMessagingTemplate.convertAndSend(topic, msg);
//		System.out.println("广播消息:" + msg);
//	}
//
//}
