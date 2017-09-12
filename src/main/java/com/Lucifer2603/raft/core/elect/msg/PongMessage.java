package com.Lucifer2603.raft.core.elect.msg;

/**
 * @author zhangchen20
 */
public class PongMessage {
    public boolean isLeader;
    public int leaderTerm;
}
