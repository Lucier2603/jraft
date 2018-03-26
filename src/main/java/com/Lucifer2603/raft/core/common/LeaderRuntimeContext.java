package com.Lucifer2603.raft.core.common;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 只保存ledaer信息
 *
 * @author zhangchen20
 */
public class LeaderRuntimeContext extends RuntimeContext {



    /**
     * commit 相关
     */
    // 有待确认的logNo
    private Map<Long, Set<Integer>> awaitLogNos;

    // 为对应logNo加上一个确认的follower, 并返回确认后的数量.
    public Integer addConfirmFollower(long logNo, int follower) {
        awaitLogNos.putIfAbsent(logNo, new HashSet<>());

        Set fs = awaitLogNos.get(logNo);
        fs.add(follower);

        return fs.size();
    }

    // 已经被确认commit的最大logNo
    private volatile Long commitLogNo;

    public Long getCommitLogNo() {
        return commitLogNo;
    }

    public void setCommitLogNo(Long commitLogNo) {
        this.commitLogNo = commitLogNo;
    }




    // 每个follower和其已保存的对应的最新logNo
    // 可能是不一致的. 初始化时, 就是leader自身的最大logNo.
    private Map<Integer, Long> followerLogMatcher;


    // 获取follower对应的最大logNo
    public Long getFollowerMaxLogNo(int follower) {
        return followerLogMatcher.get(follower);
    }

    // 向前回退follower对应的最大logNo
    public void updateFollowerMaxLogNo(int follower, long logNo) {
        followerLogMatcher.put(follower, logNo);
    }
}
