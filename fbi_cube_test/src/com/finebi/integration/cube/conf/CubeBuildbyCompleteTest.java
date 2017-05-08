package com.finebi.integration.cube.conf;

import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.BITableRelationConfigurationProvider;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.pack.imp.BISystemPackageConfigurationManager;
import com.finebi.cube.conf.relation.BISystemTableRelationManager;
import com.finebi.cube.impl.conf.CubeBuildStuffComplete;
import com.finebi.cube.impl.conf.CubeBuildStuffSupplement;
import com.finebi.cube.impl.conf.CubeBuildStuffRealTime;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.tools.BIMemoryDataSource;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.base.cube.BISystemCubeConfManager;
import com.fr.bi.conf.log.BILogManager;
import com.fr.bi.conf.provider.BICubeConfManagerProvider;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.stable.bridge.StableFactory;
import junit.framework.TestCase;

import java.util.HashSet;

/**
 * Created by kary on 2016/6/12.
 */
public class CubeBuildbyCompleteTest extends TestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
//        StableFactory.registerMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, new BISystemTableRelationManagerTestTool());
        StableFactory.registerMarkedObject(BITableRelationConfigurationProvider.XML_TAG, new BISystemTableRelationManager());
        StableFactory.registerMarkedObject(BICubeConfManagerProvider.XML_TAG, new BISystemCubeConfManager());
        StableFactory.registerMarkedObject(BILogManagerProvider.XML_TAG, new BILogManager());
        StableFactory.registerMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, new BISystemPackageConfigurationManager());
    }

    public void testSingleTable() {
    }

    public void testSingleTableSource() {
        Class a = CubeBuildbyCompleteTest.class;
        CubeBuildStuff cubeBuildManager = new CubeBuildStuffRealTime(new BIMemoryDataSource(), -999);
    }

    public void testIncremental() {
        CubeBuildStuff cubeBuildManager = new CubeBuildStuffSupplement(-999, new HashSet<CubeTableSource>(), new HashSet<BITableSourceRelation>(), new HashSet<BITableSourceRelationPath>());
        assertTrue(cubeBuildManager.getSingleSourceLayers().size() == 0);
        assertTrue(cubeBuildManager.getDependTableResource().size() == 0);
        assertTrue(cubeBuildManager.getTableSourceRelationPathSet().size() == 0);
        assertTrue(cubeBuildManager.getVersions().isEmpty());
    }

    public void testAll() {
        CubeBuildStuff cubeBuildManager = new CubeBuildStuffComplete(new BIUser(-999));
        assertTrue(cubeBuildManager.getSingleSourceLayers().size() == 0);
        assertTrue(cubeBuildManager.getDependTableResource().size() == 0);
        assertTrue(cubeBuildManager.getTableSourceRelationPathSet().size() == 0);
        assertTrue(cubeBuildManager.getVersions().isEmpty());
    }
}
