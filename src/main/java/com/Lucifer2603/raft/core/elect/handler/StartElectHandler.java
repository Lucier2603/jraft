package com.Lucifer2603.raft.core.elect.handler;


import com.Lucifer2603.raft.conf.ClusterConfig;
import com.Lucifer2603.raft.conf.LocalConfig;
import com.Lucifer2603.raft.consistent.log.LogEntry;
import com.Lucifer2603.raft.constants.RoleType;
import com.Lucifer2603.raft.core.DefaultEventHandler;
import com.Lucifer2603.raft.core.Event.Event;
import com.Lucifer2603.raft.core.EventHandler;
import com.Lucifer2603.raft.core.common.CandidateRuntimeContext;
import com.Lucifer2603.raft.core.common.FollowerRuntimeContext;
import com.Lucifer2603.raft.core.common.LeaderRuntimeContext;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.elect.event.StartElectEvent;
import com.Lucifer2603.raft.core.elect.msg.VoteRequest;
import com.Lucifer2603.raft.net.msg.MessageBuilder;
import org.apache.commons.lang.math.RandomUtils;

/**
 * @author zhangchen20
 */
public class StartElectHandler extends DefaultEventHandler {


    // 开始一个新的选举
    @Override
    public void process(Event e) {

        StartElectEvent event = (StartElectEvent) e;
        RuntimeContext preCxt = RuntimeContext.get();

        // 提升当前role
        if (preCxt.roleType != RoleType.Follower) {
            return;
        }

        // 设置当前roleType
        preCxt.roleType = RoleType.Candidate;
        preCxt.currentTerm++;
        preCxt.currentLeader = -1;

        // 重新获取RunteimContext
        CandidateRuntimeContext cxt = (CandidateRuntimeContext) RuntimeContext.get();

        // 关闭一切定时任务 阻塞关闭.
        // todo 需要等所有正在运行的任务做完.
        // todo 换成 suspend
        cxt.timeJob.end();
        cxt.nextElectTimeout = RandomUtils.nextInt(50 * 1000) + System.currentTimeMillis();

        // 必须等所有都处理完毕后,再广播
        // 广播vote request
        // todo 如果这里被伪造,是不是会有问题? 因此,raft应该是防不住拜占庭攻击的吧...
        LogEntry latestLog = cxt.logManager.getNewestLog();

        for (int serverNumber : ClusterConfig.SERVER_NOS) {
            if (serverNumber == LocalConfig.number) continue;

            VoteRequest request = MessageBuilder.buildVoteRequest(cxt);
            request.toServer = serverNumber;
            request.lastLogIndex = latestLog.logNo;
            request.lastLogTerm = latestLog.logTerm;

            // 广播
            cxt.netManager.saveMsg(request);
        }

        // 重启作为candidate的监听
        cxt.timeJob.restart(RoleType.Candidate);

    }

    public void onException(Event e, Throwable t) {

    }
}
