package com.aware.realtimeStream.config;

import java.io.Serializable;
import java.util.List;

import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.airpatrol.zd.lt.commons.model.message.GetAlertDefinitionsRequest;
import com.airpatrol.zd.lt.commons.model.message.UIAlertRequest;
import com.airpatrol.zd.lt.commons.model.message.helper.UIAlertItem;

@Component
public class AlertDataProcessor extends TextWebSocketHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessor.class);

	@Autowired
	SimpMessagingTemplate messageTemplate;


  //  @JmsListener(destination = "AlertEventTopic", containerFactory = "jmsListenerContainerFactory")
	public void receiveAlerts(ActiveMQObjectMessage message) throws JMSException {

		try {
			Serializable msgObject = message.getObject();
			String alertJsonData = null;
			if (msgObject instanceof GetAlertDefinitionsRequest) {
				GetAlertDefinitionsRequest alertDefinition = (GetAlertDefinitionsRequest) msgObject;

			} else if (msgObject instanceof UIAlertRequest) {
				UIAlertRequest alertDefinition = (UIAlertRequest) msgObject;
				List<UIAlertItem> altertUIItem = alertDefinition.getAlertItems();

				JSONObject newAlert = new JSONObject(alertDefinition);
				JSONArray jsonAlertItems = (JSONArray) newAlert.get("alertItems");
				
				JSONArray newAlerts = new JSONArray();
				for (int i = 0; i < jsonAlertItems.length(); i++) {
					JSONObject userAlertItems = (JSONObject) jsonAlertItems.get(i);

					for (int j = 0; j < altertUIItem.size(); j++) {
						UIAlertItem uiAlert = (UIAlertItem) altertUIItem.get(j);
						
						if (uiAlert.getAlertText().equals(userAlertItems.getString("alertText")) && uiAlert.getAlertTitle().equals(userAlertItems.getString("alertTitle"))) {
							
							userAlertItems.put("floorID", uiAlert.getParam(UIAlertItem.PARAM_FLOORID));
							if (uiAlert.getParam(UIAlertItem.PARAM_ZONEID) != null) {
								userAlertItems.put("zoneID", uiAlert.getParam(UIAlertItem.PARAM_ZONEID));
							} else {
								userAlertItems.put("zoneID", "NA");
							}

						}

						newAlerts.put(userAlertItems);
					}

					newAlert.remove("alertItems");
					newAlert.put("alertItems", newAlerts);

					alertJsonData = newAlert.toString();

				}
			}

			messageTemplate.convertAndSend("/topic/alertEventTopic", alertJsonData);
			LOGGER.debug("Sent message to websocket " + alertJsonData);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error in sending data to devicelocationData topic");
		}

	}
    

}
