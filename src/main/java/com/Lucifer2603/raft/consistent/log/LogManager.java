package com.Lucifer2603.raft.consistent.log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhangchen20
 */
public class LogManager {

    // todo 目前只是单线程

    private LogEntry[] logRecords = new LogEntry[1000];

    // term -> 该term的第一个log,在List中的index
    private Map<Integer, Integer> termIndexMap = new HashMap<>();

    // 最大已提交
    // 一个已提交的log,保证之前的全部都被提交了.
    public int commitIndex;

    // 最大已执行
    private int lastApplied;

    // todo
    private ReentrantLock lock;




    // 如果append成功,那么说明follower已经做好了接受的准备,可以回复success.
    public void append(LogEntry[] newEntries, int prevTerm, int prevIndex) {

        // 查找prev log
        int startIdx = findByTermAndNo(prevTerm, prevIndex);

        // 找到后,直接替换
        if (startIdx + newEntries.length >= logRecords.length) {
            resize();
        }

        System.arraycopy(newEntries, 0, logRecords, startIdx + 1, newEntries.length);

    }

    public int findByTermAndNo(int term, int no) {

        int termIdx = termIndexMap.get(term);

        int startIdx = termIdx;
        for (;logRecords[startIdx].logNo == no && logRecords[startIdx].logTerm == no;startIdx++) {
            // 当找不到时,startIdx会数组越界,或nullException
        }

        return startIdx;
    }

    public LogEntry findByPosition(int pos) {
        return logRecords[pos];
    }

    public LogEntry updateLogEntryStatus(int pos, int status) {
        logRecords[pos].status = status;

        return logRecords[pos];
    }

    // returns POSITIVE if a is latest than b. NEGTIVE if a is smaller. ZERO if equals.
    // todo this method should move to Utils.
    public static int compare(LogEntry a, LogEntry b) {

        // todo 可以简化
        if (a.logTerm != b.logTerm)
            return a.logTerm - b.logTerm;

        return a.logNo - b.logNo;
    }


//    private void add(LogEntry entry) {
//
//        if (entry.logNo > logConsequence.size()) {
//            // todo 扩容算法 是否仅在resize时候需要lock
//            resize(largestConsecutive * 2);
//        }
//
//        logConsequence.set(getIndexByLogNo(entry.logNo), entry);
//
//        // 刷新
//        for (; logConsequence.get(largestConsecutive + 1) != null; largestConsecutive++);
//
//        // todo largestRecorded
//
//    }


    private void resize() {
        int len = (int) (1.3 * (double) logRecords.length);

        LogEntry[] newRecords = new LogEntry[len];

        System.arraycopy(logRecords, 0, newRecords, 0, logRecords.length);

        logRecords = newRecords;
    }


    public void rebuild() {

    }

    private int getIndexByLogNo(int logNo) {
        return logNo;
    }



    /**
     * Leader 专用
     */
    // followerNumber -> 最大已replicate的index    (nextIndex + 1)
    private Map<Integer, Integer> replicatedMap = new HashMap<>();

    // 得到回复的log  position -> Set<followerNumber>
    private Map<Integer, Set<Integer>> prepareLogMap = new HashMap<>();

    private int leaderCommittedIndex;

    // 最大有效记录数
    private int position;


    public LogEntry[] findFollowerProgress(int followerNumber) {

        int replicatedIdx = replicatedMap.get(followerNumber);

        LogEntry[] content = new LogEntry[position - replicatedIdx];
        System.arraycopy(logRecords, replicatedIdx + 1, content, 0, content.length);

        return content;
    }

    public int findFollowerPosition(int followerNumber) {
        return replicatedMap.get(followerNumber);
    }

    public void updateProgress(int followerNumber, int newPosition) {
        int oldPosition = replicatedMap.get(followerNumber);

        replicatedMap.replace(followerNumber, newPosition);

        // 为每个logPosition加上该follower 已经accept.
        for (++oldPosition; oldPosition <= newPosition; oldPosition++) {
            prepareLogMap.get(oldPosition).add(followerNumber);
        }
    }

    public void decrement(int followerNumber) {
        int current = replicatedMap.get(followerNumber);
        replicatedMap.replace(followerNumber, current - 1);
    }


    public void append(LogEntry entry) {
        logRecords[++position] = entry;
    }

    public LogEntry getLatest() {
        return logRecords[position];
    }

    public Map<Integer, Set<Integer>> getPrepareLogMap() {
        return prepareLogMap;
    }
}
