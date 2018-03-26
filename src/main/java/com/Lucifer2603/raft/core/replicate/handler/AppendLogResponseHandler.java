package com.Lucifer2603.raft.core.replicate.handler;



import com.Lucifer2603.raft.conf.ClusterConfig;
import com.Lucifer2603.raft.conf.LocalConfig;
import com.Lucifer2603.raft.consistent.log.LogEntry;
import com.Lucifer2603.raft.consistent.log.LogManager;
import com.Lucifer2603.raft.consistent.log.LogManagerHelper;
import com.Lucifer2603.raft.constants.RoleType;
import com.Lucifer2603.raft.core.DefaultEventHandler;
import com.Lucifer2603.raft.core.Event.Event;
import com.Lucifer2603.raft.core.EventHandler;
import com.Lucifer2603.raft.core.common.CandidateRuntimeContext;
import com.Lucifer2603.raft.core.common.FollowerRuntimeContext;
import com.Lucifer2603.raft.core.common.LeaderRuntimeContext;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.replicate.event.AppendLogResponseEvent;
import com.Lucifer2603.raft.core.replicate.msg.AppendLogEntryRequest;
import com.Lucifer2603.raft.core.replicate.msg.AppendLogEntryResponse;

import java.util.Map;
import java.util.Set;

/**
 * leader 收到 appendLog 回复
 * @author zhangchen20
 */
public class AppendLogResponseHandler extends DefaultEventHandler {

    @Override
    public boolean checkEvent(Event e) {
        return e instanceof AppendLogResponseEvent;
    }

    @Override
    public void processAsLeader(Event e1, LeaderRuntimeContext cxt) {

        AppendLogResponseEvent e = (AppendLogResponseEvent) e1;
        // todo
        AppendLogEntryRequest requestMsg = null;

        if (requestMsg == null) {
            throw new RuntimeException("");
        }

        // do real work
        AppendLogEntryResponse msg = (AppendLogEntryResponse) e.raftMessage;

        if (msg.success) {

            // 更新本地follower 的nextIndex记录
            cxt.updateFollowerMaxLogNo(msg.fromServer, msg.prevIndex);

            // 尝试commit
            tryCommit(msg.fromServer, msg.prevIndex);

        } else {
            // 两种情况导致success = FAIL
            // 1. leader的term过旧.在之前已被处理
            // 2. 双方日志不一致. 此时,leader会将该follower对应的nextIndex - 1. 每次reject都会 -1, 直到双方agree.
            // todo 这里有一个优化点, 随机取一个减少距离, 尽快达到一致点.

            // 对应的 follower 的 match点 向前推移.
            Long logNo = cxt.getFollowerMaxLogNo(msg.fromServer);
            logNo -= 1L;
            cxt.updateFollowerMaxLogNo(msg.fromServer, logNo);

        }
    }




    private void tryCommit(int follower, long lastLogNo) {

        // 检查是否达到commit标准.
        LeaderRuntimeContext cxt = (LeaderRuntimeContext) RuntimeContext.get();
        int half = ClusterConfig.CLUSTER_SIZE / 2 + 1;

        // 从后往前, 为该logNo添加确认者, 并判断该logNo是否达到commit标准. 达到则直接提交, 那么其之前的log也被自动提交.
        for (long logNo = lastLogNo; lastLogNo < cxt.getCommitLogNo(); lastLogNo--) {
            int confirmNumber = cxt.addConfirmFollower(logNo, follower);
            if (confirmNumber >= half) {
                // commit
                // todo 是否有其他细节
                cxt.setCommitLogNo(logNo);
                break;
            }
        }

    }

    public void onException(Event e, Throwable t) {

    }
}
