//package com.kimleysoft.config;
//
//import javax.jms.ConnectionFactory;
//import javax.jms.Queue;
//import javax.jms.Topic;
//
//import org.apache.activemq.command.ActiveMQQueue;
//import org.apache.activemq.command.ActiveMQTopic;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
//import org.springframework.jms.config.JmsListenerContainerFactory;
//
//@Configuration
//public class ActiveMqConfig {
//
//	@Value("${spring.activemq.queuename}")
//	private String queuename;
//	
//	@Value("${spring.activemq.topicname}")
//	private String topicname;
//	
//	@Bean
//    public Queue queue() {
//        return new ActiveMQQueue(queuename); 
//    } 
//	
//	@Bean
//	public Topic topic() {
//		return new ActiveMQTopic(topicname);
//	} 
//	
//	// queue模式的ListenerContainer
//    @Bean
//    public JmsListenerContainerFactory<?> jmsListenerContainerQueue(ConnectionFactory activeMQConnectionFactory) {
//        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
//        bean.setConnectionFactory(activeMQConnectionFactory);
//        return bean;
//    }
//    
//	// topic模式的ListenerContainer
//    @Bean
//    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ConnectionFactory activeMQConnectionFactory) {
//        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
//        bean.setPubSubDomain(true);
//        bean.setConnectionFactory(activeMQConnectionFactory);
//        return bean;
//    }
//}
