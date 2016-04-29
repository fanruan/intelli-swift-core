package com.fr.bi.cluster.zookeeper;

import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

/**
 * Created by Connery on 2015/3/10.
 * BI的Watcher的抽象类，主要作用是，生成当前自身监听路径的持久化节点，
 * 自动注册监听一个路径。
 * 如果需要一个短暂的节点，需要重写生成路径。
 * 对象实例化后，并且调用init方法，才能正常工作。
 */
public abstract class BIWatcher implements Watcher {
    protected ZooKeeperWrapper zk;
    private boolean isWatcherRegistered;

    protected BIWatcher() {
        isWatcherRegistered = false;
    }


    public void setWatcherRegistered(boolean value) {
        this.isWatcherRegistered = value;
    }

    @Override
    /**
     * 实现zookeeper调用的接口。
     */
    public void process(WatchedEvent event) {
//        System.out.println("回调watcher实例： 路径" + event.getPath() + " 类型："
//                + event.getType());

        if (ComparatorUtils.equals(getFocusedEventPath(), event.getPath())) {
            eventProcessor(event);
        }
        watch();
    }

    /**
     * 获得需要注册的节点路径
     *
     * @return
     */
    public abstract String getFocusedEventPath();

    /**
     * 注册关注的节点
     *
     * @return
     */
    public void watch() {
        try {
            zk.exists(getFocusedEventPath(), this);
        } catch (KeeperException e) {
                    BILogger.getLogger().error(e.getMessage(), e);
        } catch (InterruptedException e) {
                    BILogger.getLogger().error(e.getMessage(), e);
        } catch (Exception ex) {
             BILogger.getLogger().error(ex.getMessage(), ex);
        }
    }


    public void init(ZooKeeperWrapper zk) throws KeeperException, InterruptedException, Exception {
        this.zk = zk;
        ensureNodePathExists();
        watch();
    }

    public abstract void eventProcessor(WatchedEvent event);

    /**
     * 确保路径存在
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void ensureNodePathExists() throws KeeperException, InterruptedException, Exception {
        String path = getFocusedEventPath();
        if (path != null || !path.isEmpty()) {
            if (path.charAt(0) == '/') {
                path = path.substring(1, path.length());
                if (path.charAt(path.length() - 1) == '/') {
                    path = path.substring(0, path.length() - 1);
                }
                String[] foldNames = path.split("/");
                String childPath = "/";
                for (int i = 0; i < foldNames.length; i++) {
                    childPath += foldNames[i];
                    ensurePathExists(childPath);
                    childPath += "/";
                }
            }
        }
    }

    private void ensurePathExists(String path) throws KeeperException, InterruptedException, Exception {
        Stat s = zk.exists(path, this);
        if (s == null) {
            zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public void rewriteData(byte[] data, int version) throws KeeperException, InterruptedException, Exception {
        zk.setData(getFocusedEventPath(), data, version);
    }

}