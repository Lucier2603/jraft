package com.Lucifer2603.raft.net.msg;

import com.Lucifer2603.raft.constants.MessageType;
import com.Lucifer2603.raft.core.Event.Event;
import com.Lucifer2603.raft.core.Event.EventBuilder;
import com.Lucifer2603.raft.core.common.RuntimeContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author zhangchen20
 */
public class HandlerMapping {


    private static Map<MessageType, Function<RuntimeContext, Event>> typeMap;

    static {
        typeMap = new HashMap<>();
        typeMap.put(MessageType.HEART_BEAT_OK_RESP, EventBuilder::buildHeartBeatRespondingOKEvent);
    }

    public static void map(RaftMessage msg) {


    }
}
