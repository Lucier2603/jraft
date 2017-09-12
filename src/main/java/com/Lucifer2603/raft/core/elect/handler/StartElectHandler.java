package com.Lucifer2603.raft.core.elect.handler;


import com.Lucifer2603.raft.conf.ClusterConfig;
import com.Lucifer2603.raft.conf.LocalConfig;
import com.Lucifer2603.raft.consistent.log.LogEntry;
import com.Lucifer2603.raft.constants.RoleType;
import com.Lucifer2603.raft.core.Event.Event;
import com.Lucifer2603.raft.core.EventHandler;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.elect.event.StartElectEvent;
import com.Lucifer2603.raft.core.elect.msg.VoteRequest;
import com.Lucifer2603.raft.net.msg.MessageBuilder;
import org.apache.commons.lang.math.RandomUtils;

/**
 * @author zhangchen20
 */
public class StartElectHandler implements EventHandler {

    // 开始一个新的选举
    public void process(Event e) {

        if (!(e instanceof StartElectEvent)) {
            return;
        }

        StartElectEvent event = (StartElectEvent) e;
        RuntimeContext cxt = RuntimeContext.get();

        // 提升当前role
        if (cxt.roleType != RoleType.Follower) {
            event.ends();
            return;
        }

        // 设置当前roleType
        cxt.roleType = RoleType.Candidate;
        cxt.currentTerm++;
        cxt.currentLeader = -1;

        // 关闭一切定时任务 阻塞关闭.
        // todo turnto suspend?
        cxt.timeJob.end();
        cxt.nextElectTimeout = RandomUtils.nextInt(50 * 1000) + System.currentTimeMillis();

        // 必须等所有都处理完毕后,再广播
        // 广播vote request
        LogEntry latestLog = cxt.logManager.getLatest();

        for (int serverNumber : ClusterConfig.SERVER_NOS) {
            if (serverNumber == LocalConfig.number) continue;

            VoteRequest request = MessageBuilder.buildVoteRequest(cxt);
            request.toServer = serverNumber;
            request.lastLogIndex = latestLog.logNo;
            request.lastLogTerm = latestLog.logTerm;

            cxt.netManager.saveMsg(request);
        }

        // 重启作为candidate的监听
        cxt.timeJob.restart(RoleType.Candidate);

    }

    public void onException(Event e, Throwable t) {

    }
}
