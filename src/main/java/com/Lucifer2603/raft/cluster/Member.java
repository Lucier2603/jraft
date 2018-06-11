package com.Lucifer2603.raft.cluster;

/**
 * 单节点配置
 *
 * @author zhangchen20
 */
public interface Member {

    enum Role {
        LEADER,
        FOLLOWER,
        CANDIDATE;
    }

    enum Status {
        ONLINE,
        OFFLINE;
    }

    /**
     * return id
     */
    int id();

    /**
     * return address
     */
    Address address();
    
    /**
     * return status
     */
    Status status();
    
    /**
     * return role
     */
    Role role();

}
