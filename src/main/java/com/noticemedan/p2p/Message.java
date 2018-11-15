package com.noticemedan.p2p;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 5L;


	private NodeInfo node;
	private boolean finished;
	private MessageType type;
	private int port;

	public Message(MessageType type, NodeInfo node, int port) {
		this.type = type;
		this.node = node;
		this.port = port;
	}

	public Message(MessageType type, NodeInfo node, boolean finished) {
		this.type = type;
		this.node = node;
		this.finished = finished;
	}

	public Message(MessageType type, NodeInfo node) {
		this.type = type;
		this.node = node;
	}

	public Message(MessageType type) {
		this.type = type;
	}

	public MessageType getType() {
		return type;
	}

	public NodeInfo getNode() {
		return node;
	}

	public boolean isFinished(){ return finished; }

	public int getPort() {
		return port;
	}

}
