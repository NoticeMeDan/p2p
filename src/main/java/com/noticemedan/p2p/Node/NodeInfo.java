package com.noticemedan.p2p.Node;

import java.io.Serializable;

public class NodeInfo implements Serializable {
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
	public boolean equals(Object obj) {
		if(obj instanceof NodeInfo)
			return (this.port.equals(((NodeInfo) obj).getPort())) && this.ip.equals(((NodeInfo) obj).getIp());
		return super.equals(obj);
	}
}
