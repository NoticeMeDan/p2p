package com.noticemedan.p2p;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {
        CommandType type = ArgumentHandler.handle(args);
        System.out.println(type);
        Node node;
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

    /*

     */
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
