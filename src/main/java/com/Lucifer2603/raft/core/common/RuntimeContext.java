package com.Lucifer2603.raft.core.common;


import com.Lucifer2603.raft.consistent.log.LogManager;
import com.Lucifer2603.raft.consistent.log.LogManagerHelper;
import com.Lucifer2603.raft.constants.RoleType;
import com.Lucifer2603.raft.core.Event.EventEngine;
import com.Lucifer2603.raft.core.replicate.BackendJob;
import com.Lucifer2603.raft.core.replicate.event.AppendLogClientEvent;
import com.Lucifer2603.raft.net.NetManager;
import com.Lucifer2603.raft.utils.buffer.ByteBufferPool;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhangchen20
 */
public class RuntimeContext {

    // 当前的角色
    public RoleType roleType;

    // 当前的term
    public volatile int currentTerm;

    // 当前的leader
    public volatile int currentLeader;




    // Component
    public EventEngine eventEngine;

    public LogManagerHelper logManager;

    public NetManager netManager;

    public BackendJob timeJob;

    public ByteBufferPool bufferPool;

    // todo 内存管理器 境外内存
    // todo destory时,使用Runtime.getRuntime().addShutdownHook




    /**
     * candidate
     */
    // 当candidate的时候,被缓存的客户端消息
    public List<AppendLogClientEvent> candidateWaitingAppendingLogEvents = new LinkedList<>();

    // 当candidate的时候,accpet/reject的serverNumber
    public Set electAcceptSet = new HashSet<>();
    public Set electRejectSet = new HashSet<>();

    // 当当candidate的时候,elect timeout
    public volatile long nextElectTimeout;

    // 当follower的时候,是否accept/reject过VoteRequest
    // 如果在某个term voteFor过, 那么设定为这个term的值.
    public volatile int voteForFlag = 0;





    private static RuntimeContext context;

    private ReentrantLock lock;

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }

    // todo 按照当前的role, 返回对应的cxt.
    public static RuntimeContext get() {
        return context;
    }


    public void init() {
    }

    // clear all resources and flags.
    public void clear() {

        electAcceptSet.clear();
        electAcceptSet.clear();

    }

    public void refresh() {
        // todo 与集群通信,获得最新的leader,term信息.
        lock();

        try {

        } finally {
            unlock();
        }
    }




}
