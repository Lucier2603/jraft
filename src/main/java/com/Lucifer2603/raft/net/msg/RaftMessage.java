package com.Lucifer2603.raft.net.msg;

import com.Lucifer2603.raft.constants.MessageType;

import java.io.Serializable;

/**
 * @author zhangchen20
 */
public class RaftMessage implements Serializable {

    public long msgId;

    public int fromServer;

    public int toServer;

    public int fromTerm;

    public long relatedMsgId;

    public MessageType msgType;


//    public void writeObject(ObjectOutputStream output) {
//        try {
//            output.defaultWriteObject();
//            output.writeObject(type);
//            output.writeInt(fromServer);
//            output.writeInt(fromTerm);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void readObject(ObjectInputStream input) {
//        try {
//            input.defaultReadObject();
//            this.type = (MessageType) input.readObject();
//            this.fromServer = input.readInt();
//            this.fromTerm = input.readInt();
//            this.content = input.readUTF();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
