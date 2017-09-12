package com.Lucifer2603.raft.core.elect.handler;

import com.Lucifer2603.raft.conf.ClusterConfig;
import com.Lucifer2603.raft.constants.RoleType;
import com.Lucifer2603.raft.core.Event.Event;
import com.Lucifer2603.raft.core.EventHandler;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.elect.event.NewLeaderEvent;
import com.Lucifer2603.raft.core.elect.event.VoteRequestEvent;
import com.Lucifer2603.raft.core.elect.event.VoteResponseEvent;
import com.Lucifer2603.raft.core.elect.msg.VoteResponse;

/**
 * @author zhangchen20
 */
public class VoteResponseHandler implements EventHandler {

    public void process(Event e) {

        if (!(e instanceof VoteResponseEvent)) {
            return;
        }

        VoteResponseEvent event = (VoteResponseEvent) e;
        RuntimeContext cxt = RuntimeContext.get();
        VoteResponse response = (VoteResponse) event.raftMessage;

        if (cxt.roleType != RoleType.Candidate) {
            event.ends();
            return;
        }

        // 收到其他节点的response信息.
        // 如果其他节点的term更高,那当前节点一定是落后的(因为其他节点一定是与其leader沟通后才回复. 如果其他节点是candidate,则根本不会回复.)
        if (cxt.currentTerm < response.replyTerm) {
            // 因此,这时需要把自己降低为follower. 然后update自身.
            cxt.roleType = RoleType.Follower;
            cxt.currentTerm = response.replyTerm;
            cxt.currentLeader = response.replyLeader;
//            cxt.refresh();
            cxt.timeJob.start(RoleType.Follower);

            event.ends();
            return;
        }

        // 如果其他节点的term更低,则需要先同步到最新之后,再回复. 因此这里可以不考虑这个term的情况.

        if (response.acceptFlag) {
            // accept
            cxt.electAcceptSet.add(response.fromServer);
        } else {
            cxt.electRejectSet.add(response.fromServer);
        }

        // 检查自己的accept是否达到半数
        if (cxt.electAcceptSet.size() > ClusterConfig.CLUSTER_SIZE / 2) {
            cxt.eventEngine.publishEvent(new NewLeaderEvent());

            // todo 对后续的elect消息不response.
        }

        event.pass();
    }

    public void onException(Event e, Throwable t) {

    }
}
