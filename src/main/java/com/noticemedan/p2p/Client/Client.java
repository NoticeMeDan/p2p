package com.noticemedan.p2p.Client;

public abstract class Client {

    private String ip;
    private int port;

    public Client(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

}
