package com.noticemedan.p2p;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 4L;

	private MessageType type;
	private String message;
	private int port;

	public int getHost() {
		return host;
	}

	public void setHost(int host) {
		this.host = host;
	}

	private int host;
	private String ip;

	public Message(MessageType type, String ip, int port, int host) {
		this.type = type;
		this.port = port;
		this.ip = ip;
		this.host = host;
	}

	public Message(MessageType type) {
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

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
