package com.noticemedan.p2p;

import java.io.Serializable;

public class NodeInfo implements Serializable, Comparable {
	private String ip;
	private Integer port;

	public NodeInfo(String ip, Integer port) {
		this.ip = ip;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public Integer getPort() {
		return port;
	}

	@Override
	public String toString() {
		return "NodeInfo{" +
				"ip='" + ip + '\'' +
				", port=" + port +
				'}';
	}

	@Override
	public int compareTo(Object o) {
		int samePort = this.getPort().compareTo(((NodeInfo) o).getPort());
		int sameIP = this.getIp().compareTo(((NodeInfo) o).getIp());
		if(sameIP == samePort)
			return sameIP;
		else return -1;
	}
}
