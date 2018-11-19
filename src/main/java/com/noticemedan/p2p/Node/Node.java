package com.noticemedan.p2p.Node;

import com.noticemedan.p2p.Exceptions.RebuildingNetworkException;
import com.noticemedan.p2p.Message.*;
import com.noticemedan.p2p.Message.enums.DataMessageType;
import com.noticemedan.p2p.Message.enums.MessageType;
import com.noticemedan.p2p.Message.enums.NetworkMessageType;

import java.io.*;
import java.net.*;
import java.util.*;

public class Node implements Runnable {
	private ServerSocket serverSocket;

	private Hashtable<Integer, String> data;
	private Hashtable<Integer, String> backup;

	private NodeInfo self;
	private NodeInfo front;
	private NodeInfo back;

	public Node(String ip, Integer port) throws IOException {
		this.self = new NodeInfo(ip, port);
		this.data = new Hashtable<>();
		this.backup = new Hashtable<>();
		this.serverSocket = new ServerSocket(port);
	}

	@Override
	public void run() {
		while(true) {
			try {
				Socket s = serverSocket.accept();
				new MessageListener(s).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param msg either a Network- or Data Message
	 * @param receiver
	 */
	private void sendMessage(Message msg, NodeInfo receiver) throws RebuildingNetworkException {
		System.out.println("Sending..:" + msg.getMessageType() + " to: " + receiver.getPort());
		try {
			Socket socket = new Socket(receiver.getIp(), receiver.getPort());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(msg);
			socket.close();
			out.close();
		} catch (ConnectException | NoRouteToHostException e) {
			rebuildNetwork(receiver);
			throw new RebuildingNetworkException("Rebuilding Network", receiver);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void rebuildNetwork(NodeInfo disconnectedNode){
		List<NodeInfo> nodes = Arrays.asList(this.self, disconnectedNode);
		boolean sendForward = disconnectedNode.equals(this.back);
		Message msg = new NetworkMessage(NetworkMessageType.RECONNECT, nodes, sendForward);
		try {
			if (sendForward) {
				this.setBack(null);
				this.mergeBackupData();
				this.sendBackup(data);
				sendMessage(msg, this.front);
			}
			else {
				this.setFront(null);
				sendMessage(msg, this.back);
			}
		}
		//This is an indicator that more than one Node has crashed. We handle this by killing this process.
		catch(RebuildingNetworkException e){
			System.out.println("Unsupported Network State, Turning Off Node...");
			Runtime.getRuntime().exit(0);
		}
	}

	private void handleConnect(Message msg) throws RebuildingNetworkException {
		NodeInfo node = msg.getNode();

		//The very first Node being created is handled here.
		if(this.back == null) {
			NetworkMessage confirm = new NetworkMessage(NetworkMessageType.CONFIRM, this.self);
			this.sendMessage(confirm, node);
			this.setBack(node);
			if(front == null)
				handleConfirm(msg);
		}
		else{
			if(this.back.getPort().equals(msg.getNode().getPort()))
				return;

			Message switchMessage = new NetworkMessage(NetworkMessageType.SWITCH, node);
			this.sendMessage(switchMessage, this.back);

			Message confirm = new NetworkMessage(NetworkMessageType.CONFIRM, this.self);
			this.sendMessage(confirm, node);

			this.setBack(node);

		}
	}

	private void handleConfirm(Message msg) throws RebuildingNetworkException {
		if(this.front == null){
			NodeInfo node = msg.getNode();
			NetworkMessage connect = new NetworkMessage(NetworkMessageType.CONNECT, this.self);
			this.sendMessage(connect, node);
			this.setFront(node);
		}
	}


	private void handleReconnect(NetworkMessage msg) throws RebuildingNetworkException {
        if(msg.isForward()) {
            if (msg.getNodes().get(1).equals(this.front)) {
                Message connectMessage = new NetworkMessage(NetworkMessageType.CONNECT, this.self);
                this.setFront(null);
                this.sendMessage(connectMessage, msg.getNodes().get(0));
            }
            else{
                this.sendMessage(msg, this.front);
            }
        } else{
            if (msg.getNodes().get(1).equals(this.back)) {
                Message connectMessage = new NetworkMessage(NetworkMessageType.CONFIRM, this.self);
                this.setBack(null);
                this.sendMessage(connectMessage, msg.getNode());
            }
            else{
                this.sendMessage(msg, this.back);
            }
        }

	}


	private void handleSwitchFront(NetworkMessage msg) throws RebuildingNetworkException {
		this.sendBackup(this.data);
		NetworkMessage confirmMessage = new NetworkMessage(NetworkMessageType.CONNECT, this.self);
		this.sendMessage(confirmMessage, msg.getNode());
		this.setFront(msg.getNode());
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



	public void connect(NodeInfo sender, NodeInfo receiver) throws RebuildingNetworkException{
		NetworkMessage msg = new NetworkMessage(NetworkMessageType.CONNECT, sender);
		this.sendMessage(msg, receiver);
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
	}

	private void handleGet(DataMessage msg) throws RebuildingNetworkException {
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

	public void handlePut(DataMessage msg) throws RebuildingNetworkException {
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

	private void sendBackupPut(Integer key, String value) throws RebuildingNetworkException {
		if(this.front != null) {
			DataMessage backupMessage = new DataMessage(DataMessageType.BACKUP, this.front, key, value, null);
			this.sendMessage(backupMessage, this.front);
		}
	}

	private void sendBackup(Hashtable data) throws RebuildingNetworkException {
		DataMessage backupMessage = new DataMessage(DataMessageType.BACKUP, this.front, data);
		this.sendMessage(backupMessage, this.front);
	}

	private void mergeBackupData() {
		this.data.putAll(this.backup);
		this.backup.clear();
	}

	/**
	 * Inner Class used for interpreting all received messages on the ServerSocket.
	 */
	class MessageListener extends Thread {
		Socket s;

		MessageListener(Socket s) {
			this.s = s;
		}

		@Override
		public void run() {
			Message msg = null;
			try {
				ObjectInputStream in = new ObjectInputStream(s.getInputStream());
				msg = (Message) in.readObject();
				switch (msg.getMessageType()) {
					case DATA:
						handleDataMessage(msg);
						break;
					case NETWORK:
						handleNetworkMessage(msg);
						break;
					case ERROR:
						System.out.println("The Network is rebuilding, please try again...");
						Runtime.getRuntime().exit(0);
						break;
					default:
						System.out.println("Unknown MessageType");
						break;
				}
				s.close();
				in.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			} catch (RebuildingNetworkException re){
				Message error = new Message(re.getDisconnectedNode(), MessageType.ERROR);
				try {
					sendMessage(error, msg.getNode());
				}
				catch (RebuildingNetworkException e) {
					System.out.println("Unsupported Network State, Turning Off Node...");
					Runtime.getRuntime().exit(0);
				}
			}
			printNodeInformation();
		}

		private void handleNetworkMessage(Message message) throws RebuildingNetworkException {
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

		private void handleDataMessage(Message message) throws RebuildingNetworkException {
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
}
