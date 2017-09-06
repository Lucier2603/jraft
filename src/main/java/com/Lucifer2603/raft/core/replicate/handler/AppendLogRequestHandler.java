package com.Lucifer2603.raft.core.replicate.handler;


/**
 * @author zhangchen20
 *
 *
 */


import com.Lucifer2603.raft.consistent.log.LogManager;
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

        // todo request将被leader以心跳的形式发送.
        AppendLogRequestEvent event = (AppendLogRequestEvent) e;
        RuntimeContext cxt = RuntimeContext.get();

        // 检查role
        if (cxt.roleType != RoleType.Follower) {
            event.ends();
            return;
        }

        // 检查Term
        int remoteTerm = event.raftMessage.fromTerm;
        int localTerm = cxt.currentTerm;

        // 如果remoteTerm过旧, 那么reject.
        // todo 发生情况为: 网络延迟.
        if (remoteTerm < localTerm) {
            reject(event);
            return;
        }

        // 如果localTerm过旧,那么update.
        // update会暂停一切操作,并刷新所有数据
        if (remoteTerm > localTerm) {
            cxt.eventEngine.publishEvent(EventBuilder.buildRefreshLocalContextEvent());
            event.ends();
            return;
        }


        // 直接写入日志.如果成功则回复OK.否则reject.
        LogManager logMgr = cxt.logManager;
        AppendLogEntryRequest msg = (AppendLogEntryRequest) event.raftMessage;

        try {
            logMgr.append(msg.newEntries, msg.prevLogTerm, msg.prevLogIndex);
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

        // todo getPrevMatch;

        e.pass();
    }
}
