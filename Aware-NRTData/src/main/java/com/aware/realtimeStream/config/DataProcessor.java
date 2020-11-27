package com.aware.realtimeStream.config;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.airpatrol.zd.lt.commons.model.message.SendDeviceLocationData;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DataProcessor extends TextWebSocketHandler  {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessor.class);
	
	@Value("${database.hostIP}")
	private String dbHost;
	
	@Value("${db.userName}")
	private String dbUserName;
	
	@Value("${db.password}")
	private String dbPassword;
	
	String query = "select b.building_id,b.building_title,s.site_id,s.site_title from floor f,building b,site s where f.floor_id=? and b.building_id=f.building_id and s.site_id=b.site_id";
	
	@Autowired
	SimpMessagingTemplate messageTemplate;
	
	final ObjectMapper rawData = new ObjectMapper();

//	@JmsListener(destination = "TrackedDeviceTopic", containerFactory = "jmsListenerContainerFactory")
	public void receive1(ActiveMQObjectMessage message) throws JMSException {

		try {
			Serializable msgObject = message.getObject();
			if (msgObject instanceof SendDeviceLocationData) {
				SendDeviceLocationData deviceLocaion = (SendDeviceLocationData) msgObject;
				LOGGER.debug(deviceLocaion.getCategory()+"");

					if (deviceLocaion.getDeviceList() != null && deviceLocaion.getDeviceList().size() > 0) {

						JSONArray newDevice = new JSONArray();
						JSONObject rawDeviceData = new JSONObject(deviceLocaion);

						JSONArray deviceList = (JSONArray) rawDeviceData.get("deviceList");

						for (int i = 0; i < deviceList.length(); i++) {
							JSONObject json = deviceList.getJSONObject(i);
							json = getBuildingAndSiteInfo((long) json.get("floorId"), json);
							newDevice.put(json);
						}
						if (newDevice.length() >= 1) {
							rawDeviceData.remove("deviceList");
							rawDeviceData.put("deviceList", newDevice);
						//	LOGGER.debug("Sending Amq data to websocket ::::: /topic/devicelocationData");
							messageTemplate.convertAndSend("/topic/devicelocationData", rawDeviceData.toString());
						}

					}

			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error in sending data to devicelocationData topic ");
		}

	}
	
	public JSONObject getBuildingAndSiteInfo(Long floorId,JSONObject json) {
		
		if(dbUserName != null && dbPassword != null) {
		String connectionUrl = "jdbc:mysql://"+dbHost+":3306/zonedefense_conf";
		try (Connection conn = DriverManager.getConnection(connectionUrl, dbUserName, dbPassword)) {
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setLong(1, floorId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.put("building_id", rs.getLong("building_id"));
				json.put("site_id", rs.getLong("site_id"));
				json.put("building_title",rs.getString("building_title"));
				json.put("site_title",rs.getString("site_title"));
			}
			json.put("customer_id", 1);
		

		} catch (Exception e) {
			LOGGER.error("Error in gettig building and site info");
			e.printStackTrace();
		}
		}
		return json;
	}
	
	
	
	
}