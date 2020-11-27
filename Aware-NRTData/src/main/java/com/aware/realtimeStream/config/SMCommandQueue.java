package com.aware.realtimeStream.config;

import java.io.Serializable;

import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SMCommandQueue extends TextWebSocketHandler  {

	private static final Logger LOGGER = LoggerFactory.getLogger(SMCommandQueue.class);
	
	
	@JmsListener(destination = "SMCommandQueue", containerFactory = "jmsListenerContainerFactory")
	public void SMCommandQueue(ActiveMQObjectMessage message) throws JMSException {
		try {
			Serializable msgObject = message.getObject();
			LOGGER.debug(" SMCommandQueue "+msgObject.getClass());
		}catch(Exception e) {
			LOGGER.error("Error in Reading data from UICommandQueue topic");
		}
		
		
	}
	
}
