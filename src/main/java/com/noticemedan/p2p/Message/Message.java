package com.noticemedan.p2p.Message;

import com.noticemedan.p2p.Node.NodeInfo;

import java.io.Serializable;

public class Message implements Serializable{
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

	@Override
	public String toString() {
		return "Message{" +
				"node=" + node +
				", type=" + type +
				'}';
	}
}
