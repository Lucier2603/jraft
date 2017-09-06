package com.Lucifer2603.raft.core.replicate.msg;

import com.Lucifer2603.raft.constants.MessageType;
import com.Lucifer2603.raft.net.msg.RaftMessage;

/**
 * @author zhangchen20
 */
public class AppendLogEntryResponse extends RaftMessage {

    public AppendLogEntryResponse() {
        this.msgType = MessageType.APPEND_LOG_RESP;
    }

    public boolean success;

    public int prevTerm;

    public int prevIndex;
}
