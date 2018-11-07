package com.noticemedan.p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class P2PNode {
	private Integer port;
	private ServerSocket server;
	private Socket client;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	private Integer front;
	private Integer back;

	public P2PNode(Integer port) {
		this.port = port;
	}

	public void readMessage() {
		try {
			this.server = new ServerSocket(this.port);
			this.client = this.server.accept();
			this.in = new ObjectInputStream(this.client.getInputStream());
			System.out.println("Message received");
			Message msg = (Message) this.in.readObject();
			System.out.println(msg.toString());
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

	private void sendMessage(String ip, int port, Message msg) {
		try {
			this.client = new Socket(ip, port);
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

	public static void main(String[] args) {
		if (args.length >= 1 && args.length <= 3) {
			P2PNode node = new P2PNode(Integer.parseInt(args[0]));
			if (args.length == 1) {
				node.readMessage();
			} else if (args.length == 3) {
				node.sendMessage(args[1], Integer.parseInt(args[2]), new Message(MessageKind.CONNECT, "Lol hej"));
			}
		} else {
			System.out.println("Usage: port [targetIp] [targetPort]");
		}
	}
}
