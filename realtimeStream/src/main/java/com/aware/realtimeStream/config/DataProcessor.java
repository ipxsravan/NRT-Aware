package com.aware.realtimeStream.config;

import java.io.IOException;
import java.io.Serializable;

import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.airpatrol.zd.lt.commons.model.message.SendDeviceLocationData;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DataProcessor extends TextWebSocketHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessor.class);
	
	@Autowired
	SimpMessagingTemplate messageTemplate;

	

	@JmsListener(destination = "TrackedDeviceTopic", containerFactory = "jmsListenerContainerFactory")
	public void receive1(ActiveMQObjectMessage message) throws JMSException {
		
		try {
		Serializable msgObject = message.getObject();
		if(msgObject instanceof SendDeviceLocationData ) {
		SendDeviceLocationData deviceLocaion =  (SendDeviceLocationData) msgObject;
		
		 ObjectMapper rawData = new ObjectMapper(); 
		 
		 String ipJaosnData = rawData.writeValueAsString(deviceLocaion);
		
		messageTemplate.convertAndSend("/topic/devicelocationData", ipJaosnData);
		System.out.println("Sent message to websocket "+ipJaosnData);
		
		}
		}catch(Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {

		System.out.println("sending message ti websocket");

		String payload = message.getPayload();
		JSONObject jsonObject = new JSONObject(payload);
		session.sendMessage(new TextMessage("Hi " + jsonObject.get("user") + " how may we help you?"));
	}

}