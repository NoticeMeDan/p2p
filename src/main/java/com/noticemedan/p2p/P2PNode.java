package com.noticemedan.p2p;

import com.noticemedan.p2p.Interfaces.PeerNode;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class P2PNode implements PeerNode {

	//Hashtable with key/values stored in the Node.
	private Hashtable<Integer, String> table;

	//Map of HashTables. Maps the ID of a Node to a Hashtable that contains the backup values.
	private Map<Double, Hashtable<Integer, String>> partnerTables;

	//The id of a Node
	private double id;

	//Port of the Node
	private int port;

	//The list of known P2PNode 'Neighbours. 0 is the default .next
	private List<P2PNode> partners;

	private ServerSocket nodeSocket;
	private String getInetAddress;

	public P2PNode(int port) throws IOException {
		this.port = port;
		this.partners = new ArrayList<>();
		this.partnerTables = new HashMap<>();
		this.table = new Hashtable<>();
		this.id = Math.random() * 360;

		this.nodeSocket = new ServerSocket(port);
		Socket s = nodeSocket.accept();
		InputStream in = s.getInputStream();
		in.read();
	}

	public P2PNode(int port, P2PNode node) {
		this.port = port;
		this.partnerTables = new HashMap<>();
		this.partners = new ArrayList<>();
		this.partners.add(node);
		this.table = new Hashtable<>();
		this.id = Math.random() * 360;
	}

	//Make this with a Runnable thread that listens for input to the Node 'Socket'
	public void listen(){

	}

	@Override
	public void addValue(int key, String value) {
		this.table.put(key, value);
		this.sendBackup(key, value, this.id);
	}

	@Override
	public String sendValue(int key) {
		return null;
	}

	@Override
	public void sendBackup(int key, String value, double id) {
		//Not actual implementation. Needs to go and fetch the node with the given 'id', IE the 'Next' Node.
		partners.get(0).addPartnerValue(key, value, id);
	}

	@Override
	public void addPartnerValue(int key, String value, double id) {
		this.partnerTables.get(id).put(key, value);
	}

	@Override
	public void assignPartner(P2PNode node) {
		this.partners.add(node);
	}

	@Override
	public void healthCheck() {
		P2PNode next = this.partners.get(0);
		Socket nextSocket = null;
		try {
			nextSocket = new Socket(next.getInetAddress, next.getPort());
			String message = "Are you okay?";
			nextSocket.getOutputStream().write(message.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getPort() {
		return 0;
	}


	public static void main(String[] args) {
		System.out.println("Hi mom");
	}
}
