package com.Lucifer2603.raft.core.elect.handler;


import com.Lucifer2603.raft.consistent.log.LogEntry;
import com.Lucifer2603.raft.consistent.log.LogManager;
import com.Lucifer2603.raft.core.Event.Event;
import com.Lucifer2603.raft.core.EventHandler;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.elect.event.VoteRequestEvent;
import com.Lucifer2603.raft.core.elect.msg.VoteRequest;
import com.Lucifer2603.raft.core.elect.msg.VoteResponse;
import com.Lucifer2603.raft.net.msg.MessageBuilder;

/**
 * @author zhangchen20
 */
public class VoteRequestHandler implements EventHandler {

    public void process(Event e) {

        // todo 只会accept一次

        if (!(e instanceof VoteRequestEvent)) {
            return;
        }

        VoteRequestEvent event = (VoteRequestEvent) e;
        RuntimeContext cxt = RuntimeContext.get();

        // 对于收到的vote req,需要区分各种情况.

        // 1. vote term 小, 说明发起者与leader失去了一段时间的连接.
        // 2. vote term 大, 有可能是, 发起者与集群断开了一定时间,在这段时间内,发起者不停的 start new election. 从而term超越了集群.
        // 3. vote term 大, 也有可能是, 接受者自身晚了.

        // 不管怎么说,都需要结合日志进度来判断.

        // todo 如果candidate仅仅与leader失去了链接,那么


        VoteRequest request = (VoteRequest) event.raftMessage;

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
        // todo


        // 对于 candidate,或者包括candidate在内的多数派,与leader失去链接的情况. 那么ping leader将也是失败.此时比较双方日志.
        // 对于 leader短暂性的超时, 如果在leader恢复前, 多数派已经投票, 那么, 新leader的过程将不可逆.

        LogEntry localLastLog = cxt.logManager.getLatest();

        if (localLastLog.logTerm >= request.lastLogTerm && localLastLog.logNo > request.lastLogIndex) {
            reject(cxt, event);
        } else {
            accept(cxt, event);
        }


    }

    public void reject(RuntimeContext cxt, VoteRequestEvent event) {

        VoteResponse response = MessageBuilder.buildVoteResponse(cxt);

        response.acceptFlag = false;
        response.replyTerm = cxt.currentTerm;

        event.ends();
    }

    public void accept(RuntimeContext cxt, VoteRequestEvent event) {

        VoteResponse response = MessageBuilder.buildVoteResponse(cxt);

        response.acceptFlag = true;
        response.replyTerm = cxt.currentTerm;

        event.ends();
    }
}
