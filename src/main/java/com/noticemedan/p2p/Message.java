package com.noticemedan.p2p;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 4L;

	private MessageType type;
	private String message;
	private int port;
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

	public int getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getIp() {
		return ip;
	}

	@Override
	public String toString() {
		return "Message{" +
				"type=" + type +
				", message='" + message + '\'' +
				'}';
	}
}
