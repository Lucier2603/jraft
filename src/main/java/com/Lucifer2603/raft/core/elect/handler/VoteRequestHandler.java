package com.Lucifer2603.raft.core.elect.handler;

import com.baidu.fpd.raft.core.Event.Event;
import com.baidu.fpd.raft.core.EventHandler;
import com.baidu.fpd.raft.core.common.RuntimeContext;
import com.baidu.fpd.raft.core.elect.event.VoteRequestEvent;

/**
 * @author zhangchen20
 */
public class VoteRequestHandler implements EventHandler {

    public void process(Event e) {

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


    }
}
