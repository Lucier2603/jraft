package com.Lucifer2603.raft.core.replicate.event;

import com.Lucifer2603.raft.core.Event.Event;

/**
 * @author zhangchen20
 */
public class HeartbeatBroadcastEvent extends Event {

    public String getName() {
        return "HeartbeatBroadcastEvent";
    }
}
