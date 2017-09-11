package com.Lucifer2603.raft.core;

import com.Lucifer2603.raft.core.Event.Event;

/**
 * @author zhangchen20
 */
public interface EventHandler {

    void process(Event e);

    void onException(Event e, Throwable t);
}
