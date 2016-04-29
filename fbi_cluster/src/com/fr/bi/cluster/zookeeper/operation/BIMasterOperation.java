package com.fr.bi.cluster.zookeeper.operation;

/**
 * Created by Connery on 2015/4/2.
 */
public interface BIMasterOperation {
    void prepare(String basePath, String tmpPath, long userId);
}