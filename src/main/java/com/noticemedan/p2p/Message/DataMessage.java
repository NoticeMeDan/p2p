package com.noticemedan.p2p.Message;
import com.noticemedan.p2p.Node.NodeInfo;

import java.util.Hashtable;

public class DataMessage extends Message {
    private final String value;
    private final Integer key;
    private final Hashtable<Integer, String> backupData;
    private Integer size;

    public DataMessage(MessageType type, NodeInfo node, Integer key, String value, Integer size) {
        super(type, node);
        this.key = key;
        this.value = value;
        this.size = size;
        this.backupData = null;
    }

    public DataMessage(MessageType type, NodeInfo node, Hashtable data) {
        super(type, node);
        value = null;
        key = null;
        this.backupData = data;
    }

    public DataMessage(MessageType type, NodeInfo node, Integer key) {
        super(type, node);
        this.key = key;
        this.value = null;
        this.backupData = null;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "DataMessage{" +
                "value='" + value + '\'' +
                ", key=" + key +
                "} " + super.toString();
    }

    public Integer getSize() { return size; }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean hasFullBackup() {
        return backupData != null;
    }

    public Hashtable<Integer, String> getBackupData() {
        return backupData;
    }
}
