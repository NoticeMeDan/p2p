package com.noticemedan.p2p;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Node {
	private String ip;
	private Integer port;
	private ServerSocket serverSocket;
	private Socket client;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private NodeInfo front;
	private NodeInfo back;

	public Node(Integer port, String ip) throws IOException {
		this.port = port;
		this.ip = ip;
		this.serverSocket = new ServerSocket(port);
	}

	public void sendMessage(Message msg) {
		try {
			this.client = new Socket(msg.getIp(), msg.getPort());
			this.out = new ObjectOutputStream(this.client.getOutputStream());
			out.writeObject(msg);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				this.client.close();
				this.out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void printNodeInformation() {
		System.out.println("This Node: " + this.ip + ", " + this.port);
		System.out.println("Front Node: " + this.front);
		System.out.println("Back Node: " + this.back);
		System.out.println();
	}


	private void startNodeThreads(Message msg) throws IOException {
		if(msg.getPort() != 0){
			Socket s = new Socket(msg.getIp(), msg.getPort());
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(msg);
		}
		while(true) {
			new MessageHandler(this, serverSocket.accept()).start();
		}
    }

	public static void main(String[] args) throws IOException {
		if (args.length == 1) {
			Node node = new Node(Integer.parseInt(args[0]), InetAddress.getLocalHost().getHostAddress());
			node.printNodeInformation();
			node.startNodeThreads(new Message(MessageType.CONNECT));

		} else if (args.length == 3){
			Node node = new Node(Integer.parseInt(args[0]), InetAddress.getLocalHost().getHostAddress());
			node.startNodeThreads(new Message(
					MessageType.CONNECT,
					args[1],
					Integer.parseInt(args[2]),
					Integer.parseInt(args[0])));
		}
	}

	public String getIp() {
		return this.ip;
	}

	public int getPort() {
		return this.port;
	}

	public NodeInfo getFront() {
		return this.front;
	}

	public void setFront(NodeInfo nodeInfo) {
		this.front = nodeInfo;
	}

	public NodeInfo getBack() {
		return this.back;
	}

	public void setBack(NodeInfo nodeInfo) {
		this.back = nodeInfo;
	}
}
