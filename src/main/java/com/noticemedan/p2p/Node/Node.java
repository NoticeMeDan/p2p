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
	private Hashtable<Integer, String> backup;

	public NodeInfo getInfo() {
		return self;
	}

	private NodeInfo self;
	private NodeInfo front;
	private NodeInfo back;


	public Node(String ip, Integer port) {
		this.self = new NodeInfo(ip, port);
		this.data = new Hashtable<>();
		this.backup = new Hashtable<>();
		this.startServerSocket(port);
	}

	private void startServerSocket(int port) {
		try {
			this.serverSocket = new ServerSocket(port);
		}
		catch(IOException e){
			e.getMessage();
		}
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
		this.setFront(msg.getNode());
		this.sendBackup(this.data);
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

	public void setFront(NodeInfo front) {
		this.front = front;
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
					case PUT:
						handlePut((DataMessage) msg);
						break;
					case GET:
						break;
					case BACKUP:
						handleBackup((DataMessage) msg);
						break;
					default:
						System.out.println("Unknown MessageType");
						break;
				}
				s.close();
				in.close();
			} catch (IOException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			printNodeInformation();
		}
	}



	private void sendSuccess(NodeInfo receiver, boolean result) {
		try {
			Socket sender = new Socket(receiver.getIp(), receiver.getPort());
			ObjectOutputStream out = new ObjectOutputStream(sender.getOutputStream());
			out.writeBoolean(result);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleBackup(DataMessage msg) {
		if(msg.hasFullBackup()){
			this.backup = msg.getBackupData();
		}
		else {
			this.backup.put(msg.getKey(), msg.getValue());
		}
		System.out.println(this.backup.size());
	}

	private void handleGet(DataMessage msg) {
		System.out.println();
	}

	public void handlePut(DataMessage msg) {
		if((msg.getSize() == null || msg.getSize() < this.data.size()) && this.front != null){
			msg.setSize(this.data.size());
			this.sendMessage(msg, this.front);
		}
		else{
			this.data.put(msg.getKey(), msg.getValue());
			this.sendBackupPut(msg.getKey(), msg.getValue());
			sendSuccess(msg.getNode(), true);
		}
	}

	private void sendBackupPut(Integer key, String value){
		if(this.front != null) {
			DataMessage backupMessage = new DataMessage(MessageType.BACKUP, this.front, key, value, null);
			this.sendMessage(backupMessage, this.front);
		}
	}

	private void sendBackup(Hashtable data){
		DataMessage backupMessage = new DataMessage(MessageType.BACKUP, this.front, data);
		this.sendMessage(backupMessage, this.front);
	}
}
