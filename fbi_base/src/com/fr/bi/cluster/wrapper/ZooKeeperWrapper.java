package com.fr.bi.cluster.wrapper;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.cluster.retry.RetryLoop;
import com.fr.bi.cluster.retry.RetryPolicy;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by FineSoft on 2015/5/7.
 */
public class ZooKeeperWrapper {
    private RetryLoop retryLoop = new RetryLoop();
    private ZooKeeperHandler zookeeperHandler;

    public ZooKeeperWrapper(ZooKeeperHandler zookeeperHandler) {
        this.zookeeperHandler = zookeeperHandler;

    }

    public ZooKeeperHandler getZookeeperHandler() {
        return zookeeperHandler;
    }

    public void initial(RetryPolicy biRetryPolicy) {
        retryLoop.initial(biRetryPolicy, this);
    }

    public RetryLoop getRetryLoop() {
        return retryLoop;
    }

    public void create(final String path, final byte[] content, final CreateMode mode) throws Exception {
        RetryLoop.retry(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return zookeeperHandler.getZooKeeper().create(path, content, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
            }
        }, this);
    }

    public String create(final String path, final byte[] content, final List<ACL> acl, final CreateMode mode) throws Exception {

        return RetryLoop.retry(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return zookeeperHandler.getZooKeeper().create(path, content, acl, mode);
            }
        }, this);
    }

    public void delete(final String path, final int version) throws Exception {
        RetryLoop.retry(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                zookeeperHandler.getZooKeeper().delete(path, version);
                return null;
            }
        }, this);
    }

    public List<String> getChildren(final String path, final Boolean isWatcher) throws Exception {

        return RetryLoop.retry(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return zookeeperHandler.getZooKeeper().getChildren(path, isWatcher);
            }
        }, this);
    }

    public List<String> getChildren(final String path, final Watcher watcher) throws Exception {

        return RetryLoop.retry(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return zookeeperHandler.getZooKeeper().getChildren(path, watcher);
            }
        }, this);
    }

    public Stat exists(final String path, final Boolean isWatcher) throws Exception {

        return RetryLoop.retry(new Callable<Stat>() {
            @Override
            public Stat call() throws Exception {
                return zookeeperHandler.getZooKeeper().exists(path, isWatcher);
            }
        }, this);
    }

    public Stat exists(final String path, final Watcher watcher) throws Exception {

        return RetryLoop.retry(new Callable<Stat>() {
            @Override
            public Stat call() throws Exception {
                return zookeeperHandler.getZooKeeper().exists(path, watcher);
            }
        }, this);
    }

    public Stat setData(final String path, final byte data[], final int version) throws Exception {

        return RetryLoop.retry(new Callable<Stat>() {
            @Override
            public Stat call() throws Exception {
                return zookeeperHandler.getZooKeeper().setData(path, data, version);
            }
        }, this);
    }

    public byte[] getData(final String path, final boolean watch, final Stat stat) throws Exception {

        return RetryLoop.retry(new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                return zookeeperHandler.getZooKeeper().getData(path, watch, stat);
            }
        }, this);
    }

    public byte[] getData(final String path, final Watcher watcher, final Stat stat) throws Exception {

        return RetryLoop.retry(new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                return zookeeperHandler.getZooKeeper().getData(path, watcher, stat);
            }
        }, this);
    }

    public Long getSessionId() throws Exception {

        return RetryLoop.retry(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return zookeeperHandler.getZooKeeper().getSessionId();
            }
        }, this);
    }

}