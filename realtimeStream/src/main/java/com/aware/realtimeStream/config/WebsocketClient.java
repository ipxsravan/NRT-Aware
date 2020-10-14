package com.aware.realtimeStream.config;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketClient {
	
	
	
	   @MessageMapping("devicelocationData")
	   @SendTo("/topic/devicelocationData")
	   public String greeting(String message) throws Exception {
	      Thread.sleep(1000); // simulated delay
	      System.out.println("WebsocketClient ---- "+message);
	      
	      return new String("Hello, " + message + "!");
	   }
	}
