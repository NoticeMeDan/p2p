package com.noticemedan.p2p.Client;


public class Putter {
    private String ip;
    private int port;

    public Putter(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public boolean put(Integer parseKey, String arg) {
        return true;
    }
}
