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
import com.Lucifer2603.raft.core.replicate.event.HeartbeatBroadcastEvent;
import com.Lucifer2603.raft.core.replicate.msg.AppendLogEntryRequest;
import com.Lucifer2603.raft.net.NetManager;
import com.Lucifer2603.raft.net.msg.MessageBuilder;

/**
 * @author zhangchen20
 */
public class HeartbeatBroadcastHandler implements EventHandler {

    public void process(Event e) {

        if (!(e instanceof HeartbeatBroadcastEvent)) {
            return;
        }

        AppendLogResponseEvent event = (AppendLogResponseEvent) e;
        RuntimeContext cxt = RuntimeContext.get();


        // 检查当前role
        if (cxt.roleType != RoleType.Leader) {
            event.ends();
            return;
        }


        LogManager logMgr = cxt.logManager;
        NetManager netMgr = cxt.netManager;

        for (int followerNumber : ClusterConfig.SERVER_NOS) {

            if (followerNumber == LocalConfig.number) continue;

            AppendLogEntryRequest request = MessageBuilder.buildAppendLogEntryRequest(cxt);
            // todo request.toServer = ClusterConfig.SERVER_PORTS[]
            request.leaderCommitIndex = logMgr.commitIndex;
            request.newEntries = logMgr.findFollowerProgress(followerNumber);

            LogEntry prevLog = logMgr.findByPosition(logMgr.findFollowerPosition(followerNumber));
            request.prevLogIndex = prevLog.logNo;
            request.prevLogTerm = prevLog.logTerm;
            request.toServer = followerNumber;

            netMgr.saveMsg(request);
        }

    }
}
