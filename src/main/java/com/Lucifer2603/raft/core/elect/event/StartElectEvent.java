package com.Lucifer2603.raft.core.elect.event;


import com.Lucifer2603.raft.core.Event.Event;

/**
 * @author zhangchen20
 */
public class StartElectEvent extends Event {

    public String getName() {
        return "StartElectEvent";
    }
}
