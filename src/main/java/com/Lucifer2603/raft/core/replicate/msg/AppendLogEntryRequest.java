package com.Lucifer2603.raft.core.replicate.msg;

import com.Lucifer2603.raft.consistent.log.LogEntry;
import com.Lucifer2603.raft.constants.MessageType;
import com.Lucifer2603.raft.net.msg.RaftMessage;

/**
 * @author zhangchen20
 */
public class AppendLogEntryRequest extends RaftMessage {

    public AppendLogEntryRequest() {
        this.msgType = MessageType.APPEND_LOG_REQ;
    }

    public LogEntry[] newEntries;

    public long prevLogTerm;

    public long prevLogIndex;

    public long leaderCommitIndex;

}
