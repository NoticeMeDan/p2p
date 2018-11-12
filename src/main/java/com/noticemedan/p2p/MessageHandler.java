package com.noticemedan.p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageHandler extends Thread {
    Socket socket;
    Node node;

    public MessageHandler(Node node, Socket socket) {
        this.socket = socket;
        this.node = node;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Message msg = (Message) in.readObject();
            String receivedIP = socket.getInetAddress().toString().substring(1);
            switch (msg.getType()) {
                case CONNECT:
                    handleConnect(msg, receivedIP);
                    break;
                case SWITCH:
                    handleSwitch(msg, receivedIP);
                    break;
                default:
                    System.out.println("Unknown MessageType");

            }
            socket.close();
            in.close();
        } catch (IOException | ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        System.out.println("something changed!");
        node.printNodeInformation();
    }

    private void handleSwitch(Message msg, String receivedIP) {
        if(node.getIp().equals(receivedIP) && node.getPort() == msg.getPort()){
            node.setFront(new NodeInfo(msg.getIp(), msg.getHost()));
            node.sendMessage(
                    new Message(MessageType.CONNECT,
                    node.getFront().getIp(),
                    node.getFront().getPort(),
                    node.getPort())
            );
        }
    }

    private void handleConnect(Message msg, String receivedIP) {
        if (node.getFront() == null && node.getBack() == null) {
            node.setFront(new NodeInfo(receivedIP, msg.getHost()));
            node.setBack(new NodeInfo(receivedIP, msg.getHost()));
            node.sendMessage(new Message(MessageType.CONNECT, receivedIP, msg.getHost(), node.getPort()));

        } else if (node.getBack() == null) {
            node.setBack(new NodeInfo(receivedIP, msg.getHost()));
        } else {
            node.sendMessage(new Message(MessageType.SWITCH, node.getBack().getIp(), node.getBack().getPort(), node.getPort()));
        }
    }
}