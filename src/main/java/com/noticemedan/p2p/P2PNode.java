package com.noticemedan.p2p;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class P2PNode {
	private ServerSocket server;
	private Socket client;

	private Integer front;
	private Integer back;

	public void start(int port) throws IOException {
		this.server = new ServerSocket(port);
	}

	public void start(int port, int target) {

	}

	public static void main(String[] args) {
		System.out.println("Hi mom");
	}
}
