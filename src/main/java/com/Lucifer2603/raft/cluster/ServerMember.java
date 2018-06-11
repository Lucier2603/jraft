package com.Lucifer2603.raft.cluster;

import com.Lucifer2603.raft.listener.Listener;

/**
 * ServerMember
 *
 * @author zhangchen20
 */
public class ServerMember implements Member {

    private int id;
    private Address address;
    private Role role;
    private Status status;

    private Cluster cluster;

    private Listener roleChanageListeners;
    private Listener newEventListeners;

    /**
     * return id
     */
    public int id() {
        return id;
    }

    /**
     * return address
     */
    public Address address() {
        return address;
    }

    /**
     * return status
     */
    public Status status() {
        return status;
    }

    /**
     * return role
     */
    public Role role() {
        return role;
    }

}
