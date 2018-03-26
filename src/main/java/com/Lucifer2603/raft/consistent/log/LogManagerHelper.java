package com.Lucifer2603.raft.consistent.log;

import java.util.HashMap;
import java.util.Map;

/**
 * 面向raft封装的logManger
 * @author zhangchen20
 */
public class LogManagerHelper {



    // 实际的日志存储器
    private LogManager logManager;







    // 每个term对应的最新最近logIndex
    private final Map<Long, TermLogIndexPair> newestTermLogIndexMap = new HashMap<>();

    public static class TermLogIndexPair {
        public Long term;
        public Long startLogNo;
        public Long endLogNo;
    }


    // append日志
    // 如果成功,返回true,否则返回false
    public void appendLogs(LogEntry[] entries, Long prevTerm, Long prevLogIndex) {
        // todo entries包含的term可能是跨term的

        TermLogIndexPair prevTermPair = newestTermLogIndexMap.get(prevTerm);
        if (prevTermPair != null && prevTermPair.startLogNo <= prevLogIndex && prevTermPair.endLogNo > prevLogIndex) {
            // 确认找到了对应的log, 可以更新本地日志
            // todo


        }

        // 如果没有找到这样一条日志,她的term和logNo, 与传入的 prevTerm, prevLogIndex相等, 那么就给leader返回false, 说明双方不一致
        // 于是, leader就会回退且再次发送appendLogRequest, 直到可以匹配上.
        throw new RuntimeException("");
    }







}
