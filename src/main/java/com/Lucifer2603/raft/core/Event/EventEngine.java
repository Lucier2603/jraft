package com.Lucifer2603.raft.core.Event;

/**
 * @author zhangchen20
 */
public interface EventEngine {

    void publishEventAsync(Event e, EventCallback callback);

    void publishEvent(Event e);
}
