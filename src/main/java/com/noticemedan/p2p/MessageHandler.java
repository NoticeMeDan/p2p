package com.noticemedan.p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
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
                    break;

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
            node.setFrontNode(new NodeInfo(msg.getIp(), msg.getHost()));
            node.sendMessage(
                    new Message(MessageType.CONNECT,
                    node.getFrontNodeInfo().getIp(),
                    node.getFrontNodeInfo().getPort(),
                    node.getPort())
            );
        }
    }

    private void handleConnect(Message msg, String receivedIP) {
        if (node.getFrontNodeInfo() == null && node.getBackNodeInfo() == null) {
            node.setFrontNode(new NodeInfo(receivedIP, msg.getHost()));
            node.setBackNode(new NodeInfo(receivedIP, msg.getHost()));
            node.sendMessage(new Message(MessageType.CONNECT, receivedIP, msg.getHost(), node.getPort()));

        } else if (node.getBackNodeInfo() == null) {
            node.setBackNode(new NodeInfo(receivedIP, msg.getHost()));
        } else {
            node.sendMessage(new Message(MessageType.SWITCH, node.getBackNodeInfo().getIp(), node.getBackNodeInfo().getPort(), node.getPort()));
        }
    }
}