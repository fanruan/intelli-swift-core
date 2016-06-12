package com.finebi.cube.conf;

import com.fr.bi.base.BIUser;
import junit.framework.TestCase;

/**
 * Created by 49597 on 2016/6/12.
 */
public class CubeBuildStuffManagerTest extends TestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testSingleTable() {
    }

    public void testSingleTableSource() {
        CubeBuildStuff cubeBuildStuffManager = new CubeBuildStuffManager(new BIUser(-999));
    }

    public void testIncremental() {

    }

    public void testAll() {
        CubeBuildStuff cubeBuildStuffManager = new CubeBuildStuffManager(new BIUser(-999));
        cubeBuildStuffManager.getAllSingleSources();

    }
}