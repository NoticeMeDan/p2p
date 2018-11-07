package com.noticemedan.p2p;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	private MessageKind kind;
	private String message;

	public Message(MessageKind kind, String message) {
		this.kind = kind;
		this.message = message;
	}

	public MessageKind getKind() {
		return kind;
	}

	public void setKind(MessageKind kind) {
		this.kind = kind;
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
				"kind=" + kind +
				", message='" + message + '\'' +
				'}';
	}
}
