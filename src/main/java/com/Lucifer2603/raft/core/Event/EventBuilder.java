package com.Lucifer2603.raft.core.Event;

import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.replicate.event.RefreshLocalContextEvent;

/**
 * @author zhangchen20
 */
public class EventBuilder {

    private RuntimeContext cxt;

    public static RefreshLocalContextEvent buildRefreshLocalContextEvent() {
        return new RefreshLocalContextEvent();
    }

}
