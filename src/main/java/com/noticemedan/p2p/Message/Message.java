package com.noticemedan.p2p.Message;

import com.noticemedan.p2p.Message.enums.MessageType;
import com.noticemedan.p2p.Node.NodeInfo;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	private final NodeInfo node;
	private final MessageType type;

	public Message(NodeInfo node, MessageType type) {
		this.node = node;
		this.type = type;
	}

	public NodeInfo getNode() {
		return node;
	}

	public MessageType getMessageType() {
		return type;
	}
}
