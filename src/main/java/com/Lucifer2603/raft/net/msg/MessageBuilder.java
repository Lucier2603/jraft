package com.Lucifer2603.raft.net.msg;

import com.Lucifer2603.raft.conf.LocalConfig;
import com.Lucifer2603.raft.constants.MessageType;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.replicate.msg.AppendLogEntryRequest;
import com.Lucifer2603.raft.core.replicate.msg.AppendLogEntryResponse;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhangchen20
 */
public class MessageBuilder {

    private static AtomicLong idGenerator;



    private static void setBase(RuntimeContext cxt, RaftMessage msg) {
        msg.msgId = idGenerator.getAndIncrement();
        msg.fromTerm = cxt.currentTerm;
        msg.fromServer = LocalConfig.number;
    }


    public static AppendLogEntryRequest buildAppendLogEntryRequest(RuntimeContext cxt) {
        AppendLogEntryRequest msg = new AppendLogEntryRequest();
        setBase(cxt, msg);

        return msg;
    }

    public static AppendLogEntryResponse buildAppendLogEntryAcceptResponse(RuntimeContext cxt) {
        AppendLogEntryResponse msg = new AppendLogEntryResponse();
        setBase(cxt, msg);

        msg.success = Boolean.TRUE;
        msg.toServer = cxt.currentLeader;
        msg.msgId = idGenerator.getAndIncrement();
        msg.msgType = MessageType.APPEND_LOG_RESP;

        return msg;
    }

    public static AppendLogEntryResponse buildAppendLogEntryRejectResponse(RuntimeContext cxt) {
        AppendLogEntryResponse msg = new AppendLogEntryResponse();
        setBase(cxt, msg);

        msg.success = Boolean.FALSE;
        msg.toServer = cxt.currentLeader;
        msg.msgId = idGenerator.getAndIncrement();
        msg.msgType = MessageType.APPEND_LOG_RESP;

        return msg;
    }
}
