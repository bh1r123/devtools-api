package com.wells.poc.models;

public class UpdateMessage {

	private Message doc;

	public UpdateMessage(Message doc) {
		this.doc = doc;
	}

	public void setDoc(Message doc) {
		this.doc = doc;
	}

	public Message getDoc() {
		return doc;
	}

}
