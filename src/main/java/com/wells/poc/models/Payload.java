package com.wells.poc.models;

import java.time.Instant;

public class Payload {

	private String message;

	private Instant time;

	public Payload(String message) {
		this.message = message;
		time = Instant.now();
	}
	
	public String getMessage() {
		return message;
	}

	public Instant getTime() {
		return time;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Message is :"+message+", Time is "+time;
	}
	
}
