package com.noticemedan.p2p.Client;

import com.noticemedan.p2p.Message.DataMessage;
import com.noticemedan.p2p.Message.Message;
import com.noticemedan.p2p.Message.enums.DataMessageType;
import com.noticemedan.p2p.Node.NodeInfo;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
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
            try{
                DataMessage message = (DataMessage) in.readObject();
                return message.getValue();
            }catch(OptionalDataException e){
                if(!in.readBoolean())
                    return "The item could not be found";
            }

        } catch(SocketException e) {
            return "There was a connection error. Please try again.";//Handle TCP Error
        }catch (IOException | ClassNotFoundException e) {
            return "An error occured: " + e.getMessage();
        }
        return null;
    }
}
