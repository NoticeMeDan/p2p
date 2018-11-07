package com.noticemedan.p2p;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer port;
	private MessageKind kind;
	private String message;

	public Message(Integer port, String message) {
		this.port = port;
		this.message = message;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
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
}
