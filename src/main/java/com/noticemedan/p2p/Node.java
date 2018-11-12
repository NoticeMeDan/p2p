package com.noticemedan.p2p;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Node {
	private String ip;
	private Integer port;
	private ServerSocket serverSocket;
	private boolean active;
	private NodeInfo frontNode;
	private NodeInfo backNode;

	public Node(Integer port, String ip) {
		this.port = port;
		this.ip = ip;
		openServerSocket();
		this.active = true;
	}

	public void sendMessage(Message msg) {
			OutputSocket outputSocket = new OutputSocket(msg.getIp(),msg.getPort());
			outputSocket.write(msg);
			outputSocket.close();
	}

	private void startNodeThreads(Message msg) {
		if(msg.getPort() != 0) {
			OutputSocket outputSocket = new OutputSocket(msg.getIp(), msg.getPort());
			outputSocket.write(msg);
		}

		while(this.active) {
			new MessageHandler(this, getClientSocket())
					.start();
		}
    }

	public static void main(String[] args) throws IOException {
		String localIp = InetAddress.getLocalHost().getHostAddress();
		int port = Integer.parseInt(args[0]);

		if (args.length == 1) {
			Node node = new Node(port, localIp);
			node.printNodeInformation();
			node.startNodeThreads(new Message(MessageType.CONNECT));

		} else if (args.length == 3) {
			Node node = new Node(port, localIp);
			node.startNodeThreads(
					new Message(
							MessageType.CONNECT,
							args[1],
							Integer.parseInt(args[2]),
							port
					)
			);
		}
	}

	private void openServerSocket() {
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			throw new RuntimeException("Unable to open port '" + this.port + "'", e);
		}
	}

	private Socket getClientSocket() {
		try {
			return this.serverSocket.accept();
		} catch (IOException e) {
			throw new RuntimeException("Error getting client-socket", e);
		}
	}

	public String getIp() {
		return this.ip;
	}

	public int getPort() {
		return this.port;
	}

	public NodeInfo getFrontNodeInfo() {
		return this.frontNode;
	}

	public void setFrontNode(NodeInfo nodeInfo) {
		this.frontNode = nodeInfo;
	}

	public NodeInfo getBackNodeInfo() {
		return this.backNode;
	}

	public void setBackNode(NodeInfo nodeInfo) {
		this.backNode = nodeInfo;
	}

	public void printNodeInformation() {
		System.out.println("This Node: " + this.ip + ", " + this.port);
		System.out.println("Front Node: " + this.frontNode.toString());
		System.out.println("Back Node: " + this.backNode.toString());
		System.out.println();
	}
}
