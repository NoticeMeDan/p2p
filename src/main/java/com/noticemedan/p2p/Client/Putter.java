package com.noticemedan.p2p.Client;
import com.noticemedan.p2p.Message.DataMessage;
import com.noticemedan.p2p.Message.Message;
import com.noticemedan.p2p.Message.MessageType;
import com.noticemedan.p2p.Node.Node;
import com.noticemedan.p2p.Node.NodeInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Putter{

    private NodeInfo info;

    public Putter(NodeInfo info) {
        this.info = info;
    }

    public boolean put(Integer key, String value, NodeInfo receiver) {
        boolean success = false;

        //Create a put message with the information of this Putter
        Message msg = new DataMessage(MessageType.PUT, this.info, key, value);
        try {
            //Create the serverSocket for this client
            ServerSocket clientSocket = new ServerSocket(this.info.getPort());

            //Send the Message
            Socket senderSocket = new Socket(receiver.getIp(), receiver.getPort());
            ObjectOutputStream out = new ObjectOutputStream(senderSocket.getOutputStream());
            out.writeObject(msg);

            //Wait for an answer
            Socket s = clientSocket.accept();
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            success = in.readBoolean();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(success);
        return success;
    }
}
