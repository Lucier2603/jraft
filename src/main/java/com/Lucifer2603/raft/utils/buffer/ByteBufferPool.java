package com.Lucifer2603.raft.utils.buffer;

import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author zhangchen20
 */
public class ByteBufferPool {


    public static final int TOTAL_SIZE = 8 * 256 * 1024;
    /**
     * DirectByteBuffer is package-private
     */
    static ByteBuffer buffer = ByteBuffer.allocateDirect(TOTAL_SIZE);

    // size = 16, 32, 64, 128, 256, 512, 1024, 2048.
    List<InnerByteBuffer[]> bufferMap = new ArrayList<>(8);

    // todo 使用bitmap记录occupied情况



    public void init() {
        // todo 分配
    }

    public ByteBufferBlock allocate(int size) {

        ByteBufferBlock block = new ByteBufferBlock();

        InnerByteBuffer buf = doAllocate(size);

        block.buffers = buf;

        return block;
    }

    /**
     * 使用递归的方法 找到所有符合的buf串
     *
     * Eg. 对于 size = 5000, will return
     * 2048 -> 2048 -> 512 -> 256 -> 128 -> 16
     */
    private InnerByteBuffer doAllocate(int size) {
        // find the largest size that is smaller than or equals required size
        int allocateSize = 2048;

        if (size == 0) return null;
        if (size <= 16) return findAvailable(16);

        while (allocateSize > size) {
            allocateSize /= 2;
        }

        InnerByteBuffer buf = findAvailable(allocateSize);

        if (buf == null) throw new RuntimeException("Not enough buffer!");

        buf.next = doAllocate(size - allocateSize);

        return buf;
    }

    private InnerByteBuffer findAvailable(int size) {
        // todo
        return null;
    }



    private byte get(int index) {
        return buffer.get(index);
    }

    private void get(byte[] dest, int index) {
        for (int i = 0; i < dest.length; i++) {
            dest[i] = buffer.get(index + i);
        }
    }

    private void put(int index, byte b) {
        buffer.put(index, b);
    }

    private void put(byte[] src, int index) {
        for (int i = 0; i < src.length; i++) {
            buffer.put(index + i, src[i]);
        }
    }




    public static class InnerByteBuffer {
        public int size;
        public int base;

        public boolean occupied;

        public InnerByteBuffer next;

        public InnerByteBuffer(int size, int base) {
            this.size = size;
            this.base = base;
            this.occupied = false;
        }

        // todo if this is full, move it to next
        public byte get(int index) {
            if (index <= size) return ByteBufferPool.buffer.get(base + index);
            return next.get(index - size);
        }

        public void get(byte[] dest, int index) {
            for (int i = 0; i < dest.length; i++) {
                dest[i] = ByteBufferPool.buffer.get(base + index + i);
            }
        }

        public void put(int index, byte b) {
            if (index <= size) ByteBufferPool.buffer.put(base + index, b);
            else ByteBufferPool.buffer.put(index - size, b);
        }

        public void put(byte[] src, int index) {
            for (int i = 0; i < src.length; i++) {
                ByteBufferPool. buffer.put(base + index + i, src[i]);
            }
        }

    }
}
