package com.Lucifer2603.raft.core.replicate.handler;

import com.baidu.fpd.raft.core.Event.Event;
import com.baidu.fpd.raft.core.EventHandler;
import com.baidu.fpd.raft.core.common.RuntimeContext;
import com.baidu.fpd.raft.core.replicate.event.PreCheckEvent;
import com.baidu.fpd.raft.socket.msg.RaftMessage;

/**
 * @author zhangchen20
 */
public class PreCheckHandler implements EventHandler {

    public void process(Event e) {

        if (!(e instanceof PreCheckEvent)) {
            return;
        }

        PreCheckEvent event = (PreCheckEvent) e;

        RaftMessage rcvMsg = event.rcvMsg;
        RuntimeContext cxt = RuntimeContext.get();

        // 比较currentTerm
        int msgTerm = rcvMsg.currentTerm;
        int currentTerm = cxt.currentTerm;

        // 说明自己的term太旧,需要更新
        if (msgTerm > currentTerm) {

            // todo
            e.ends();
        }
        else if (msgTerm < currentTerm) {
            // 说明是网络延迟原因,造成收到过期的消息.
            // 或者当前Leader问题?
            // todo
        }
    }
}
