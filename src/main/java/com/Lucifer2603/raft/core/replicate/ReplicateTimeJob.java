package com.Lucifer2603.raft.core.replicate;

import com.Lucifer2603.raft.conf.LocalConfig;
import com.Lucifer2603.raft.constants.RoleType;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.elect.event.StartElectEvent;
import com.Lucifer2603.raft.core.replicate.event.HeartbeatBroadcastEvent;
import org.apache.commons.lang3.RandomUtils;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangchen20
 */
public class ReplicateTimeJob {


    private ScheduledExecutorService executor;

    // 保证各机器的timetou不会冲突
    private static final long HEARTBEAT_TIMEOUT = RandomUtils.nextInt(LocalConfig.number * 10, LocalConfig.number * 10 + 10) * 1000;


    public void init() {
        executor = Executors.newScheduledThreadPool(2);
    }


    public void start() {

        // leader定时发布心跳信息
        if (RuntimeContext.get().roleType == RoleType.Leader) {
            executor.scheduleAtFixedRate(new Runnable() {
                                             @Override
                                             public void run() {

                                                 // 作为leader 定时发布心跳信息
                                                 RuntimeContext.get().eventEngine.publishEvent(new HeartbeatBroadcastEvent());
                                             }
                                         },
                    1000, 500, TimeUnit.MILLISECONDS);
        }

        // follower定期检查heartbeat
        if (RuntimeContext.get().roleType == RoleType.Follower) {
            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {

                    RuntimeContext cxt = RuntimeContext.get();

                    if (Math.abs(System.currentTimeMillis() - cxt.lastHeartBeatTime) > HEARTBEAT_TIMEOUT) {
                        cxt.eventEngine.publishEvent(new StartElectEvent());
                    }
                }
            },
            10 * 1000, 500, TimeUnit.MILLISECONDS);
        }
    }


    // todo 是否可以这样做
    public void endAsyn() {
        executor.shutdown();
    }

    public void end() {
        executor.shutdown();

        while (!executor.isTerminated());
    }

}
