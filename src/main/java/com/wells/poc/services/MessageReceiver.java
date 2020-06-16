package com.wells.poc.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.wells.poc.models.Payload;

@Component
public class MessageReceiver {

//	@KafkaListener(topics = "imessage")
	public void receiveMessage(@org.springframework.messaging.handler.annotation.Payload Payload payload) {
		System.out.println(payload.toString());
	}
}
