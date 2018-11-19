package com.noticemedan.p2p.Node;

import com.noticemedan.p2p.Message.*;

import java.io.*;
import java.net.*;
import java.util.Hashtable;

public class Node implements Runnable {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private ObjectOutputStream out;

	private Hashtable<Integer, String> data;
	private Hashtable<Integer, String> backup;

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
		} catch (ConnectException | NoRouteToHostException e) {
			handleNodeNotFound();
			//Find new Front ( Send message to back until you get a message from your new front )
		}

		catch (IOException e) {
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

	private void handleNodeNotFound() {
		NetworkMessage msg = new NetworkMessage(NetworkMessageType.RECONNECT, this.self, this.front);
		sendMessage(msg, this.back);
	}

	private void handleConnect(Message msg) {
		//The very first Node handled
		if(this.back == null) {
			this.back = msg.getNode();
			NetworkMessage confirm = new NetworkMessage(NetworkMessageType.CONFIRM, this.self);
			this.sendMessage(confirm, this.back);
			if(this.front == null){
				this.front = msg.getNode();
				NetworkMessage connect = new NetworkMessage(NetworkMessageType.CONNECT, this.self);
				this.sendMessage(connect, front);
			}
		}
		else{
			if(this.back.getPort().equals(msg.getNode().getPort())){
				return;
			}
			NodeInfo oldBack = this.back;
			this.setBack(msg.getNode());
			NetworkMessage confirm = new NetworkMessage(NetworkMessageType.CONFIRM, this.self);
			this.sendMessage(confirm, this.back);
			NetworkMessage switchMessage = new NetworkMessage(NetworkMessageType.SWITCH, this.back);
			this.sendMessage(switchMessage, oldBack);
		}
	}

	private void handleConfirm(Message msg){
		if(this.front == null){
			this.front = msg.getNode();
			NetworkMessage connect = new NetworkMessage(NetworkMessageType.CONNECT, this.self);
			this.sendMessage(connect, front);
		}
	}


	private void handleReconnect(NetworkMessage msg) {
		if(msg.getOldFront().equals(this.back)){
			NetworkMessage connectMessage = new NetworkMessage(NetworkMessageType.CONFIRM, this.self);
			this.sendMessage(connectMessage, msg.getNode());
		}
		else{
			this.sendMessage(msg, this.back);
		}
	}


	private void handleSwitchFront(NetworkMessage msg) {
		this.setFront(msg.getNode());
		this.sendBackup(this.data);
		NetworkMessage confirmMessage = new NetworkMessage(NetworkMessageType.CONNECT, this.self);
		this.sendMessage(confirmMessage, this.front);
	}


	public void printNodeInformation() {
		System.out.println("This: " + this.self.getIp() + ", " + this.self.getPort());
		System.out.println("Front: " + this.front);
		System.out.println("Back: " + this.back);
		System.out.println();
	}

	public NodeInfo getInfo() {
		return self;
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
		NetworkMessage msg = new NetworkMessage(NetworkMessageType.CONNECT, sender);
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
				switch (msg.getMessageType()) {
					case DATA:
						handleDataMessage(msg);
						break;
					case NETWORK:
						handleNetworkMessage(msg);
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

		public void handleNetworkMessage(Message message) {
			NetworkMessage msg = (NetworkMessage) message;
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
				case RECONNECT:
					handleReconnect(msg);
					break;
				default:
					System.out.println("Unknown NetworkMessageType");
					break;
			}
		}

		public void handleDataMessage(Message message) {
			DataMessage msg = (DataMessage) message;
			switch (msg.getType()) {
				case PUT:
					handlePut(msg);
					break;
				case GET:
					handleGet(msg);
					break;
				case BACKUP:
					handleBackup(msg);
					break;
				default:
					System.out.println("Unknown DataMessageType");
					break;
			}
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
		// TODO: Create "not found"
		System.out.println(msg.toString());
		String value = this.data.get(msg.getKey());

		if (value == null) {
			this.sendMessage(msg, this.front);
		} else {
			Message reply = new DataMessage(DataMessageType.PUT, this.getInfo(), msg.getKey(), value);
			this.sendMessage(reply, msg.getNode());
		}
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
			DataMessage backupMessage = new DataMessage(DataMessageType.BACKUP, this.front, key, value, null);
			this.sendMessage(backupMessage, this.front);
		}
	}

	private void sendBackup(Hashtable data){
		DataMessage backupMessage = new DataMessage(DataMessageType.BACKUP, this.front, data);
		this.sendMessage(backupMessage, this.front);
	}
}
