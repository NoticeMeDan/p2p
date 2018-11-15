package com.noticemedan.p2p;

public class DataMessage extends Message {


    private final String value;
    private final Integer key;

    public DataMessage(MessageType type, NodeInfo node, Integer key, String value) {
        super(type, node);
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
