package com.noticemedan.p2p.Node;

import com.noticemedan.p2p.Message.DataMessage;
import com.noticemedan.p2p.Message.Message;
import com.noticemedan.p2p.Message.MessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class Node implements Runnable {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private ObjectOutputStream out;

	private Hashtable<Integer, String> data;
	//private Hashtable<Integer, String> backup;
	private NodeInfo self;
	private NodeInfo front;
	private NodeInfo back;


	public Node(String ip, Integer port) throws IOException {
		this.self = new NodeInfo(ip, port);
		this.data = new Hashtable<>();
		this.serverSocket = new ServerSocket(port);
	}

	private void sendMessage(Message msg, NodeInfo receiver){
		try {
			this.clientSocket = new Socket(receiver.getIp(), receiver.getPort());
			this.out = new ObjectOutputStream(this.clientSocket.getOutputStream());
			out.writeObject(msg);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				this.clientSocket.close();
				this.out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleConnect(Message msg) {
		//The very first Node handled
		if(this.back == null) {
			this.back = msg.getNode();
			Message confirm = new Message(MessageType.CONFIRM, this.self);
			this.sendMessage(confirm, this.back);
			if(this.front == null){
				this.front = msg.getNode();
				Message connect = new Message(MessageType.CONNECT, this.self);
				this.sendMessage(connect, front);
			}
		}
		else{
			if(this.back.getPort().equals(msg.getNode().getPort())){
				return;
			}
			NodeInfo oldBack = this.back;
			this.setBack(msg.getNode());
			Message confirm = new Message(MessageType.CONFIRM, this.self);
			this.sendMessage(confirm, this.back);
			Message switchMessage = new Message(MessageType.SWITCH, this.back);
			this.sendMessage(switchMessage, oldBack);
		}
	}


	private void handleConfirm(Message msg){
		if(this.front == null){
			this.front = msg.getNode();
			Message connect = new Message(MessageType.CONNECT, this.self);
			this.sendMessage(connect, front);
		}
	}


	private void handleSwitchFront(Message msg) {
		this.front = msg.getNode();
		Message confirmMessage = new Message(MessageType.CONNECT, this.self);
		this.sendMessage(confirmMessage, this.front);
	}

	
	public void printNodeInformation() {
		System.out.println("This: " + this.self.getIp() + ", " + this.self.getPort());
		System.out.println("Front: " + this.front);
		System.out.println("Back: " + this.back);
		System.out.println();
	}

	private void setBack(NodeInfo back) {
		this.back = back;
	}


	@Override
	public void run() {
		while(true) {
			try {
				Socket s = serverSocket.accept();
				new MessageHandler(s).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void connect(NodeInfo sender, NodeInfo receiver) {
		Message msg = new Message(MessageType.CONNECT, sender);
		this.sendMessage(msg, receiver);
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
						handleSwitchFront(msg);
						break;
					case CONFIRM:
						handleConfirm(msg);
						break;
					default:
						System.out.println("Unknown MessageType");
						break;
				}
				DataOutputStream response = new DataOutputStream(s.getOutputStream());
				response.writeBoolean(true);
				s.close();
				in.close();
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void handleGet(DataMessage msg) {
		System.out.println();
	}

	private void handlePut(DataMessage msg) {
		this.data.put(msg.getKey(), msg.getValue());
		System.out.println("Successfully added to network");
	}


}
