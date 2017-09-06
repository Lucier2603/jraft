package com.Lucifer2603.raft.net.msg;

import com.Lucifer2603.raft.conf.LocalConfig;
import com.Lucifer2603.raft.constants.MessageType;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.replicate.msg.AppendLogEntryRequest;
import com.Lucifer2603.raft.core.replicate.msg.AppendLogEntryResponse;
import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhangchen20
 */
public class MessageHandler {




    public static class Resolver {

        public static RaftMessage resolve(byte[] bytes) {


            String str = StringUtils.toEncodedString(bytes, Charset.forName("UTF-8"));

            RaftMessage msg = JSON.parseObject(str, RaftMessage.class);

            return msg;
        }
    }

    public static class Assembler {
        public static String assemble(RaftMessage msg) {
            return JSON.toJSONString(msg);
        }
    }


}
