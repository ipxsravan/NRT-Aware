package com.aware.realtimeStream.config;

import java.util.Arrays;

import org.apache.activemq.ActiveMQConnectionFactory;
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
	
	  
	  private String brokerUrl="tcp://localhost:61616";

	  @Bean
	  public ActiveMQConnectionFactory receiverActiveMQConnectionFactory() {
	    ActiveMQConnectionFactory activeMQConnectionFactory =
	        new ActiveMQConnectionFactory();
	    activeMQConnectionFactory.setBrokerURL(brokerUrl);
	    //activeMQConnectionFactory.setTrustedPackages(Arrays.asList("com.airpatrol.zd.lt.commons.model.message"));
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


