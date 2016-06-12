package com.finebi.cube.conf;

import com.finebi.cube.conf.relation.BISystemTableRelationManager;
import com.finebi.cube.conf.relation.BISystemTableRelationManager4Test;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.base.cube.BISystemCubeConfManager;
import com.fr.bi.conf.log.BILogManager;
import com.fr.bi.conf.provider.BICubeConfManagerProvider;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.stable.bridge.StableFactory;
import junit.framework.TestCase;

/**
 * Created by 49597 on 2016/6/12.
 */
public class CubeBuildStuffManagerTest extends TestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        StableFactory.registerMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, new BISystemTableRelationManager4Test());
        StableFactory.registerMarkedObject(BITableRelationConfigurationProvider.XML_TAG, new BISystemTableRelationManager());
        StableFactory.registerMarkedObject(BICubeConfManagerProvider.XML_TAG, new BISystemCubeConfManager());
        StableFactory.registerMarkedObject(BILogManagerProvider.XML_TAG, new BILogManager());
    }

    public void testSingleTable() {
        CubeBuildStuff cubeBuildStuffManager = new CubeBuildStuffManagerSingleTable(null,-999);
    }

    public void testSingleTableSource() {
        CubeBuildStuff cubeBuildStuffManager = new CubeBuildStuffManagerTableSource(null,-999);
    }

    public void testIncremental() {
        CubeBuildStuff cubeBuildStuffManager = new CubeBuildStuffManagerIncremental(null,-999);
        assertTrue(cubeBuildStuffManager.getAllSingleSources().size() == 0);
        assertTrue(cubeBuildStuffManager.getDependTableResource().size() == 0);
        assertTrue(cubeBuildStuffManager.getRelationPaths().size() == 0);
        assertTrue(cubeBuildStuffManager.getTableRelationSet().size() == 0);
        assertTrue(!cubeBuildStuffManager.getVersions().isEmpty());
    }

    public void testAll() {
        CubeBuildStuff cubeBuildStuffManager = new CubeBuildStuffManager(new BIUser(-999));
        assertTrue(cubeBuildStuffManager.getAllSingleSources().size() == 0);
        assertTrue(cubeBuildStuffManager.getDependTableResource().size() == 0);
        assertTrue(cubeBuildStuffManager.getRelationPaths().size() == 0);
        assertTrue(cubeBuildStuffManager.getTableRelationSet().size() == 0);
        assertTrue(!cubeBuildStuffManager.getVersions().isEmpty());
    }
}