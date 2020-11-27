package com.aware.realtimeStream.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms
public class ReceiverConfig {
	
	  
	  private String brokerUrl="tcp://192.168.22.236:61616";
	  
	  private static final Logger LOGGER = LoggerFactory.getLogger(ReceiverConfig.class);

	  @Bean
	  public ActiveMQConnectionFactory receiverActiveMQConnectionFactory() {
	    ActiveMQConnectionFactory activeMQConnectionFactory =
	        new ActiveMQConnectionFactory();
	    LOGGER.error("brokerUrl  "+brokerUrl);
	    
	    activeMQConnectionFactory.setBrokerURL(brokerUrl);
	    activeMQConnectionFactory.setTrustAllPackages(true);
	    
	    return activeMQConnectionFactory;
	  }

	  @Bean
	  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
	    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
	    factory.setConnectionFactory(receiverActiveMQConnectionFactory());
	    factory.setPubSubDomain(true);

	    return factory;
	  }
	  
	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
			MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
			converter.setTargetType(MessageType.OBJECT);
			converter.setTypeIdPropertyName("_type");
			return converter;
		}
	  
	}


