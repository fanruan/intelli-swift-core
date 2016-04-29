package com.fr.bi.cube.engine.result;

import com.fr.bi.cube.engine.store.ColumnKey;
import junit.framework.TestCase;

/**
 * Created by Connery on 14-11-6.
 */
public class DiskBaseRootNodeChildTest extends TestCase {

    public void testGetBytes() {
        DiskBaseRootNodeChild diskBaseRootNodeChild = new DiskBaseRootNodeChild(new ColumnKey("HelloWorld?"), new Object());
//        byte[] bytes =   diskBaseRootNodeChild.getBytes();
//        assertTrue(bytes == null);
    }

    public void testCreateRootNodeChild() {
        DiskBaseRootNodeChild diskBaseRootNodeChild = new DiskBaseRootNodeChild(new ColumnKey("HelloWorld?"), new Object());
        RootNodeChild rootNodeChild = diskBaseRootNodeChild.createRootNodeChild();
        assertTrue(rootNodeChild != null);
    }

}