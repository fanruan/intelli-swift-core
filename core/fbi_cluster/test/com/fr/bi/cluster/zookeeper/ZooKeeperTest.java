package com.fr.bi.cluster.zookeeper;
import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.Ids;

import java.io.IOException;

public class ZooKeeperTest {
  
    private static final int TIME_OUT = 3000000;
    private static final String HOST = "localhost:2181";
    public static ZooKeeper zookeeper = null;
    public static void init() throws IOException {
        zookeeper =  new ZooKeeper(HOST, TIME_OUT, null);
    }
    public static void main(String[] args) throws Exception{

        ZooKeeperTest.init();

        System.out.println("=========创建节点===========");
        if(zookeeper.exists("/test2", new MyWatcher()) == null)
        {
            zookeeper.create("/test2", "znode1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
//        System.out.println("=============查看节点是否安装成功===============");
        System.out.println(zookeeper.getChildren("/test", false));
//
//        System.out.println("=========修改节点的数据==========");
//        String data = "zNode2";
//        zookeeper.setData("/test", data.getBytes(), -1);
//
//        System.out.println("========查看修改的节点是否成功=========");
//        System.out.println(new String(zookeeper.getData("/test", false, null)));
//
//        System.out.println("=======删除节点==========");
//        zookeeper.delete("/test", -1);
//
//        System.out.println("==========查看节点是否被删除============");
//        System.out.println("节点状态：" + zookeeper.exists("/test", false));
//
        Thread.currentThread().join();
        zookeeper.close();
    } 
}

class MyWatcher implements Watcher{

    @Override
    public void process(WatchedEvent event) {
        System.err.println("event "+event.getType()+" "+event.getPath());
        try {
            ZooKeeperTest.zookeeper.getChildren("/test",this);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}