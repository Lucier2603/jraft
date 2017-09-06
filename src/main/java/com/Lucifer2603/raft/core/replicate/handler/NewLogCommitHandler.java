package com.Lucifer2603.raft.core.replicate.handler;

import com.baidu.fpd.raft.core.Event.Event;
import com.baidu.fpd.raft.core.EventHandler;
import com.baidu.fpd.raft.core.common.RuntimeContext;
import com.baidu.fpd.raft.core.replicate.event.NewLogCommitEvent;

/**
 * @author zhangchen20
 */
public class NewLogCommitHandler implements EventHandler {

    public void process(Event e) {

        if (!(e instanceof NewLogCommitEvent)) {
            return;
        }

        NewLogCommitEvent event = (NewLogCommitEvent) e;
        RuntimeContext cxt = RuntimeContext.get();


    }
}
