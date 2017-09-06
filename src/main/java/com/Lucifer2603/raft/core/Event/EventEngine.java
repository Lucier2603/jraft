package com.Lucifer2603.raft.core.Event;

/**
 * @author zhangchen20
 */
public interface EventEngine {

    void publishEvent(Event e);
}
