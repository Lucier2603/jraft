package com.Lucifer2603.raft.core.common;


import com.Lucifer2603.raft.consistent.log.LogManager;
import com.Lucifer2603.raft.constants.RoleType;
import com.Lucifer2603.raft.core.Event.EventEngine;
import com.Lucifer2603.raft.core.replicate.event.AppendLogClientEvent;
import com.Lucifer2603.raft.net.NetManager;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zhangchen20
 */
public class RuntimeContext {

    // 当前的角色
    public RoleType roleType;

    // 当前的term
    public int currentTerm;

    // 当前的leader
    public int currentLeader;



    // Component
    public EventEngine eventEngine;

    public LogManager logManager;

    public NetManager netManager;



    // 当candidate的时候,被缓存的客户端消息
    public List<AppendLogClientEvent> candidateWaitingAppendingLogEvents = new LinkedList<>();

    // 当follower的时候,上次接受到 HeartBeat 的时间
    public long lastHeartBeatTime;


    private static RuntimeContext context;

    public static RuntimeContext get() {
        return context;
    }
}
