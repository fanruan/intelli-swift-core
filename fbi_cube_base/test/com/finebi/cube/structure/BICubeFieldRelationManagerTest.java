package com.finebi.cube.structure;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.location.BICubeConfigurationTest;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.tools.BITableSourceRelationPathTestTool;
import com.finebi.cube.tools.BITableSourceTestTool;
import com.finebi.cube.tools.DBFieldTestTool;
import com.finebi.cube.tools.GroupValueIndexTestTool;
import com.finebi.cube.utils.BICubePathUtils;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

/**
 * This class created on 2016/4/6.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeFieldRelationManagerTest extends TestCase {

    private ICubeResourceRetrievalService retrievalService;
    private ICubeConfiguration cubeConfiguration;
    private BICubeFieldRelationManager relationEntityManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cubeConfiguration = new BICubeConfigurationTest();
        retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        relationEntityManager = new BICubeFieldRelationManager(retrievalService, BITableKeyUtils.convert(BITableSourceTestTool.getDBTableSourceA()), BIColumnKey.covertColumnKey(DBFieldTestTool.generateSTRINGA()));
    }

    public void testFieldRelation() {
        try {
            ICubeRelationEntityService relationEntityService = relationEntityManager.getRelationService(BICubePathUtils.convert(BITableSourceRelationPathTestTool.generatePathAaBC()));
            relationEntityService.addRelationIndex(0, GroupValueIndexTestTool.generateSampleIndex());
            assertEquals(GroupValueIndexTestTool.generateSampleIndex(), relationEntityService.getBitmapIndex(0));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }
}
