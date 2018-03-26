package com.Lucifer2603.raft.core.common;

/**
 * @author zhangchen20
 */
public class FollowerRuntimeContext extends RuntimeContext {


    // 当follower的时候,上次接受到来自leader的 HeartBeat 的时间
    public volatile long lastHeartBeatTime;
}
