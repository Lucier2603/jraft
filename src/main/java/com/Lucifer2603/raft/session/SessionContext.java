package com.Lucifer2603.raft.session;

import com.Lucifer2603.raft.protocol.serializable.RaftSerializable;

/**
 * @author zhangchen20
 */
public class SessionContext {

    private ThreadLocal<RaftSerializable> messages;

    private static SessionContext sessionContext;

    public static SessionContext get() {
        if (sessionContext == null) {
            synchronized(sessionContext) {
                if (sessionContext == null) {
                    sessionContext = new SessionContext();
                }
            }
        }
        return sessionContext;
    }

    public RaftSerializable getMessage() {
        return messages.get();
    }

    public void setMessage(RaftSerializable msg) {
        messages.set(msg);
    }

    public void removeMessage() {
        messages.remove();
    }
}
