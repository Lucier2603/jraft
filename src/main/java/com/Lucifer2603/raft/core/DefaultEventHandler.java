package com.Lucifer2603.raft.core;

import com.Lucifer2603.raft.constants.RoleType;
import com.Lucifer2603.raft.core.Event.Event;
import com.Lucifer2603.raft.core.common.CandidateRuntimeContext;
import com.Lucifer2603.raft.core.common.FollowerRuntimeContext;
import com.Lucifer2603.raft.core.common.LeaderRuntimeContext;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.net.msg.RaftMessage;

/**
 * 默认eventhandler
 *
 * 使用了模版方法, 规定了所有handler的流程
 *
 * @author zhangchen20
 */
public class DefaultEventHandler implements EventHandler {

    public void process(Event e) {

        checkEvent(e);

        // 如果event校验不过关, 直接返回不处理
        if (!checkEvent(e)) {
            return;
        }

        RuntimeContext cxt = RuntimeContext.get();

        if (cxt.roleType == RoleType.Leader) {
            processAsLeader(e, (LeaderRuntimeContext) cxt);
        } else
        if (cxt.roleType == RoleType.Candidate) {
            processAsCandidate(e, (CandidateRuntimeContext) cxt);
        } else
        if (cxt.roleType == RoleType.Follower) {
            processAsFollower(e, (FollowerRuntimeContext) cxt);
        }

    }

    public void onException(Event e, Throwable t) {

    }


    private void checkTerm(Event e) {

        RaftMessage msg = e.raftMessage;
        RuntimeContext cxt = RuntimeContext.get();

        if (!doCheckTerm(msg.fromTerm, cxt.currentTerm, e, cxt)) {
            // todo
        }
    }

    // need override
    public boolean doCheckTerm(int remoteTerm, int localTerm, Event e, RuntimeContext cxt) {
        return false;
    }

    public boolean checkEvent(Event e) {
        return true;
    }

    public void processAsLeader(Event e, LeaderRuntimeContext cxt) {

    }

    public void processAsCandidate(Event e, CandidateRuntimeContext cxt) {

    }

    public void processAsFollower(Event e, FollowerRuntimeContext cxt) {

    }
}
