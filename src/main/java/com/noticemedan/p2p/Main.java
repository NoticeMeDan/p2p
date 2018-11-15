package com.noticemedan.p2p;

import com.noticemedan.p2p.Exceptions.InvalidPortException;
import com.noticemedan.p2p.Node.Node;
import com.noticemedan.p2p.Node.NodeInfo;

import java.net.*;
import java.util.Scanner;

import static com.noticemedan.p2p.CommandType.*;

public class Main {

    private static Node node;

    public static void main(String[] args) {
        if(args.length == 0){
            printNodeGuidelines();
            return;
        }

        try {
            node = new Node(getHostIp(), parsePort(args[0]));
            new Thread(node).start();
            node.printNodeInformation();
            CommandType command = ArgumentHandler.handle(args);
            if (command.equals(CREATE_NODE)) {
                NodeInfo info = node.getInfo();
                NodeInfo receiver = new NodeInfo(args[1], parsePort(args[2]));
                node.connect(info, receiver);
            }
            else if (!command.equals(CREATE_NETWORK))
                printNodeGuidelines();

            Scanner in = new Scanner(System.in);
            while (in.hasNextLine()) {
                String[] dataArgs = in.nextLine().split("");
                command = ArgumentHandler.handle(dataArgs);
                if (command.equals(PUT)) {
                    NodeInfo putter = new NodeInfo(dataArgs[4], Integer.parseInt(args[5]));
                    node.put(Integer.parseInt(args[1]), args[2], putter);
                }
                else if (command.equals(GET)) {
                    NodeInfo client = node.getInfo();
                    NodeInfo getter = new NodeInfo(args[2], Integer.parseInt(args[3]));
                    node.get(parsePort(args[1]), getter, client);
                }
            }
        }catch(InvalidPortException e){
            System.out.println(e.getMessage());
        }
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
        System.out.println("Please provide a valid argument:");
        System.out.println("################################################");
        System.out.println("New network: [your port]");
        System.out.println("New node:    [your port] [node ip] [node port]");
        System.out.println("################################################");
    }

    private static void printDataGuidelines(){
        System.out.println("Please provide a valid argument:");
        System.out.println("################################################");
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
}
