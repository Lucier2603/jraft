package com.Lucifer2603.raft.protocol.serializable;

/**
 * @author zhangchen20
 */
public class VoteRequest extends BaseRequest {

    private int candidateId;
    private long logIndex;
    private long logTerm;

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public long getLogIndex() {
        return logIndex;
    }

    public void setLogIndex(long logIndex) {
        this.logIndex = logIndex;
    }

    public long getLogTerm() {
        return logTerm;
    }

    public void setLogTerm(long logTerm) {
        this.logTerm = logTerm;
    }
}
