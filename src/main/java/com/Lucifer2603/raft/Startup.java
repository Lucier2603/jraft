package com.Lucifer2603.raft;

/**
 * @author zhangchen20
 */
public class Startup {


    public static void main(String[] args) throws Exception {


//        List<NetManager> mgrs = new ArrayList<>();
//
//        for (int i = 0; i < 5; i++) {
//            LocalConfig.number = i;
//            LocalConfig.generateLocalConfig();
//
//            NetManager mgr = new NetManager();
//            mgrs.add(mgr);
//            mgr.init();
//            mgr.start();
//        }
//
//
//        Thread.sleep(10000);
//
//        for (NetManager mgr : mgrs) {
//            try {
//                RaftMessage msg = new RaftMessage();
//                msg.content = "hahaha";
//                msg.currentTerm = 1;
//                msg.fromNumber = 3;
//                msg.type = MessageType.HEART_BEAT_REQ;
//                mgr.send(0, JSON.toJSONString(msg));
//                System.out.println("send! " + mgr.localNumber);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
}
