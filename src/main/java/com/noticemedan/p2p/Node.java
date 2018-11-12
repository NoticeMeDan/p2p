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

	private void handleConnect(Message msg, String receivedIP) {
		if (this.front == null && this.back == null) {
			this.front = new NodeInfo(receivedIP, msg.getHost());
			this.back = new NodeInfo(receivedIP, msg.getHost());
			this.sendMessage(new Message(MessageType.CONNECT, receivedIP, msg.getHost(), this.port));


		} else if (this.back == null) {
			this.back = new NodeInfo(receivedIP, msg.getHost());
		} else {
			this.sendMessage(new Message(MessageType.SWITCH, this.back.getIp(), this.back.getPort(), this.port));
		}
	}

	private void handleSwitch(Message msg, String receivedIP) {
		if(this.ip.equals(receivedIP) && this.port == msg.getPort()){
			this.front = new NodeInfo(msg.getIp(), msg.getHost());
			this.sendMessage(new Message(MessageType.CONNECT, this.front.getIp(), this.front.getPort(), this.port));
		}
	}

	private void printNodeInformation() {
		System.out.println("Me: " + this.ip + ", " + this.port);
		System.out.println("Front: " + this.front);
		System.out.println("Back: " + this.back);
		System.out.println();
	}


	private void startNodeThreads(Message msg) throws IOException {
		if(msg.getPort() != 0){
			Socket s = new Socket(msg.getIp(), msg.getPort());
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(msg);
		}
		while(true) {
			new MessageHandler(serverSocket.accept()).start();
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
				String receivedIP = s.getInetAddress().toString().substring(1);
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
				s.close();
				in.close();
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			System.out.println("something changed!");
			printNodeInformation();
		}
	}
}
