package com.Lucifer2603.raft.net;

import com.Lucifer2603.raft.conf.ClusterConfig;
import com.Lucifer2603.raft.conf.LocalConfig;
import com.Lucifer2603.raft.core.common.RuntimeContext;
import com.Lucifer2603.raft.core.elect.msg.PongMessage;
import com.Lucifer2603.raft.net.msg.HandlerMapping;
import com.Lucifer2603.raft.net.msg.MessageHandler;
import com.Lucifer2603.raft.net.msg.RaftMessage;
import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.ArrayUtils;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author zhangchen20
 */
public class NetManager {

    private static final int TIME_OUT = 3000;
    private static final int INTERVAL = 500;

    private static final int localNumber = LocalConfig.number;
    private static final int localPort = LocalConfig.serverPort;


    private Selector selector = null;
    private ServerSocketChannel listenChannel = null;
    private Map<Integer, SocketChannel> channels = new HashMap<>();


    public void init() {
        try {
            selector = Selector.open();

            // server启动
            listenChannel = ServerSocketChannel.open();

            listenChannel.socket().bind(new InetSocketAddress(localPort));
            listenChannel.configureBlocking(Boolean.FALSE);
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);


            // client启动
            //
            Thread.sleep(2000);

            // connect to other servers
            for (int n = 0; n < localNumber; n++) {
                SocketChannel channel = SocketChannel.open();
                channel.configureBlocking(Boolean.FALSE);
                channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                channel.socket().setReuseAddress(Boolean.TRUE);
                System.out.println(localNumber + "  " + localPort + " -> " + ClusterConfig.SERVER_PORTS[n]);
                channel.bind(new InetSocketAddress("127.0.0.1", localPort));
                channel.connect(new InetSocketAddress("127.0.0.1", ClusterConfig.SERVER_PORTS[n]));

//                SocketChannel channel = SocketChannel.open(new InetSocketAddress("127.0.0.1", ClusterConfig.SERVER_PORTS[n]));

                while (!channel.finishConnect());
                channels.put(n, channel);
            }

            System.out.println("init end! " + localNumber);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {

        // todo executor
        new Thread(() -> accept()).start();

    }

    private void accept() {

        while (true) {

            try {

                Thread.sleep(INTERVAL);

                if (selector.select(TIME_OUT) == 0) {
                    continue;
                }

                Iterator<SelectionKey> keyItr = selector.selectedKeys().iterator();

                while (keyItr.hasNext()) {

                    SelectionKey key = keyItr.next();

                    if (key.isAcceptable()) {

                        // 新建连接
                        SocketChannel clientChannel=((ServerSocketChannel)key.channel()).accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                        int remotePort = clientChannel.socket().getPort();
                        int remoteNumber = ArrayUtils.indexOf(ClusterConfig.SERVER_PORTS, remotePort);
                        channels.put(remoteNumber, clientChannel);

                        System.out.println(localNumber + " accept from " + remoteNumber);
                    }

                    // todo 需要做成异步,并且考虑多线程的锁.
                    if (key.isReadable()) {
                        System.out.println(localNumber + " isReadable! ");
                        SocketChannel channel = (SocketChannel) key.channel();

                        // todo 需要解决拆包和粘包问题
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int len = channel.read(buffer);
                        // todo no use? buffer.flip();
                        receive(buffer, len);
                    }

                    if (key.isWritable()) {
//                        System.out.println(localNumber + " isWritable! ");

                    }

                    keyItr.remove();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void receive(ByteBuffer buffer, int len) {
        try {
            RaftMessage msg = MessageHandler.Resolver.resolve(ArrayUtils.subarray(buffer.array(), 0, len));

            HandlerMapping.map(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(RaftMessage msg) {
        send(msg.toServer, JSON.toJSONString(msg));
    }

    public void send(Integer targetNumber, RaftMessage msg) {
        send(targetNumber, JSON.toJSONString(msg));
    }

    public void send(Integer targetNumber, String content) {
        try {

            SocketChannel channel = channels.get(targetNumber);

            if (channel == null) {
                System.out.println(localNumber + " null channel " + targetNumber);
            }

            channel.write(ByteBuffer.wrap(content.getBytes("UTF-8")));

            // todo 应该做一个缓冲区

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private Map<String, RaftMessage> msgHistory = new TreeMap<>();

    public String saveMsg(RaftMessage msg) {
        String key = msg.fromTerm + "|" + msg.msgId;
        msgHistory.replace(key, msg);

        return key;
    }

    public RaftMessage getMsg(int fromTerm, long msgId) {
        return msgHistory.get(fromTerm + "|" + msgId);
    }

    
    /**
     * ping & pong is Sync Operation. Invokers will wait until function returns or an exception is thrown out.
     */

    // ping will send a PingMessage, and receives a PongMessage.
    public PongMessage ping() {
        return null;
    }
    
    public PongMessage ping(long timeout) {
        throw new RuntimeException("Ping time out!");
    }

    // pong receives a PingMessage, and send a PongMessage.
    public void pong() {

    }

}
