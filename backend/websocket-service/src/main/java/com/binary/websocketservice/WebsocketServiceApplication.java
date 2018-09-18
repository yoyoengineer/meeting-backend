package com.binary.websocketservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@SpringBootApplication
@RestController
@Slf4j
public class WebsocketServiceApplication {

	@Autowired SimpMessagingTemplate simpMessagingTemplate;

	@RequestMapping("/me")
	public Principal me(Principal user){
		return user;
	}

	public static void main(String[] args) {
		SpringApplication.run(WebsocketServiceApplication.class, args);
	}

	@RequestMapping("/sendmymessages")
	public void send(){
		log.debug("Oreder received");
		simpMessagingTemplate.convertAndSend("/topic/mm","Hello World");
	}
}
