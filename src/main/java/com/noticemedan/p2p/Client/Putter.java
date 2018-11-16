package com.noticemedan.p2p.Client;


public class Putter extends Client {

    public Putter(String ip, Integer port) {
        super(ip, port);
    }

    public boolean put(Integer parseKey, String arg) {
        return true;
    }
}
