package com.noticemedan.p2p;

import java.io.IOException;
import java.net.InetAddress;

public class Main {

    public static void main(String[] args) {
        CommandType type = ArgumentHandler.handle(args);
        System.out.println(type);
        Node node;
        try {
        switch (type){
            case CREATE_FIRST:
                node = new Node(Integer.parseInt(args[0]), InetAddress.getLocalHost().getHostAddress());
                node.printNodeInformation();
                node.startNodeThreads(new Message(MessageType.CONNECT));
                break;
            case CREATE_NODE:
                node = new Node(Integer.parseInt(args[0]), InetAddress.getLocalHost().getHostAddress());
                node.startNodeThreads(new Message(MessageType.CONNECT, args[1], Integer.parseInt(args[2]), Integer.parseInt(args[0])));
            case PUT:
                break;
            case GET:
                break;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
