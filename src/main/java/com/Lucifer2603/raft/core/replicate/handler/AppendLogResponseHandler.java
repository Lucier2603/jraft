package com.Lucifer2603.raft.core.replicate.handler;



import com.Lucifer2603.raft.conf.ClusterConfig;
import com.Lucifer2603.raft.conf.LocalConfig;
import com.Lucifer2603.raft.consistent.log.LogEntry;
import com.Lucifer2603.raft.consistent.log.LogManager;
import com.Lucifer2603.raft.constants.RoleType;
import com.Lucifer2603.raft.core.Event.Event;
import com.Lucifer2603.raft.core.EventHandler;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.replicate.event.AppendLogResponseEvent;
import com.Lucifer2603.raft.core.replicate.msg.AppendLogEntryRequest;
import com.Lucifer2603.raft.core.replicate.msg.AppendLogEntryResponse;

import java.util.Map;
import java.util.Set;

/**
 * @author zhangchen20
 */
public class AppendLogResponseHandler implements EventHandler {

    public void process(Event e) {

        if (!(e instanceof AppendLogResponseEvent)) {
            return;
        }

        AppendLogResponseEvent event = (AppendLogResponseEvent) e;
        RuntimeContext cxt = RuntimeContext.get();

        // 检查role
        if (cxt.roleType != RoleType.Leader) {
            e.ends();
            return;
        }

        AppendLogEntryResponse msg = (AppendLogEntryResponse) event.raftMessage;
        LogManager logMgr = cxt.logManager;

        // 检查term
        if (cxt.currentTerm > msg.fromTerm) {
            // ignore
            e.ends();
            return;
        }

        if (cxt.currentTerm < msg.fromTerm) {
            // 自身有问题, 降级
            // todo
        }

        AppendLogEntryRequest requestMsg = (AppendLogEntryRequest) cxt.netManager.getMsg(cxt.currentTerm, msg.relatedMsgId);

        if (requestMsg == null) {
            throw new RuntimeException("");
        }

        // do real work
        if (msg.success) {

            // 更新本地follower记录
            int updateIdx = logMgr.findByTermAndNo(msg.prevTerm, msg.prevIndex);
            logMgr.updateProgress(msg.fromServer, updateIdx);

            // 尝试commit
            commit(logMgr);

        } else {
            // 两种情况导致success = FAIL
            // 1. leader的term过旧.在之前已被处理
            // 2. 双方日志不一致. 此时,leader会将该follower对应的nextIndex - 1. 每次reject都会 -1, 直到双方agree.

            // 拿到最新的match点
            int matchTerm = requestMsg.prevLogTerm;
            int matchIndex = requestMsg.prevLogIndex;

            int matchPos = logMgr.findByTermAndNo(matchTerm, matchIndex);
            logMgr.updateProgress(requestMsg.fromServer, matchPos);
        }
    }


    private void commit(LogManager logMgr) {

        // 检查是否达到commit标准.
        Map<Integer, Set<Integer>> prepareMap = logMgr.getPrepareLogMap();
        int half = ClusterConfig.CLUSTER_SIZE / 2 + 1;

        for (Integer logPosition : prepareMap.keySet()) {
            if (prepareMap.get(logPosition).size() > half) {
                // 该logPosition可以判定为 can commit

                // 那么 commitIndex++, 然后从 prepareMap remove.
                logMgr.commitIndex = logMgr.commitIndex > logPosition ? logMgr.commitIndex : logPosition;
                logMgr.getPrepareLogMap().remove(logPosition);

                /**
                 * 状态
                 * 1. prepare
                 * 2. committed
                 * 3. applied
                 */
                LogEntry logEntry = logMgr.updateLogEntryStatus(logPosition, 2);
            }
        }

    }
}
