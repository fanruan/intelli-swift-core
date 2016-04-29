package com.fr.bi.cluster.zookeeper.operation;

import com.fr.bi.cluster.zookeeper.BIWorkerNodeValue;

/**
 * Created by Connery on 2015/4/2.
 */
public interface BIWorkerOperation {
    BIWorkerNodeValue operate(BIWorkerNodeValue nodeValue);
}