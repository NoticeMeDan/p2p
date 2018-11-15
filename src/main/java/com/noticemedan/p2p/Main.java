package com.noticemedan.p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;

public class Main {

    private static Node node;

    public static void main(String[] args) {
        CommandType type = ArgumentHandler.handle(args);
        System.out.println(type);
        try {
            int port = Integer.parseInt(args[0]);
            String ip = getHostIp();
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
                break;
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //Insert Scanner that calls get or put;
        /**
         *  case PUT:
         *                 NodeInfo putter = new NodeInfo(args[4], Integer.parseInt(args[5]));
         *                 sendPut(Integer.parseInt(args[1]), args[2], putter);
         *             case GET:
         *                 NodeInfo client = new NodeInfo(ip, port);
         *                 NodeInfo getter = new NodeInfo(args[2], Integer.parseInt(args[3]));
         *                 sendGet(Integer.parseInt(args[1]), getter, client);
         *                 break;
         */

    }

    private static String getHostIp() {
        String os = System.getProperty("os.name");
        if(os.equals("Mac OS X")){
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        else {
            try (final DatagramSocket socket = new DatagramSocket()) {
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                return socket.getLocalAddress().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
