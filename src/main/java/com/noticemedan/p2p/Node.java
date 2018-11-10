package com.noticemedan.p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Node {
	private String ip;
	private Integer port;
	private ServerSocket server;
	private Socket client;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	private NodeInfo front;
	private NodeInfo back;

	public Node(Integer port, String ip) {
		this.port = port;
		this.ip = ip;
	}

	public void readMessage() {
		try {
			this.whoAmI();
			this.server = new ServerSocket(this.port);
			this.client = this.server.accept();
			this.in = new ObjectInputStream(this.client.getInputStream());
			System.out.println("Message received");
			Message msg = (Message) this.in.readObject();
			this.parseMessage(msg, this.client.getInetAddress().getHostAddress(), this.client.getPort());
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				this.server.close();
				this.client.close();
				this.in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMessage(String ip, int targetPort, Message msg) {
		try {
			this.client = new Socket(ip, targetPort);
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

	private void parseMessage(Message msg, String ip, Integer port) {
		switch (msg.getKind()) {
			case CONNECT: this.handleConnect(msg, ip, port); break;
			case SWITCH: this.handleSwitch(msg, ip, port); break;
			default: System.out.println("Unknown message kind");
		}
	}

	private void handleConnect(Message msg, String ip, Integer port) {
		if (this.front == null && this.back == null) {
			this.front = new NodeInfo(ip, port);
			this.back = new NodeInfo(ip, port);
			this.sendMessage(this.front.getIp(), this.front.getPort(), new Message(MessageKind.CONNECT));
			this.readMessage();
		} else if (this.back == null) {
			this.back = new NodeInfo(ip, port);
		} else {
			String arg = String.format("%s|%s|%s|%s", this.ip, this.port, ip, port);
			this.sendMessage(this.back.getIp(), this.back.getPort(), new Message(MessageKind.SWITCH, arg));
		}
	}

	private void handleSwitch(Message msg, String ip, Integer port) {
		String[] nodes = msg.getMessage().split("|");
		if (this.ip.equals(nodes[0]) && this.port == Integer.parseInt(nodes[1])) {
			this.front = new NodeInfo(nodes[2], Integer.parseInt(nodes[3]));
			this.sendMessage(this.front.getIp(), this.front.getPort(), new Message(MessageKind.CONNECT));
		} else {
			this.sendMessage(this.back.getIp(), this.back.getPort(), msg);
		}
	}

	private void whoAmI() {
		System.out.println("Me: " + this.ip + ", " + this.port);
		System.out.println("Front: " + this.front);
		System.out.println("Back: " + this.back);
		System.out.println();
	}

	public static void main(String[] args) throws UnknownHostException {
		if (args.length == 1 || args.length == 3) {
			Node node = new Node(Integer.parseInt(args[0]), InetAddress.getLocalHost().getHostAddress());
			if (args.length == 1) {
				node.readMessage();
			} else {
				node.sendMessage(args[1], Integer.parseInt(args[2]), new Message(MessageKind.CONNECT));
				node.readMessage();
			}
		} else {
			System.out.println("Usage: port [targetIp] [targetPort]");
		}
	}
}
