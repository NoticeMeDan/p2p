package com.noticemedan.p2p;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Node {
	private ServerSocket serverSocket;
	private Socket client;
	private ObjectOutputStream out;

	private NodeInfo self;
	private NodeInfo front;
	private NodeInfo back;


	public Node(Integer port, String ip) throws IOException {
		this.self = new NodeInfo(ip, port);
		this.serverSocket = new ServerSocket(port);
	}

	private void sendMessage(Message msg, NodeInfo receiver) {
		try {
			this.client = new Socket(receiver.getIp(), receiver.getPort());
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

	private void handleConnect(Message msg) {
		this.front = msg.getNode();
		this.back = msg.getNode();
		Message confirmMessage = new Message(MessageType.CONFIRM, this.self);
		this.sendMessage(confirmMessage, this.front);
	}

	private void handleConfirm(Message msg){
		this.back = msg.getNode();
		if(this.front == null){
			this.front = msg.getNode();
		}
		else{
			if(!msg.isFinished()) {
				Message switchMessage = new Message(MessageType.SWITCH, this.back);
				this.sendMessage(switchMessage, this.front);
			}
		}
	}


	private void handleSwitch(Message msg) {
		this.front = msg.getNode();
		Message confirmMessage = new Message(MessageType.CONFIRM, this.self, true);
		this.sendMessage(confirmMessage, this.front);
	}

	
	void printNodeInformation() {
		System.out.println("This: " + this.self.getIp() + ", " + this.self.getPort());
		System.out.println("Front: " + this.front);
		System.out.println("Back: " + this.back);
		System.out.println();
	}


	void startNodeThreads(Message msg) throws IOException {
		if(msg.getPort() != 0){
			Socket s = new Socket(msg.getNode().getIp(), msg.getPort());
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(msg);
		}
		while(true) {
			new MessageHandler(serverSocket.accept()).start();
		}
    }

	class MessageHandler extends Thread {
		Socket s;

		MessageHandler(Socket s) {
			this.s = s;
		}

		@Override
		public void run() {

			try {
				ObjectInputStream in = new ObjectInputStream(s.getInputStream());
				Message msg = (Message) in.readObject();
				switch (msg.getType()) {
					case CONNECT:
						handleConnect(msg);
						break;
					case SWITCH:
						handleSwitch(msg);
						break;
					case CONFIRM:
						handleConfirm(msg);
						break;
					default:
						System.out.println("Unknown MessageType");
				}
				s.close();
				in.close();
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			printNodeInformation();
		}
	}

	
}
