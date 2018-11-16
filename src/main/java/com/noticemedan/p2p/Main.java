package com.noticemedan.p2p;

import com.noticemedan.p2p.Client.*;
import com.noticemedan.p2p.Exceptions.InvalidDataKeyException;
import com.noticemedan.p2p.Exceptions.InvalidPortException;
import com.noticemedan.p2p.Node.Node;
import com.noticemedan.p2p.Node.NodeInfo;

import java.net.*;
import java.util.Arrays;

public class Main {

    private static Node node;

    public static void main(String[] args) {
        try {
            CommandType commandType = ArgumentHandler.handle(args);
            switch (commandType) {
                case CREATE_NETWORK:
                    createNodeThread(args[0]);
                    node.printNodeInformation();
                    break;
                case CREATE_NODE:
                    createNodeThread(args[0]);
                    NodeInfo info = node.getInfo();
                    NodeInfo receiver = new NodeInfo(args[1], parsePort(args[2]));
                    node.connect(info, receiver);
                    break;
                case PUT:
                    Putter putter = new Putter(new NodeInfo(getHostIp(), parsePort(args[0])));
                    NodeInfo putReceiver = new NodeInfo(args[4], parsePort(args[5]));
                    //Make put async? wait for response and get boolean from put if inserted?
                    boolean success = putter.put(parseKey(args[2]), args[3], putReceiver);
                    break;
                case GET:
                    Getter getter = new Getter(new NodeInfo(getHostIp(), parsePort(args[0])));
                    NodeInfo getReceiver = new NodeInfo(args[3], parseKey(args[4]));
                    //Make get async? then wait until an answer in the client that runs? Make Getter threaded?
                    System.out.println(getter.get(parseKey(args[2]), getReceiver));
                    break;
                case UNKNOWN:
                    printArgumentGuidelines();
                    break;
                default:
                    printArgumentGuidelines();
            }
        }catch(InvalidPortException | InvalidDataKeyException e){
            e.getMessage();
        }
    }

    private static void createNodeThread(String port) throws InvalidPortException{
        node = new Node(getHostIp(), parsePort(port));
        new Thread(node).start();
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
        System.out.println("Please provide a valid argument:");
        System.out.println("################################################");
        System.out.println("New network: [your port]");
        System.out.println("New node:    [your port] [node ip] [node port]");
        System.out.println("Put request: [your port] put [key] [value] [node ip] [node port]");
        System.out.println("Get request: [your port] get [key] [node ip] [node port]");
        System.out.println("################################################");
    }


    public static Integer parsePort(String text) throws InvalidPortException {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new InvalidPortException("Please provide a valid port");
        }
    }

    public static Integer parseKey(String text) throws InvalidDataKeyException {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new InvalidDataKeyException("Please provide a valid key of type int");
        }
    }
}
