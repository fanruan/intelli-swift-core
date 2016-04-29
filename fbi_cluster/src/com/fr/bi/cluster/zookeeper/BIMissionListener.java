package com.fr.bi.cluster.zookeeper;

/**
 * Created by Connery on 2015/3/29.
 * 当前分配的任务，在所有工作节点上完成后。
 * 会调用此接口
 */
public interface BIMissionListener {
    void fireJobFinishedListener();
}