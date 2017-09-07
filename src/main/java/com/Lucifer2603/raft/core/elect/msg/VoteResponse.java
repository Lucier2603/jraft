package com.Lucifer2603.raft.core.elect.msg;

import com.Lucifer2603.raft.net.msg.RaftMessage;

/**
 * @author zhangchen20
 */
public class VoteResponse extends RaftMessage {

    public int replyLeader;

    public int replyTerm;

    public boolean acceptFlag;
}
