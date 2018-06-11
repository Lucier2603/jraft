package com.Lucifer2603.raft.protocol.serializable;

import com.Lucifer2603.raft.cluster.Member;

/**
 * @author zhangchen20
 */
public class BaseResponse implements RaftSerializable {

    protected long term;
    protected Member from;

    public long getTerm() {
        return term;
    }

    public Member getFrom() {
        return from;
    }
}
