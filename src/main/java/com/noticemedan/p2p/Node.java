package com.noticemedan.p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Node {
	private Integer port;
	private ServerSocket server;
	private Socket client;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	private NodeInfo front;
	private NodeInfo back;

	public Node(Integer port) {
		this.port = port;
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
			this.sendMessage(this.front.getIp(), this.front.getPort(), new Message(MessageKind.CONNECT, null));
			this.readMessage();
		} else if (this.back == null) {
			this.back = new NodeInfo(ip, port);
		} else {
			// TODO: THis probably does not work, as the server is closed at this point
			String arg = String.format("%s|%s|%s|%s", this.server.getInetAddress().getHostAddress(), this.port, ip, port);
			this.sendMessage(this.back.getIp(), this.back.getPort(), new Message(MessageKind.SWITCH, arg));
		}
	}

	private void handleSwitch(Message msg, String ip, Integer port) {

	}

	private void whoAmI() {
		System.out.println("Me: " + this.port);
		System.out.println("Front: " + this.front);
		System.out.println("Back: " + this.back);
		System.out.println();
	}

	public static void main(String[] args) {
		if (args.length >= 1 && args.length <= 3) {
			Node node = new Node(Integer.parseInt(args[0]));
			if (args.length == 1) {
				node.readMessage();
			} else if (args.length == 3) {
				node.sendMessage(args[1], Integer.parseInt(args[2]), new Message(MessageKind.CONNECT, "Lol hej"));
				node.readMessage();
			}
		} else {
			System.out.println("Usage: port [targetIp] [targetPort]");
		}
	}
}
