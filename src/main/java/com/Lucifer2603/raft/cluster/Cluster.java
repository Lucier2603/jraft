package com.Lucifer2603.raft.cluster;

import java.util.Collection;

/**
 * 集群配置
 *
 * @author zhangchen20
 */
public interface Cluster {

    /**
     * return current leader
     */
    Member leader();

    /**
     * return current term
     */
    long term();

    /**
     * return local member
     */
    Member member();

    /**
     * return member by id
     */
    Member member(int id);

    /**
     * return all members registerd
     */
    Collection<Member> members();
}
