package com.noticemedan.p2p.Interfaces;


import com.noticemedan.p2p.Node;

public interface PeerNode {

    void addValue(int key, String value);

    //Takes a key and returns a value from an internal hashtable;
    String sendValue(int key);

    void sendBackup(int key, String value, double id);

    void addPartnerValue(int key, String value, double id);

    void assignPartner(Node node);

    void healthCheck();

    int getPort();
}
