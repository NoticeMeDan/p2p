package com.noticemedan.p2p.Client;

import com.noticemedan.p2p.Message.DataMessage;
import com.noticemedan.p2p.Message.enums.DataMessageType;
import com.noticemedan.p2p.Node.NodeInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Getter {

    private NodeInfo info;

    public Getter(NodeInfo info) {
        this.info = info;
    }

    public String get(Integer key, NodeInfo receiver) {
        DataMessage msg = new DataMessage(DataMessageType.GET, this.info, key);
        try {
            ServerSocket clientSocket = new ServerSocket(this.info.getPort());

            //Send the Message
            Socket senderSocket = new Socket(receiver.getIp(), receiver.getPort());
            ObjectOutputStream out = new ObjectOutputStream(senderSocket.getOutputStream());
            out.writeObject(msg);

            //Wait for an answer
            Socket s = clientSocket.accept();
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            DataMessage answer = (DataMessage) in.readObject();

            if (!answer.getType().equals(DataMessageType.PUT)) {
                return "Received a message, but it was not a PUT. Something is not right here.";
            }

            return answer.getValue();
        } catch(SocketException e) {
            return null;//Handle TCP Error
        }catch (IOException | ClassNotFoundException e) {
            return "An error occured: " + e.getMessage();
        }

    }
}
