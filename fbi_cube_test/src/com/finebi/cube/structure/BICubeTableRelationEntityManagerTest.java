package com.finebi.cube.structure;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.*;
import com.finebi.cube.tools.BICubeConfigurationTool;
import com.finebi.cube.tools.BITableSourceRelationPathTestTool;
import com.finebi.cube.tools.BITableSourceTestTool;
import com.finebi.cube.tools.GroupValueIndexTestTool;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.finebi.cube.common.log.BILoggerFactory;
import junit.framework.TestCase;

/**
 * This class created on 2016/3/31.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTableRelationEntityManagerTest extends TestCase {

    private ICubeResourceRetrievalService retrievalService;
    private ICubeConfiguration cubeConfiguration;
    private BICubeTableRelationEntityManager relationEntityManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cubeConfiguration = new BICubeConfigurationTool();
        retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        relationEntityManager = new BICubeTableRelationEntityManager(retrievalService, BITableKeyUtils.convert(BITableSourceTestTool.getDBTableSourceA()), BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
    }

    public void testBasic() {
        try {
            ICubeRelationEntityService relationEntityService = relationEntityManager.getRelationService(BICubePathUtils.convert(BITableSourceRelationPathTestTool.generatePathABC()));
            relationEntityService.addRelationIndex(0, GroupValueIndexTestTool.generateSampleIndex());
            relationEntityService.forceReleaseWriter();
            assertEquals(GroupValueIndexTestTool.generateSampleIndex(), relationEntityService.getBitmapIndex(0));

        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }


    public void testIllegalRelationException() {
        try {
            relationEntityManager.getRelationService(BICubePathUtils.convert(BITableSourceRelationPathTestTool.generatePathBC()));
        } catch (IllegalRelationPathException e) {
            assertTrue(true);
            return;
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
        assertTrue(false);
    }


}
