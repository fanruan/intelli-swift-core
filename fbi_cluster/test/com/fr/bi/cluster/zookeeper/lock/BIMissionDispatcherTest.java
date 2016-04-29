package com.fr.bi.cluster.zookeeper.lock;

import com.fr.bi.cluster.zookeeper.BIMissionDispatcher;
import com.fr.bi.cluster.zookeeper.BIWorkerNodeValue;
import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Created by Connery on 2015/3/29.
 */
public class BIMissionDispatcherTest extends TestCase {
    public void testQueue() {
        BIMissionDispatcher dispatcher = BIMissionDispatcher.getInstance();
        ArrayList<BIWorkerNodeValue> a = new ArrayList<BIWorkerNodeValue>();
        a.add(new BIWorkerNodeValue());
        a.add(new BIWorkerNodeValue());
        ArrayList<BIWorkerNodeValue> b = new ArrayList<BIWorkerNodeValue>();
        b.add(new BIWorkerNodeValue());
        dispatcher.addMission(a);
        dispatcher.addMission(b);
        assertEquals(dispatcher.getMissionSize(), 2);
        ArrayList<BIWorkerNodeValue> c = dispatcher.getMission();
        assertEquals(c.size(), a.size());
        assertEquals(dispatcher.getMissionSize(), 1);
    }
}