package com.noticemedan.p2p;

import com.noticemedan.p2p.Interfaces.PeerNode;

import java.util.*;

public class P2PNode implements PeerNode {

	private Hashtable<Integer, String> table;
	private Map<Double, Hashtable<Integer, String>> partnerTables;
	private double id;
	private int port;
	private List<P2PNode> partners;

	public P2PNode(int port) {
		this.port = port;
		this.partners = new ArrayList<>();
		this.partnerTables = new HashMap<>();
		this.table = new Hashtable<>();
		this.id = Math.random() * 360;
	}

	public P2PNode(int port, P2PNode node) {
		this.port = port;
		this.partnerTables = new HashMap<>();
		this.partners = new ArrayList<>();
		this.partners.add(node);
		this.table = new Hashtable<>();
		this.id = Math.random() * 360;
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
	public void assignPartner() {

	}

	@Override
	public void healthCheck() {

	}



	public static void main(String[] args) {
		System.out.println("Hi mom");
	}
}
