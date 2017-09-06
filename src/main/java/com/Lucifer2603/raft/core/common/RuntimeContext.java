package com.Lucifer2603.raft.core.common;


import com.Lucifer2603.raft.consistent.log.LogManager;
import com.Lucifer2603.raft.constants.RoleType;
import com.Lucifer2603.raft.core.Event.EventEngine;
import com.Lucifer2603.raft.net.NetManager;

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



    // Useage
    public EventEngine eventEngine;

    public LogManager logManager;

    public NetManager netManager;



    private static RuntimeContext context;

    public static RuntimeContext get() {
        return context;
    }
}
