package com.Lucifer2603.raft.core.elect.handler;


import com.Lucifer2603.raft.consistent.log.LogEntry;
import com.Lucifer2603.raft.consistent.log.LogManager;
import com.Lucifer2603.raft.core.DefaultEventHandler;
import com.Lucifer2603.raft.core.Event.Event;
import com.Lucifer2603.raft.core.EventHandler;
import com.Lucifer2603.raft.core.common.CandidateRuntimeContext;
import com.Lucifer2603.raft.core.common.FollowerRuntimeContext;
import com.Lucifer2603.raft.core.common.LeaderRuntimeContext;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.elect.event.VoteRequestEvent;
import com.Lucifer2603.raft.core.elect.msg.PongMessage;
import com.Lucifer2603.raft.core.elect.msg.VoteRequest;
import com.Lucifer2603.raft.core.elect.msg.VoteResponse;
import com.Lucifer2603.raft.net.msg.MessageBuilder;

/**
 * 接收到 vote请求 的
 * @author zhangchen20
 */
public class VoteRequestHandler extends DefaultEventHandler {

    @Override
    public boolean doCheckTerm(int remoteTerm, int localTerm, Event e, RuntimeContext cxt) {
        return false;
    }

    @Override
    public boolean checkEvent(Event e) {
        return e instanceof VoteRequestEvent;
    }

    @Override
    public void processAsLeader(Event e, LeaderRuntimeContext cxt) {

    }

    @Override
    public void processAsCandidate(Event e, CandidateRuntimeContext cxt) {
        // candidate 不会对其他vote投票, 除非那个vote与自己的term不同.
    }

    // 作为follower, 只需要投票即可. 但是一个follower只能对一个term投票.
    @Override
    public void processAsFollower(Event e, FollowerRuntimeContext cxt) {
        // 同步获得锁
        cxt.voteLock.lock();

        // todo ???
        // 是否存在这种情况, 有一个节点无法接收其他节点的消息, 于是自己不断处于candidate状态, 从而不断自增term, 并对外广播?
        // 这个时候应该如何处理?

    }

    public void process(Event e) {

        VoteRequestEvent event = (VoteRequestEvent) e;
        RuntimeContext cxt = RuntimeContext.get();
        VoteRequest request = (VoteRequest) event.raftMessage;


        // 先设置vote for.
        // use lock to pretent 2 candidates.
        cxt.lock();
        try {
            // 已经vote for
            // vote for 只会递增
            if (request.candidateTerm <= cxt.voteForFlag) {
                event.ends();
                return;
            }

            cxt.voteForFlag = request.candidateTerm;

        } finally {
            cxt.unlock();
        }


        // 对于收到的vote req,需要区分各种情况.

        // 1. vote term 小, 说明发起者与leader失去了一段时间的连接.
        // 2. vote term 大, 有可能是, 发起者与集群断开了一定时间,在这段时间内,发起者不停的 start new election. 从而term超越了集群.
        // 3. vote term 大, 也有可能是, 接受者自身晚了.

        // 不管怎么说,都需要结合日志进度来判断.

        // 判断term
        int candidateTerm = request.candidateTerm;
        int currentTerm = cxt.currentTerm;

        if (candidateTerm <= currentTerm) {
            // 说明 candidate 未及时更新,或者当前时间有延迟.
            reject(cxt, event);
            return;
        }

        // 由于保证了同一时间只有一个leader,因此ping leader将可以解决所有的情况

        // 对于 candidate,或者包括candidate在内的少数派,仅仅与leader失去了链接的情况,需要ping一下leader确认.
        // 此处需要设置同步 & timeout, 以防止确实是leader挂了的情况.

        long PING_TIMEOUT = 10 * 1000;
        boolean pingSuccessFlag;
        try {
            PongMessage pong = cxt.netManager.ping(PING_TIMEOUT);

            // if resolvePong() returns true. 说明本server和leader都是最新的,并且沟通良好.(不代表一定是集群的leader)
            pingSuccessFlag = resolvePong(pong);

        } catch (Exception ex) {
            pingSuccessFlag = false;
        }

        // reject if ping success
        if (pingSuccessFlag) {
            reject(cxt, event);
            return;
        }


        // 对于 candidate,或者包括candidate在内的多数派,与leader失去链接的情况. 那么ping leader将也是失败.此时比较双方日志.
        // 对于 leader短暂性的超时, 如果在leader恢复前, 多数派已经投票, 那么, 新leader的过程将不可逆.

        LogEntry localLastLog = cxt.logManager.getLatest();

        if (localLastLog.logTerm >= request.lastLogTerm && localLastLog.logNo > request.lastLogIndex) {
            reject(cxt, event);
        } else {
            accept(cxt, event);
        }

        event.pass();

    }

    public void reject(RuntimeContext cxt, VoteRequestEvent event) {

        VoteResponse response = MessageBuilder.buildVoteResponse(cxt);

        response.acceptFlag = false;
        response.replyTerm = cxt.currentTerm;

        event.pass();
    }

    public void accept(RuntimeContext cxt, VoteRequestEvent event) {

        VoteResponse response = MessageBuilder.buildVoteResponse(cxt);

        response.acceptFlag = true;
        response.replyTerm = cxt.currentTerm;

        event.pass();
    }

    public void onException(Event e, Throwable t) {

    }

    public boolean resolvePong(PongMessage msg) {
        if (msg.isLeader && msg.leaderTerm == RuntimeContext.get().currentTerm)
            return true;

        // 不用考虑currentTerm过旧的情况.
//        如果旧的leader发来commit信息，整个集群不会因此影响。
//        如果新的leader发来信息，那么会覆盖之前的。
        return false;
    }
}
