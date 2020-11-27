package com.aware.realtimeStream.config;

import java.io.Serializable;

import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.airpatrol.zd.lt.commons.model.message.GetSensorStateResponse;
import com.airpatrol.zd.lt.commons.model.message.PositioningRequest;
import com.airpatrol.zd.lt.commons.model.message.SensorPositionUpdate;
import com.airpatrol.zd.lt.commons.model.message.SensorStateUpdateMessage;

@Component
public class SensorDataProcessor extends TextWebSocketHandler {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SensorDataProcessor.class);
	public static final int STATE_OFFLINE 		= 100;
	public static final int STATE_ONLINE 		= 200;

	@Autowired
	SimpMessagingTemplate messageTemplate;

	@JmsListener(destination = "SensorCmdTopic", containerFactory = "jmsListenerContainerFactory")
	public void receiveAlerts(ActiveMQObjectMessage message) throws JMSException {
		try {
			Serializable msgObject = message.getObject();
			if(msgObject instanceof SensorPositionUpdate) {
				SensorPositionUpdate sensorData = (SensorPositionUpdate)msgObject;
			//JSONObject newSensorData = new JSONObject(sensorData);
			
			//messageTemplate.convertAndSend("/topic/sensorStatusUpdates", newSensorData.toString());
			
		//	LOGGER.debug("SensorCmdTopic "+sensorData);
			}else if(msgObject instanceof GetSensorStateResponse) {
				
				GetSensorStateResponse sensorState = (GetSensorStateResponse)msgObject;
				LOGGER.debug("GetSensorStateResponse "+sensorState.toString());	
				
			}else if(msgObject instanceof SensorStateUpdateMessage) {
				SensorStateUpdateMessage sensorState = (SensorStateUpdateMessage)msgObject;
				JSONObject rawSensorData = new JSONObject(sensorState);
				JSONArray sensorDetails = (JSONArray) rawSensorData.get("stateUpdateList");
				for(int i=0;i<sensorDetails.length();i++ ) {
					
					
					
				}
				
				
				//messageTemplate.convertAndSend("/topic/sensorStatusUpdates", newSensorData.toString());
				//LOGGER.debug("SensorStateUpdateMessage "+newSensorData.toString());
				
				
			}
		}catch(Exception e) {
			LOGGER.error("Error in Reading data from UICommandQueue topic");
		}
		
		
	}
	
	@JmsListener(destination = "sensorDataQueue", containerFactory = "jmsListenerContainerFactory")
	public void getSensorData(ActiveMQObjectMessage message) throws JMSException {
		try {
			Serializable msgObject = message.getObject();
			LOGGER.debug("sensorDataQueue "+msgObject.getClass());
		}catch(Exception e) {
			LOGGER.error("Error in Reading data from UICommandQueue topic");
		}
	}
	
	
	@JmsListener(destination = "zdChangeTopic", containerFactory = "jmsListenerContainerFactory")
	public void zdChangeTopic(ActiveMQObjectMessage message) throws JMSException {
		try {
			Serializable msgObject = message.getObject();
			LOGGER.debug(" zdChangeTopic "+msgObject.getClass());
		}catch(Exception e) {
			LOGGER.error("Error in Reading data from UICommandQueue topic");
		}
	}
	
	
	@JmsListener(destination = "SMCommandQueue", containerFactory = "jmsListenerContainerFactory")
	public void SMCommandQueue(ActiveMQObjectMessage message) throws JMSException {
		try {
			Serializable msgObject = message.getObject();
			LOGGER.debug(" SMCommandQueue "+msgObject.getClass());
		}catch(Exception e) {
			LOGGER.error("Error in Reading data from UICommandQueue topic");
		}
	}
	
	@JmsListener(destination = "PositioningDataQueue", containerFactory = "jmsListenerContainerFactory")
	public void PositioningDataQueue(ActiveMQObjectMessage message) throws JMSException {
		try {
			
			Serializable msgObject = message.getObject();
			if(msgObject instanceof PositioningRequest) {
				PositioningRequest sensorData = (PositioningRequest)msgObject;
				
			LOGGER.debug(" PositioningDataQueue "+msgObject.getClass());
			}
		}catch(Exception e) {
			LOGGER.error("Error in Reading data from UICommandQueue topic");
		}
	}

}
