package com.noticemedan.p2p;

public class NodeInfo {
    private String ip;
    private Integer port;

    public NodeInfo(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "NodeInfo{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
