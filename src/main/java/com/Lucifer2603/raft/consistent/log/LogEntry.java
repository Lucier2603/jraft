package com.Lucifer2603.raft.consistent.log;

/**
 * @author zhangchen20
 */
public class LogEntry {

    public int logTerm;

    // todo 每个term,从头开始计算
    public int logNo;

    public String content;

    /**
     * 状态
     * 1. prepare
     * 2. committed
     * 3. applied
     */
    public int status;



    public void commit() {
        // todo 写入文件
    }
}
