package com.Lucifer2603.raft.core.replicate.handler;

import com.baidu.fpd.raft.consistent.log.LogEntry;
import com.baidu.fpd.raft.consistent.log.LogManager;
import com.baidu.fpd.raft.constants.RoleType;
import com.baidu.fpd.raft.core.Event.Event;
import com.baidu.fpd.raft.core.EventHandler;
import com.baidu.fpd.raft.core.common.RuntimeContext;
import com.baidu.fpd.raft.core.replicate.event.NewLogInsertEvent;

/**
 * @author zhangchen20
 */
public class NewLogInsertHandler implements EventHandler {

    // leader端
    public void process(Event e) {

        if (!(e instanceof NewLogInsertEvent)) {
            return;
        }

        NewLogInsertEvent event = (NewLogInsertEvent) e;
        RuntimeContext cxt = RuntimeContext.get();

        if (cxt.roleType == RoleType.Candidate) {
            // todo 缓存,然后转发
            e.ends();
            return;
        }

        if (cxt.roleType == RoleType.Follower) {
            // 转发给leader
            e.ends();
            return;
        }

        // leader
        LogManager logMgr = cxt.logManager;

        // 更新本地
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


        // 广播一次
        // todo braodcast

    }
}
