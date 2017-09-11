package com.Lucifer2603.raft.core.Event;

/**
 * @author zhangchen20
 */
public interface EventCallback<E extends Event> {

    void preInvoke(E event);

    void onSuccess(E event);

    void onFail(E event, Throwable t);
}
