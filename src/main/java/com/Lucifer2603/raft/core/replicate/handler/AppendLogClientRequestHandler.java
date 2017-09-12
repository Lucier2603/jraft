package com.Lucifer2603.raft.core.replicate.handler;

import com.Lucifer2603.raft.consistent.log.LogEntry;
import com.Lucifer2603.raft.consistent.log.LogManager;
import com.Lucifer2603.raft.constants.RoleType;
import com.Lucifer2603.raft.core.Event.Event;
import com.Lucifer2603.raft.core.EventHandler;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.replicate.event.AppendLogClientEvent;

/**
 * @author zhangchen20
 */
public class AppendLogClientRequestHandler implements EventHandler {

    public void process(Event e) {

        if (!(e instanceof AppendLogClientEvent)) {
            return;
        }

        AppendLogClientEvent event = (AppendLogClientEvent) e;
        RuntimeContext cxt = RuntimeContext.get();

        // 检查角色是否leader
        if (cxt.roleType == RoleType.Candidate) {
            cxt.candidateWaitingAppendingLogEvents.add(event);
            event.ends();
            return;
        }

        if (cxt.roleType == RoleType.Follower) {
            // 转发给leader
            transmit(event);
            event.ends();
            return;
        }

        // leader
        LogManager logMgr = cxt.logManager;

        // 更新本地
        // 新建logEntry
        LogEntry entry = new LogEntry();
        // todo content
        entry.content = "";
        /**
         * 状态
         * 1. prepare
         * 2. committed
         * 3. applied
         */
        entry.status = 1;

        LogEntry prev = logMgr.getLatest();
        entry.logTerm = cxt.currentTerm;
        entry.logNo = (prev.logTerm == cxt.currentTerm) ? (prev.logNo + 1) : 1;

        logMgr.append(entry);

    }

    private void transmit(AppendLogClientEvent event) {
        // todo
    }

    public void onException(Event e, Throwable t) {

    }
}
