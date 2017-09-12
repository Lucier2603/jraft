package com.Lucifer2603.raft.core.elect.handler;

import com.Lucifer2603.raft.conf.LocalConfig;
import com.Lucifer2603.raft.constants.RoleType;
import com.Lucifer2603.raft.core.Event.Event;
import com.Lucifer2603.raft.core.EventHandler;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.elect.event.NewLeaderEvent;
import com.Lucifer2603.raft.core.elect.event.StartElectEvent;
import com.Lucifer2603.raft.core.replicate.event.AppendLogClientEvent;

import java.util.List;

/**
 * @author zhangchen20
 */

// invokes when this server is new leader.
public class NewLeaderHandler implements EventHandler {

    public void process(Event e) {

        if (!(e instanceof NewLeaderEvent)) {
            return;
        }

        NewLeaderEvent event = (NewLeaderEvent) e;
        RuntimeContext cxt = RuntimeContext.get();

        // 表示自己已经当选为leader
        cxt.currentLeader = LocalConfig.number;
        cxt.roleType = RoleType.Leader;

        cxt.clear();
        cxt.timeJob.start(RoleType.Leader);

        // 处理在candidate时候的缓存.
        List<AppendLogClientEvent> appendings = cxt.candidateWaitingAppendingLogEvents;
        appendings.forEach(v -> cxt.eventEngine.publishEvent(v));

        e.pass();
    }

    public void onException(Event e, Throwable t) {

    }
}
