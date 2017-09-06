package com.Lucifer2603.raft.core.replicate.event;


import com.Lucifer2603.raft.consistent.log.LogEntry;
import com.Lucifer2603.raft.core.Event.Event;

/**
 * @author zhangchen20
 */
public class NewLogCommitEvent extends Event {

    public LogEntry log;
}
