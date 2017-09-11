package com.Lucifer2603.raft.core.Event;


import com.Lucifer2603.raft.net.msg.RaftMessage;

/**
 * @author zhangchen20
 */
public abstract class Event {

    public RaftMessage raftMessage;

    private boolean endsupFlag;



    public void ends() {
        endsupFlag = true;
    }

    public void pass() {
        endsupFlag = false;
    }

    public boolean shouldEnd() {
        return endsupFlag;
    }

    // todo 覆盖此方法,解决如下问题: 1. 在Event类中增加static成员变量name,则不太容易加入继承体系. 2. 如果不用static变量,那么每个static
    // todo 实例都要保存一个name变量. 非static的getName方法可以很好的解决这个问题.
    public abstract String getName();
}
