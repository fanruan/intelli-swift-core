package com.finebi.cube.location;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.tools.BIColumnKeyTestTool;
import com.finebi.cube.tools.BITableSourceRelationPathTestTool;
import com.finebi.cube.tools.BITableSourceRelationTestTool;
import com.finebi.cube.tools.BITableSourceTestTool;
import com.finebi.cube.utils.BICubePathUtils;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.stable.relation.BITableSourceRelationPath;
import com.fr.bi.stable.utils.algorithem.BIMD5Utils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.stable.StableUtils;
import junit.framework.TestCase;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public class CubeResourceRetrieveTest extends TestCase {
    private ICubeResourceRetrievalService retrievalService;
    private ICubeConfiguration cubeConfiguration;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cubeConfiguration = new BICubeConfigurationTest();
        retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
    }

    public void testITableResource() {
        try {
            ICubeResourceLocation location = retrievalService.retrieveResource(BITableKeyUtils.convert(BITableSourceTestTool.getDBTableSourceA()));
            assertEquals(StableUtils.pathJoin(cubeConfiguration.getRootURI().getPath(), BITableSourceTestTool.getDBTableSourceA().fetchObjectCore().getIDValue())
                    , location.getAbsolutePath());
        } catch (Exception allIgnore) {
            assertTrue(false);
            BILogger.getLogger().error(allIgnore.getMessage(), allIgnore);
        }
    }

    public void testAddRelationNOException() {
        try {
            retrievalService.retrieveResource(BITableKeyUtils.convert(BITableSourceTestTool.getDBTableSourceA()));
            BITableSourceRelationPath path = BITableSourceRelationPathTestTool.generatePathABC();
            ICubeResourceLocation location = retrievalService.retrieveResource(BITableKeyUtils.convert(path.getStartTable()), BICubePathUtils.convert(path));
            assertEquals(StableUtils.pathJoin(cubeConfiguration.getRootURI().getPath(), BITableSourceTestTool.getDBTableSourceA().fetchObjectCore().getIDValue()
                    , ICubeResourceRetrievalService.RELATION_NAME,
                    BIMD5Utils.getMD5String(new String[]{BITableKeyUtils.convert(BITableSourceTestTool.getDBTableSourceA()).getSourceID(), BICubePathUtils.convert(path).getSourceID()}))
                    , location.getAbsolutePath());
        } catch (BICubeResourceAbsentException allIgnore) {
            BILogger.getLogger().error(allIgnore.getMessage(), allIgnore);
            assertTrue(false);

        } catch (Exception allIgnore) {
            BILogger.getLogger().error(allIgnore.getMessage(), allIgnore);
            assertTrue(false);

        }

    }

    public void testAddRelation() {
        try {
            BITableSourceRelationPath path = new BITableSourceRelationPath();
            path.addRelationAtTail(BITableSourceRelationTestTool.getAB()).addRelationAtTail(BITableSourceRelationTestTool.getBC());
            ICubeResourceLocation location = retrievalService.retrieveResource(BITableKeyUtils.convert(path.getStartTable()), BICubePathUtils.convert(path));
            assertEquals(StableUtils.pathJoin(cubeConfiguration.getRootURI().getPath(), BITableSourceTestTool.getDBTableSourceA().fetchObjectCore().getIDValue()
                    , ICubeResourceRetrievalService.RELATION_NAME,
                    BIMD5Utils.getMD5String(new String[]{BITableSourceTestTool.getDBTableSourceA().fetchObjectCore().getIDValue(), BICubePathUtils.convert(path).getSourceID()}))
                    , location.getAbsolutePath());
        } catch (BICubeResourceAbsentException allIgnore) {
            assertTrue(false);
        } catch (Exception allIgnore) {
            assertTrue(false);
            BILogger.getLogger().error(allIgnore.getMessage(), allIgnore);
        }

    }

    public void testFieldRelation() {
        try {
            BIColumnKey key = BIColumnKeyTestTool.generateA();
            BITableSourceRelationPath path = new BITableSourceRelationPath();
            path.addRelationAtTail(BITableSourceRelationTestTool.getAB()).addRelationAtTail(BITableSourceRelationTestTool.getBC());
            ICubeResourceLocation location = retrievalService.retrieveResource(BITableKeyUtils.convert(BITableSourceTestTool.getDBTableSourceB()), key, BICubePathUtils.convert(path));
            assertEquals(StableUtils.pathJoin(cubeConfiguration.getRootURI().getPath(), BITableSourceTestTool.getDBTableSourceB().fetchObjectCore().getIDValue(),
                    BIMD5Utils.getMD5String(new String[]{BITableSourceTestTool.getDBTableSourceB().fetchObjectCore().getIDValue(), key.getKey()}),
                    ICubeResourceRetrievalService.RELATION_NAME
                    , BIMD5Utils.getMD5String(new String[]{BITableSourceTestTool.getDBTableSourceB().fetchObjectCore().getIDValue(), key.getKey(), BICubePathUtils.convert(path).getSourceID()}))
                    , location.getAbsolutePath());
        } catch (BICubeResourceAbsentException allIgnore) {
            BILogger.getLogger().error(allIgnore.getMessage(), allIgnore);
            assertFalse(true);
        } catch (Exception allIgnore) {
            BILogger.getLogger().error(allIgnore.getMessage(), allIgnore);
            assertFalse(true);
        }
    }

    public void testField() {
        try {
            BIColumnKey key = BIColumnKeyTestTool.generateA();

            ICubeResourceLocation location = retrievalService.retrieveResource(BITableKeyUtils.convert(BITableSourceTestTool.getDBTableSourceA()), key);
            assertEquals(StableUtils.pathJoin(cubeConfiguration.getRootURI().getPath(), BITableSourceTestTool.getDBTableSourceA().fetchObjectCore().getIDValue()
                    , BIMD5Utils.getMD5String(new String[]{BITableSourceTestTool.getDBTableSourceA().getSourceID(), key.getKey()}))
                    , location.getAbsolutePath());
        } catch (BICubeResourceAbsentException allIgnore) {
            BILogger.getLogger().error(allIgnore.getMessage(), allIgnore);
            assertFalse(true);
        } catch (Exception allIgnore) {
            BILogger.getLogger().error(allIgnore.getMessage(), allIgnore);
            assertFalse(true);
        }
    }
}
