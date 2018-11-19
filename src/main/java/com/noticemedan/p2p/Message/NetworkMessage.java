package com.noticemedan.p2p.Message;
import com.noticemedan.p2p.Message.enums.MessageType;
import com.noticemedan.p2p.Message.enums.NetworkMessageType;
import com.noticemedan.p2p.Node.NodeInfo;

import java.util.Collections;
import java.util.List;

public class NetworkMessage extends Message {
	private static final long serialVersionUID = 10L;

	private NetworkMessageType type;
	private List<NodeInfo> nodes;
	private boolean forward;

	public NetworkMessage(NetworkMessageType type, List<NodeInfo> nodes, boolean forward) {
		super(nodes.get(0), MessageType.NETWORK);
		this.type = type;
		this.nodes = nodes;
		this.forward = forward;
	}

	public NetworkMessage(NetworkMessageType type, NodeInfo node) {
		super(node, MessageType.NETWORK);
		this.type = type;
		this.nodes = Collections.singletonList(node);
	}


	public NetworkMessageType getType() {
		return type;
	}

	public List<NodeInfo> getOldFront() {
		return nodes;
	}

	public boolean isForward() {
		return forward;
	}
}
