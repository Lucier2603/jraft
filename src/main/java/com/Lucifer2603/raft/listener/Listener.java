package com.Lucifer2603.raft.listener;

/**
 * Listener
 *
 * @author zhangchen20
 */
public interface Listener {

    boolean skip();

    void invoke();
}
