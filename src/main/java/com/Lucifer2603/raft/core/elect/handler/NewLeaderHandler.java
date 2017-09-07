package com.Lucifer2603.raft.core.elect.handler;

import com.Lucifer2603.raft.conf.LocalConfig;
import com.Lucifer2603.raft.constants.RoleType;
import com.Lucifer2603.raft.core.Event.Event;
import com.Lucifer2603.raft.core.EventHandler;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.elect.event.NewLeaderEvent;
import com.Lucifer2603.raft.core.elect.event.StartElectEvent;

/**
 * @author zhangchen20
 */
public class NewLeaderHandler implements EventHandler {

    public void process(Event e) {

        if (!(e instanceof StartElectEvent)) {
            return;
        }

        NewLeaderEvent event = (NewLeaderEvent) e;
        RuntimeContext cxt = RuntimeContext.get();

        // 表示自己已经当选为leader

        cxt.currentLeader = LocalConfig.number;
        cxt.roleType = RoleType.Leader;

        cxt.timeJob.start();
        cxt.refresh();
        // todo 处理在candidate时候的缓存.
        // cxt.candidateWaitingAppendingLogEvents;
    }
}
