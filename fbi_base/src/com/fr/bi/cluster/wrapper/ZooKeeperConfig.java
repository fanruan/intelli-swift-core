package com.fr.bi.cluster.wrapper;

/**
 * Created by FineSoft on 2015/5/7.
 */
public class ZooKeeperConfig {
    /**
     * comma separated host:port pairs, each corresponding to a zk
     * server. e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002" If
     * the optional chroot suffix is usable the example would look
     * like: "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002/app/a"
     * where the client would be rooted at "/app/a" and all paths
     * would be relative to this root - ie getting/setting/etc...
     * "/foo/bar" would result in operations being run on
     * "/app/a/foo/bar" (from the server perspective).
     */
    private String connectString;
    private int tickTime;


    public int getTickTime() {
        return tickTime;
    }

    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }
}