package com.Lucifer2603.raft.utils;

import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author zhangchen20
 */
public class ByteBufferPool {

    public static final int BUFFER_SIZE = 256;

    /**
     * use -> use -> use -> empty -> empty
     */
    private static Deque<ByteBufferBlock> pool;

    public static int capacity;

    static {
        capacity = 1000;
        pool = new ConcurrentLinkedDeque<>();

        for (int i = 0; i < 1000; i++) {
            pool.add(new ByteBufferBlock());
        }
    }


    public static ByteBuffer allocate() {
        // todo
        return null;
    }


    public static class ByteBufferBlock {
        public ByteBuffer buffer;
        public Boolean isOccupied;

        public ByteBufferBlock() {
            this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            this.isOccupied = false;
        }
    }
}
