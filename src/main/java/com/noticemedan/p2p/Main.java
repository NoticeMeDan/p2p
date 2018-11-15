package com.noticemedan.p2p;

import java.io.IOException;
import java.net.InetAddress;

public class Main {

    public static void main(String[] args) {
        CommandType type = ArgumentHandler.handle(args);
        System.out.println(type);
        Node node;
        try {
            int port = Integer.parseInt(args[0]);
            String ip = InetAddress.getLocalHost().getHostAddress();
        switch (type){
            case CREATE_FIRST:
                node = new Node(ip, port);
                new Thread(node).start();
                node.printNodeInformation();
                break;
            case CREATE_NODE:
                node = new Node(ip, port);
                new Thread(node).start();
                node.printNodeInformation();
                NodeInfo info = new NodeInfo(ip, port);
                NodeInfo receiver = new NodeInfo(args[1], Integer.parseInt(args[2]));
                node.connect(info, receiver);
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
