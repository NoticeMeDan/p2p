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


	private void sendMessage(Message msg) {
		try {
			this.client = new Socket(ip, msg.getPort());
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

	private void handleConnect(Message msg, String receivedIP, Integer receivedPort) {
		if (this.front == null && this.back == null) {
			this.front = new NodeInfo(receivedIP, receivedPort);
			this.back = new NodeInfo(receivedIP, receivedPort);
			this.sendMessage(new Message(MessageType.CONNECT, receivedPort, receivedIP));


		} else if (this.back == null) {
			this.back = new NodeInfo(receivedIP, receivedPort);
		} else {
			//String arg = String.format("%s|%s|%s|%s", this.ip, this.port, receivedIP, receivedPort);
			this.sendMessage(new Message(MessageType.SWITCH, this.back.getPort(), this.back.getIp()));
		}
	}

	private void handleSwitch(Message msg, String ip, Integer port) {
		String[] nodes = msg.getMessage().split("|");
		if (this.ip.equals(nodes[0]) && this.port == Integer.parseInt(nodes[1])) {
			this.front = new NodeInfo(nodes[2], Integer.parseInt(nodes[3]));
			this.sendMessage(new Message(MessageType.CONNECT, this.front.getPort(), this.front.getIp()));
		} else {
			//this.sendMessage(this.back.getIp(), this.back.getPort(), msg);
		}
	}

	private void printNodeInformation() {
		System.out.println("Me: " + this.ip + ", " + this.port);
		System.out.println("Front: " + this.front);
		System.out.println("Back: " + this.back);
		System.out.println();
	}


	private void startNodeThreads() throws IOException {
		while(true) {
			new MessageHandler(serverSocket.accept()).start();
		}
    }

	public static void main(String[] args) throws IOException {
		if (args.length == 1 || args.length == 3) {
			Node node = new Node(Integer.parseInt(args[0]), InetAddress.getLocalHost().getHostAddress());
			node.printNodeInformation();
			node.startNodeThreads();
			/**
			if (args.length == 1) {
				node.readMessage();
			} else {
				node.sendMessage(args[1], Integer.parseInt(args[2]), new Message(MessageType.CONNECT));
				node.readMessage();
			}
		} else {
			System.out.println("Usage: port [targetIp] [targetPort]");
		}
			 */
		}
	}

	class MessageHandler extends Thread {
		Socket s;

		public MessageHandler(Socket s) {
			this.s = s;
		}

		@Override
		public void run() {
			try {
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				ObjectInputStream in = new ObjectInputStream(s.getInputStream());
				Message msg = (Message) in.readObject();
				System.out.println(msg.getMessage());
				switch (msg.getType()) {
					case CONNECT:
						handleConnect(msg, ip, port);
						break;
					case SWITCH:
						handleSwitch(msg, ip, port);
						break;
					default:
						System.out.println("Unknown MessageType");

				}
				s.close();
				in.close();
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}


		}
	}
}
