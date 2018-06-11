package com.Lucifer2603.raft.protocol.serializable;

/**
 * @author zhangchen20
 */
public class AppendResponse extends BaseResponse {

    private boolean succeeded;
    private long logIndex;

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public long getLogIndex() {
        return logIndex;
    }

    public void setLogIndex(long logIndex) {
        this.logIndex = logIndex;
    }

    public String getType() {
        return "AppendResponse";
    }
}
