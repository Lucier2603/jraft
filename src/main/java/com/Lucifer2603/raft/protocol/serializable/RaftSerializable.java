package com.Lucifer2603.raft.protocol.serializable;

import java.io.Serializable;

/**
 * @author zhangchen20
 */
public interface RaftSerializable extends Serializable {

    String getType();
}
