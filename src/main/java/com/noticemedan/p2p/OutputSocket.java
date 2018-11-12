package com.noticemedan.p2p;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class OutputSocket {
    private Socket clientSocket;
    private ObjectOutputStream outputStream;

    public OutputSocket(String ipAddress, int port) {
        setClientSocket(ipAddress, port);
        updateOutputStream();
    }

    private void setClientSocket(String ipAddress, int port) {
        try {
            this.clientSocket = new Socket(ipAddress, port);
        } catch (IOException e) {
            throw new RuntimeException("Unable to set client-socket", e);
        }
    }

    private void updateOutputStream() {
        try {
            this.outputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("Error updating the output stream", e);
        }
    }

    public void write(Message message) {
        try {
            this.outputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write message", e);
        }
    }

    public void close() {
        closeClientSocket();
        closeOutputStream();
    }

    private void closeClientSocket() {
        try {
            this.clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing the client-socket", e);
        }
    }

    private void closeOutputStream() {
        try {
            this.outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing the output-stream", e);
        }
    }

}
