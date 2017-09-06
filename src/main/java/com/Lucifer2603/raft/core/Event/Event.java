package com.Lucifer2603.raft.core.Event;


import com.Lucifer2603.raft.net.msg.RaftMessage;

/**
 * @author zhangchen20
 */
public abstract class Event {

    public RaftMessage raftMessage;

    private boolean endsupFlag;



    public void ends() {
        endsupFlag = true;
    }

    public void pass() {
        endsupFlag = false;
    }

    public boolean shouldEnd() {
        return endsupFlag;
    }


}
