package com.Lucifer2603.raft.protocol.serializable;

import java.util.List;

import com.Lucifer2603.raft.protocol.log.LogEntry;

/**
 * @author zhangchen20
 */
public class AppendRequest extends BaseRequest {

    // log
    private long logIndex;
    private long logTerm;

    private List<LogEntry> entries;

    private long commitIndex;

    public long getLogIndex() {
        return logIndex;
    }

    public long getLogTerm() {
        return logTerm;
    }

    public List<LogEntry> getEntries() {
        return entries;
    }

    public long getCommitIndex() {
        return commitIndex;
    }
}
