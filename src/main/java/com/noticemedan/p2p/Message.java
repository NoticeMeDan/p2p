package com.noticemedan.p2p;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 5L;


	private NodeInfo nodeInfo;
	private MessageType type;
	private int port;


	public Message(MessageType type, NodeInfo nodeInfo, int port) {
		this.type = type;
		this.nodeInfo = nodeInfo;
		this.port = port;
	}

	public Message(MessageType type, NodeInfo backInfo) {
		this.type = type;
		this.nodeInfo = nodeInfo;
	}

	public Message(MessageType type) {
		this.type = type;
	}

	public MessageType getType() {
		return type;
	}

	public NodeInfo getNodeInfo() {
		return nodeInfo;
	}

	public int getPort() {
		return port;
	}

}
