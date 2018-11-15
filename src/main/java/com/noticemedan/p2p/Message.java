package com.noticemedan.p2p;

import java.io.Serializable;

public abstract class Message implements Serializable{
	private static final long serialVersionUID = 5L;


	private NodeInfo node;
	private MessageType type;

	public Message(MessageType type, NodeInfo node) {
		this.type = type;
		this.node = node;
	}

	public MessageType getType() {
		return type;
	}

	public NodeInfo getNode() {
		return node;
	}



}
