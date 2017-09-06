package com.Lucifer2603.raft.core.elect.handler;

import com.baidu.fpd.raft.constants.RoleType;
import com.baidu.fpd.raft.core.Event.Event;
import com.baidu.fpd.raft.core.EventHandler;
import com.baidu.fpd.raft.core.common.RuntimeContext;
import com.baidu.fpd.raft.core.elect.event.StartNewElectionEvent;

/**
 * @author zhangchen20
 */
public class StartNewElectionHandler implements EventHandler {

    // 开始一个新的选举
    public void process(Event e) {

        if (!(e instanceof StartNewElectionEvent)) {
            return;
        }

        StartNewElectionEvent event = (StartNewElectionEvent) e;
        RuntimeContext cxt = RuntimeContext.get();

        // 提升当前role
        if (cxt.roleType == RoleType.Leader) {
            // todo
        }

        cxt.roleType = RoleType.Candidate;
        cxt.currentTerm++;
        cxt.currentLeader = -1;

        // 发出vote request
        // todo


    }
}
