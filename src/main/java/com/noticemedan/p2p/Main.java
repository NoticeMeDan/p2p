package com.noticemedan.p2p;

import com.noticemedan.p2p.Node.Node;
import com.noticemedan.p2p.Node.NodeInfo;

import java.io.IOException;
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
            case CREATE_FIRST_NODE:
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
            default:
                printArgumentGuidelines();
                break;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //Insert Scanner that calls get or put;
        /**
         *  CommandType type = ArgumentHandler.handle(args);
         *  (Should be able to handle all request, maybe you'd like to close a node and start a new one)
         *  case CREATE_FIRST_NODE, case CREATE_NODE, case PUT, case GET
         *
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
            } catch (UnknownHostException | SocketException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void printArgumentGuidelines(){
        System.out.println("################################################");
        System.out.println("Please use one of the following command formats:");
        System.out.println("################################################");
        System.out.println("New network: [your port]");
        System.out.println("New node:    [your port] [node ip] [node port]");
        System.out.println("Put request: [your port] put [key] [value] [node ip] [node port]");
        System.out.println("Get request: [your port] get [key] [node ip] [node port]");
        System.out.println("################################################");
        System.out.println("The program uses your local ip.");
    }
}
