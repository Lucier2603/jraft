package com.Lucifer2603.raft.utils.buffer;

/**
 * @author zhangchen20
 */
public class ByteBufferBlock {

    ByteBufferPool.InnerByteBuffer buffers;

    private int head;
    private int end;

    private int readIndex;
    private int writeIndex;


    // get() and put() will not move position
    public byte get(int index) {
        return buffers.get(index);
    }

    public void get(byte[] dest, int index) {
        buffers.get(dest, index);
    }

    public void put(int index, byte b) {
        buffers.put(index, b);
    }

    public void put(byte[] src, int index) {
        buffers.put(src, index);
    }

    // read() and write() will move position
    // todo read and write

}
