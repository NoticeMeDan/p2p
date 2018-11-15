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

	private void handleConnect(Message msg, String senderIP) {
		//If we're connecting for the first time
		if (this.front == null && this.back == null) {
			this.connectBoth(msg);
			NodeInfo receiver  = new NodeInfo(senderIP, msg.getNodeInfo().getPort());
			Message confirmMsg = new Message(MessageType.CONFIRM, this.self);
			System.out.println("ME: " + this.self);
			System.out.println("Receiver: "+receiver);
			this.sendMessage(confirmMsg, receiver);
		} 
		else {
			this.back = msg.getNodeInfo();
		}
	}

	private void handleConfirm(Message msg){
		if (this.front == null && this.back == null){
			this.connectBoth(msg);
		}
		else{
			this.back = msg.getNodeInfo();
			NodeInfo switchReceiver = this.front;
			Message switchMessage = new Message(MessageType.SWITCH, this.back);
			this.sendMessage(switchMessage, switchReceiver);
		}
	}

	private void connectBoth(Message msg) {
		this.front = msg.getNodeInfo();
		this.back = msg.getNodeInfo();
	}


	private void handleSwitch(Message msg) {
		this.back = msg.getNodeInfo();
		this.sendMessage(new Message(MessageType.CONNECT, this.self), this.back);
	}

	
	void printNodeInformation() {
		System.out.println("This: " + this.self.getIp() + ", " + this.self.getPort());
		System.out.println("Front: " + this.front);
		System.out.println("Back: " + this.back);
		System.out.println();
	}


	void startNodeThreads(Message msg) throws IOException {
		if(msg.getPort() != 0){
			Socket s = new Socket(msg.getNodeInfo().getIp(), msg.getPort());
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
				String senderIP = s.getInetAddress().toString().substring(1);
				switch (msg.getType()) {
					case CONNECT:
						handleConnect(msg, senderIP);
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
