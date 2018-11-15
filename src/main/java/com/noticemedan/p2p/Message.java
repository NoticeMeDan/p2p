package com.noticemedan.p2p;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 5L;


	private NodeInfo node;
	private boolean finished;
	private MessageType type;
	private int port;
	private int key;
	private String value;

	public Message(MessageType type, NodeInfo node) {
		this.type = type;
		this.node = node;
	}

	public Message(MessageType type, NodeInfo client, Integer key, String value){
		this.type = type;
		this.node = client;
		this.key = key;
		this.value = value;
	}

	public MessageType getType() {
		return type;
	}

	public NodeInfo getNode() {
		return node;
	}

	public Integer getKey() {
		return this.key;
	}

	public String getValue() {
		return this.value;
	}
}
