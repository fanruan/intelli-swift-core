package com.finebi.cube;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.BICubeConfigurationTest;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.table.BICubeTableEntity;
import com.finebi.cube.tools.BITableSourceTestTool;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.common.factory.BIFactoryHelper;
import junit.framework.TestCase;

/**
 * This class created on 2016/4/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTestBase extends TestCase {

    protected ICubeResourceRetrievalService retrievalService;
    protected ICubeConfiguration cubeConfiguration;
    protected BICube cube;
    protected BICubeTableEntity tableEntity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cubeConfiguration = new BICubeConfigurationTest();
        retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        cube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        tableEntity = (BICubeTableEntity) cube.getCubeTableWriter(BITableKeyUtils.convert(BITableSourceTestTool.getDBTableSourceA()));
    }

    public void testVoid() {

    }
}
