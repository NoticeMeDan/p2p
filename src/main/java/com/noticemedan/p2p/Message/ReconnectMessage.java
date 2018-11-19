package com.noticemedan.p2p.Message;
import com.noticemedan.p2p.Node.NodeInfo;

public class ReconnectMessage extends Message {
    private NodeInfo oldFront;

   public ReconnectMessage(NodeInfo node, NodeInfo oldFront){
       super(MessageType.RECONNECT, node);
       this.oldFront = oldFront;
   }

    public NodeInfo getOldFront() {
        return oldFront;
    }
}
