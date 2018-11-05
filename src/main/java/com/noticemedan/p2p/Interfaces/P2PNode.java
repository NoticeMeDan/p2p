package com.noticemedan.p2p.Interfaces;


public interface P2PNode {

    void addValue(int key, String value);

    //Takes a key and returns a value from an internal hashtable;
    String sendValue(int key);

    void sendBackup();

    void assignPartner();

    void healthCheck();

}
