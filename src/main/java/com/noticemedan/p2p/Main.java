package com.noticemedan.p2p;

import com.noticemedan.p2p.Node.Node;
import com.noticemedan.p2p.Node.NodeInfo;

import java.net.*;

public class Main {

    private static Node node;

    public static void main(String[] args) {
        CommandType nodeType = ArgumentHandler.handle(args);
        int port = Integer.parseInt(args[0]);
        String localIp = getHostIp();
        node = new Node(localIp, port);



        switch (nodeType){
            case CREATE_NETWORK:
                new Thread(node).start();
                break;
            case CREATE_NODE:
                new Thread(node).start();
                NodeInfo info = new NodeInfo(localIp, port);
                NodeInfo receiver = new NodeInfo(args[1], Integer.parseInt(args[2]));
                node.connect(info, receiver);
                break;
            default:
                printNodeGuidelines();
                break;
            }
        node.printNodeInformation();
        //Insert Scanner that calls get or put;
        /**
         *  CommandType type = ArgumentHandler.handle(args);
         *  (Should be able to handle all request, maybe you'd like to close a node and start a new one)
         *  case CREATE_NETWORK, case CREATE_NODE, case PUT, case GET
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

    private static void printNodeGuidelines(){
        System.out.println("Please use one of the following command formats:");
        System.out.println("################################################");
        System.out.println("New network: [your port]");
        System.out.println("New node:    [your port] [node ip] [node port]");
        System.out.println("################################################");
    }

    private static void printDataGuidelines(){
        System.out.println("Please use one of the following command formats:");
        System.out.println("################################################");
        System.out.println("Put request: [your port] put [key] [value] [node ip] [node port]");
        System.out.println("Get request: [your port] get [key] [node ip] [node port]");
        System.out.println("################################################");

    }
}
