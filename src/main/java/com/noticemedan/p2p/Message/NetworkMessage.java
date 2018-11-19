package com.noticemedan.p2p.Message;
import com.noticemedan.p2p.Node.NodeInfo;

public class NetworkMessage extends Message {
	private static final long serialVersionUID = 1L;

	private NetworkMessageType type;
	private NodeInfo oldFront;

	public NetworkMessage(NetworkMessageType type, NodeInfo node, NodeInfo oldFront) {
		super(node, MessageType.NETWORK);
		this.type = type;
		this.oldFront = oldFront;
	}

	public NetworkMessage(NetworkMessageType type, NodeInfo node) {
		super(node, MessageType.NETWORK);
		this.type = type;
	}

	public NetworkMessageType getType() {
		return type;
	}

	public NodeInfo getOldFront() {
		return oldFront;
	}
}
