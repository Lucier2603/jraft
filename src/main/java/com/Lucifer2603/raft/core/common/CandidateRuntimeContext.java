package com.Lucifer2603.raft.core.common;

/**
 * @author zhangchen20
 */
public class CandidateRuntimeContext extends RuntimeContext {

    // 当当candidate的时候,elect timeout
    public volatile long nextElectTimeout;

}
