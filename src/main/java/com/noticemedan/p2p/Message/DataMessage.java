package com.noticemedan.p2p.Message;
import com.noticemedan.p2p.Node.NodeInfo;

public class DataMessage extends Message {


    private final String value;
    private final Integer key;
    private Integer size;

    public DataMessage(MessageType type, NodeInfo node, Integer key, String value, int size) {
        super(type, node);
        this.key = key;
        this.value = value;
        this.size = size;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Integer getSize() { return size; }

    public void setSize(int size) {
        this.size = size;
    }
}
