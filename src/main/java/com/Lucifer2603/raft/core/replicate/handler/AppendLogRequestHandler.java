package com.Lucifer2603.raft.core.replicate.handler;


/**
 * 收到 复制日志的 请求.
 *
 * @author zhangchen20
 */


import com.Lucifer2603.raft.consistent.log.LogManager;
import com.Lucifer2603.raft.consistent.log.LogManagerHelper;
import com.Lucifer2603.raft.constants.RoleType;
import com.Lucifer2603.raft.core.Event.Event;
import com.Lucifer2603.raft.core.Event.EventBuilder;
import com.Lucifer2603.raft.core.EventHandler;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.replicate.event.AppendLogRequestEvent;
import com.Lucifer2603.raft.core.replicate.msg.AppendLogEntryRequest;

/**
 * raft 日志同步的目的是 复制. 因此, follower收到的 entry[], 是对整个数组回复.
 */
public class AppendLogRequestHandler implements EventHandler {

    public void process(Event e) {

        if (!(e instanceof AppendLogRequestEvent)) {
            return;
        }

        //  request将被leader以心跳的形式发送.
        AppendLogRequestEvent event = (AppendLogRequestEvent) e;
        RuntimeContext cxt = RuntimeContext.get();
        AppendLogEntryRequest request = (AppendLogEntryRequest) event.raftMessage;

        // 检查role
        // leader收到,一般有2种情况: 收到的是之前的term,那么是延迟,或者旧leader还不知道当前情况,reject;收到的是之后的term,说明有新的leader当选了.
        if (cxt.roleType == RoleType.Leader) {

            if (cxt.currentTerm > request.fromTerm) {
                reject(event);
                return;
            }

            // will never happen
            if (cxt.currentTerm == request.fromTerm) ;

            if (cxt.currentTerm < request.fromTerm) {
                // leader 需要降级
                cxt.refresh();
//                cxt.lock();
//                try {
//                    cxt.currentLeader = 1; // todo 可以通过ping fromServer来getLeader
//                    cxt.currentTerm = request.fromTerm;
//                    cxt.electAcceptSet.clear();
//                    cxt.electRejectSet.clear();
//                    cxt.logManager.clear();
//                    cxt.lastHeartBeatTime = System.currentTimeMillis();
//                    cxt.roleType = RoleType.Follower;
//                } finally {
//                    cxt.unlock();
//                }
            }
        }
//        if (cxt.roleType != RoleType.Follower) {
//            event.ends();
//            return;
//        }

        // 检查Term
        int remoteTerm = event.raftMessage.fromTerm;
        int localTerm = cxt.currentTerm;

        // 如果remoteTerm过旧, 那么reject.
        // 发生情况为: 网络延迟.
        if (remoteTerm < localTerm) {
            reject(event);
            return;
        }

        // 如果localTerm过旧, 说明掉线过, 需要update.
        // update会暂停一切操作,并刷新所有数据
        if (remoteTerm > localTerm) {
            // 这里不应该使用 eventEngine. 因为是异步的.
//            cxt.eventEngine.publishEvent(EventBuilder.buildRefreshLocalContextEvent());
            // 应当使用同步的方法
            cxt.refresh();
            event.ends();
            return;
        }

        
        /**
         * 正常情况下, 从这里开始走
         */
        // 更新heartbeat timeout
        cxt.lastHeartBeatTime = System.currentTimeMillis();

        // 直接写入日志.如果成功则回复OK.否则reject.
        LogManagerHelper logMgr = cxt.logManager;
        AppendLogEntryRequest msg = (AppendLogEntryRequest) event.raftMessage;

        try {
            // 如果没有找到这样一条日志,她的term和logNo, 与传入的 prevTerm, prevLogIndex相等, 那么就给leader返回false, 说明双方不一致
            // 于是, leader就会回退且再次发送appendLogRequest, 直到可以匹配上.
            logMgr.appendLogs(msg.newEntries, msg.prevLogTerm, msg.prevLogIndex);
            success(event);

        } catch (Exception t) {

            reject(event);

        }


    }

    private void reject(AppendLogRequestEvent e) {

        e.successFlag = false;

        // todo getPrevMatch;

        // 不再继续流转
        e.pass();
    }

    private void success(AppendLogRequestEvent e) {

        e.successFlag = true;

        // 当成功时, prev就是当前值
        e.prevMatchTerm = RuntimeContext.get().currentTerm;
        e.prevMatchIndex =

        e.pass();
    }

    public void onException(Event e, Throwable t) {

    }
}
