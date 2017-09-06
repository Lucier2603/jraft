package com.Lucifer2603.raft.core.Event;

import com.baidu.fpd.raft.core.EventHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangchen20
 */
public class BaseEventEngine implements EventEngine {


    private Map<Event, EventHandler[]> eventHandlers = new HashMap<>();


    private static BaseEventEngine engine;

    public static BaseEventEngine get() {
        return engine;
    }

    public void publishEvent(Event e) {
        // 按照event类型分配
    }

    public void registerEventHandler(Event e, EventHandler[] hs) {
        eventHandlers.put(e, hs);
    }
}
