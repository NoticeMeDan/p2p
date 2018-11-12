package com.noticemedan.p2p;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 2L;

	private MessageType type;
	private String message;

	public Message(MessageType type, String message) {
		this.type = type;
		this.message = message;
	}

	public Message(MessageType kind) {
		this.type = type;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType kind) {
		this.type = kind;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Message{" +
				"type=" + type +
				", message='" + message + '\'' +
				'}';
	}
}
