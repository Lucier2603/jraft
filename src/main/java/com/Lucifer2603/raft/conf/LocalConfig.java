package com.Lucifer2603.raft.conf;

/**
 * @author zhangchen20
 */
public class LocalConfig {

    public static int number;



    public static String host = "127.0.0.1";
    public static int serverPort;

    public static void generateLocalConfig() {
        serverPort = ClusterConfig.SERVER_PORTS[number];
    }
}
