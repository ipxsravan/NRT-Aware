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

import com.airpatrol.zd.lt.commons.model.message.GetAlertDefinitionsRequest;
import com.airpatrol.zd.lt.commons.model.message.UIAlertRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AlertDataProcessor extends TextWebSocketHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessor.class);

	@Autowired
	SimpMessagingTemplate messageTemplate;

	final ObjectMapper rawData = new ObjectMapper();


	@JmsListener(destination = "AlertEventTopic", containerFactory = "jmsListenerContainerFactory")
	public void receiveAlerts(ActiveMQObjectMessage message) throws JMSException {

		try {
			Serializable msgObject = message.getObject();
			String alertJsonData = null;
			if (msgObject instanceof GetAlertDefinitionsRequest) {
				GetAlertDefinitionsRequest alertDefinition = (GetAlertDefinitionsRequest) msgObject;

				alertJsonData = rawData.writeValueAsString(alertDefinition);

			} else if (msgObject instanceof UIAlertRequest) {
				UIAlertRequest alertDefinition = (UIAlertRequest) msgObject;
				alertJsonData = rawData.writeValueAsString(alertDefinition);

			}

			messageTemplate.convertAndSend("/topic/alertEventTopic", alertJsonData);
			LOGGER.debug("Sent message to websocket " + alertJsonData);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error in sending data to devicelocationData topic");
		}

	}

}
