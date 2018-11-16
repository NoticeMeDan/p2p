package com.noticemedan.p2p;

import com.noticemedan.p2p.Client.*;
import com.noticemedan.p2p.Exceptions.InvalidDataKeyException;
import com.noticemedan.p2p.Exceptions.InvalidPortException;
import com.noticemedan.p2p.Node.Node;
import com.noticemedan.p2p.Node.NodeInfo;

import java.net.*;

public class Main {

    private static Node node;

    public static void main(String[] args) {
        if(args.length == 0){
            printArgumentGuidelines();
            return;
        }
        try {
            CommandType commandType = ArgumentHandler.handle(args);
            switch (commandType) {
                case CREATE_NETWORK:
                    createNodeThread(args[0]);
                    break;
                case CREATE_NODE:
                    createNodeThread(args[0]);
                    NodeInfo info = node.getInfo();
                    NodeInfo receiver = new NodeInfo(args[1], parsePort(args[2]));
                    node.connect(info, receiver);
                    break;
                case PUT:
                    Client putter = new Putter(args[4], parsePort(args[5]));
                    //Make put async? wait for response and get boolean from put if inserted?
                    //boolean success = putter.put(parseKey(args[2]), args[3]);
                    break;
                case GET:
                    NodeInfo getterNode = new NodeInfo(args[2], parseKey(args[3]));
                    Client client = new Getter(getHostIp(), parsePort(args[0]));
                    //Make get async? then wait until an answer in the client that runs? Make Getter threaded?
                    //String value = getter.get(parsePort(args[1]), getter);
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
