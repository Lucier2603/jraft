package com.Lucifer2603.raft.core.elect.event;


import com.Lucifer2603.raft.core.Event.Event;

/**
 * @author zhangchen20
 */
public class VoteRequestEvent extends Event {

    public String getName() {
        return "VoteRequestEvent";
    }
}
