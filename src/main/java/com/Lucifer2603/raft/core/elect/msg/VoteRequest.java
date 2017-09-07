package com.Lucifer2603.raft.core.elect.msg;

import com.Lucifer2603.raft.constants.MessageType;
import com.Lucifer2603.raft.net.msg.RaftMessage;

/**
 * @author zhangchen20
 */
public class VoteRequest extends RaftMessage {

    public VoteRequest() {
        this.msgType = MessageType.VOTE_REQ;
    }

    public int candidateTerm;

    public int candidateNumber;

    public int lastLogTerm;

    public int lastLogIndex;
}
