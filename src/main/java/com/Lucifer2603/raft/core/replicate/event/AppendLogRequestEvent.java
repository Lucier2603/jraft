package com.Lucifer2603.raft.core.replicate.event;


import com.Lucifer2603.raft.core.Event.Event;

/**
 * 被作为心跳发送
 * @author zhangchen20
 */
public class AppendLogRequestEvent extends Event {

    // if local log failed. It should record local prevTerm and prevIndex that matches leader
    public boolean successFlag;

    public int prevMatchTerm;

    public int prevMatchIndex;

    public String getName() {
        return "AppendLogRequestEvent";
    }
}
