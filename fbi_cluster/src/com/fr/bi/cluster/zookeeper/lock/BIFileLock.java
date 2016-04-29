package com.fr.bi.cluster.zookeeper.lock;

import com.fr.bi.cluster.ClusterAdapter;
import com.fr.bi.cluster.exception.DistributedException;
import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.cluster.zookeeper.ZooKeeperConstant;
import com.fr.bi.stable.utils.code.BILogger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

import java.util.Iterator;

/**
 * Created by Connery on 2015/4/14.
 * 文件生成锁。
 * 生成文件的时候，只会有一个机器的一个线程获得指点路径的锁，没有获得锁的线程
 * 等待获得锁线程工作的完成。
 * 等到工作完成后，释放锁并通知其他等待的线程。
 * 等待的线程获得通知后，之间返回，
 */
public class BIFileLock {
    private final static String FILE_LOCK = "/file_lock";
    protected ZooKeeperWrapper zooKeeper;
    private String lock_path;
    private boolean isLocked = false;
    private String id;

    public BIFileLock(String lock_path) {
        this.lock_path = lock_path;
        try {
            init();

        } catch (Exception ex) {
            BILogger.getLogger().error(ex.getMessage(), ex);
        }
    }

    private String getPath() {
        return FILE_LOCK + "/" + lock_path;
    }

    private void init() throws Exception {
        if (getZookeeper().exists(FILE_LOCK, false) == null) {
            getZookeeper().create(FILE_LOCK, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public ZooKeeperWrapper getZookeeper() {
        if (zooKeeper == null) {
            zooKeeper = ClusterAdapter.getManager().getZooKeeperManager().getZooKeeper();
        }
        return zooKeeper;
    }

    public void lock(BIFileLockWatcher watcher) throws DistributedException {

        WriteLock writeLock = new WriteLock(getZookeeper().getZookeeperHandler().getZooKeeper(), getPath(), ZooKeeperConstant.DEFAULT_ACL);

        try {
            isLocked = writeLock.lock();
            id = writeLock.getId();
            registerWatcher(watcher);
//            System.out.println("注册文件锁的监听");

        } catch (InterruptedException ex) {
            BILogger.getLogger().error(ex.getMessage(), ex);
        } catch (KeeperException ex) {
            BILogger.getLogger().error(ex.getMessage(), ex);
        }

    }

    public boolean isLocked() {
        return isLocked;
    }

    public void unlock() throws DistributedException {
        if (isLocked) {
            isLocked = false;
            deleteNode();
            try {
                Iterator<String> iterator = getZookeeper().getChildren(getPath(), false).iterator();
                while (iterator.hasNext()) {
                    String childNode = iterator.next();
//                    System.out.println("释放其他");
//                    getZookeeper().setData(getPath() + "/" + childNode, "1".getBytes(), -1);
                    getZookeeper().delete(getPath() + "/" + childNode, -1);
                }
            } catch (Exception ex) {
                BILogger.getLogger().error(ex.getMessage(), ex);
            }
        }
    }

    private void deleteNode() {
        try {
            getZookeeper().delete(id, -1);
        } catch (Exception ex) {
            BILogger.getLogger().error(ex.getMessage(), ex);
        }
    }

    private void registerWatcher(BIFileLockWatcher watcher) {
        try {
//            System.out.println("注册监听");
            getZookeeper().exists(id, watcher);
        } catch (Exception ex) {
            BILogger.getLogger().error(ex.getMessage(), ex);
        }

    }

    /**
     * 如果没有获得锁，需要等待获得锁的线程将任务进行完。
     * 在等待前，需要检查是否任务已经
     *
     * @return
     */
    public boolean checkExists() throws Exception {
        if (!isLocked) {
//            System.out.println("检查路径");
            return getZookeeper().exists(id, false) != null;
        } else {
            return false;
        }
    }
}