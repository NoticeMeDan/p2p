package com.noticemedan.p2p.Message;
import com.noticemedan.p2p.Message.enums.DataMessageType;
import com.noticemedan.p2p.Message.enums.MessageType;
import com.noticemedan.p2p.Node.NodeInfo;

import java.util.Hashtable;

public class DataMessage extends Message {
    private final DataMessageType type;
    private final String value;
    private final Integer key;
    private final Hashtable<Integer, String> backupData;
    private Integer size;

    public DataMessage(DataMessageType type, NodeInfo node, Integer key, String value, Integer size) {
        super(node, MessageType.DATA);
        this.type = type;
        this.key = key;
        this.value = value;
        this.size = size;
        this.backupData = null;
    }

    public DataMessage(DataMessageType type, NodeInfo node, Integer key, String value) {
        super(node, MessageType.DATA);
        this.type = type;
        this.key = key;
        this.value = value;
        this.size = null;
        this.backupData = null;
    }

    public DataMessage(DataMessageType type, NodeInfo node, Hashtable data) {
        super(node, MessageType.DATA);
        this.type = type;
        value = null;
        key = null;
        this.size = null;
        this.backupData = data;
    }

    public DataMessage(DataMessageType type, NodeInfo node, Integer key) {
        super(node, MessageType.DATA);
        this.type = type;
        this.key = key;
        this.value = null;
        this.size = null;
        this.backupData = null;
    }

    public String toString() {
        return "DataMessage{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", key=" + key +
                ", backupData=" + backupData +
                ", size=" + size +
                '}';
    }

    public DataMessageType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Integer getKey() {
        return key;
    }

    public Hashtable<Integer, String> getBackupData() {
        return backupData;
    }

    public Integer getSize() {
        return size;
    }

    public Boolean hasFullBackup() {
        return backupData != null;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
