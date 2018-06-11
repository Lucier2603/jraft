package com.Lucifer2603.raft.protocol.serializable;

/**
 * @author zhangchen20
 */
public class VoteResponse extends BaseResponse {

    private boolean voted;

    public boolean isVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    public String getType() {
        return "VoteResponse";
    }
}
