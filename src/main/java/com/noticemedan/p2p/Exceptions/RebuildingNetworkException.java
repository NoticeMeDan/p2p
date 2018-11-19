package com.noticemedan.p2p.Exceptions;

import com.noticemedan.p2p.Node.NodeInfo;

public class RebuildingNetworkException extends Throwable {

    private NodeInfo disconnectedNode;

    public RebuildingNetworkException(String message, NodeInfo disconnectedNode) {
        super(message);
        this.disconnectedNode = disconnectedNode;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public NodeInfo getDisconnectedNode() {
        return disconnectedNode;
    }
}
