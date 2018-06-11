package com.Lucifer2603.raft.cluster;

import java.net.InetSocketAddress;

/**
 * Address
 *
 * @author zhangchen20
 */
public class Address {

    private String host;
    private int port;
    private InetSocketAddress address;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}
