package com.aware.realtimeStream.config;

import java.io.Serializable;

import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.airpatrol.zd.lt.commons.model.message.SendDeviceLocationData;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DataProcessor extends TextWebSocketHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessor.class);

	@Autowired
	SimpMessagingTemplate messageTemplate;

	ObjectMapper rawData = new ObjectMapper();

	@JmsListener(destination = "TrackedDeviceTopic", containerFactory = "jmsListenerContainerFactory")
	public void receive1(ActiveMQObjectMessage message) throws JMSException {

		try {
			Serializable msgObject = message.getObject();
			if (msgObject instanceof SendDeviceLocationData) {
				SendDeviceLocationData deviceLocaion = (SendDeviceLocationData) msgObject;

				String ipJaosnData = rawData.writeValueAsString(deviceLocaion);

				messageTemplate.convertAndSend("/topic/devicelocationData", ipJaosnData);
				LOGGER.debug("Sent message to websocket " + ipJaosnData);

			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error in sending data to devicelocationData topic");
		}

	}

}