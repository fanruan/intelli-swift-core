package com.finebi.cube.structure;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.tools.BICubeRelationTestTool;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.utils.BITableKeyUtils;

/**
 * This class created on 2016/6/3.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeRelationEntityTest extends BICubeTestBase {


    private ICubeRelationEntityService relationEntityService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        relationEntityService = (ICubeRelationEntityService) cube.getCubeRelation(BITableKeyUtils.convert( BIMemoryDataSourceFactory.generateTableA()), BICubeRelationTestTool.getTaTb());
    }

    public void testVersionWriteRead() {
        try {
            relationEntityService.addVersion(100);
            assertEquals(100, relationEntityService.getCubeVersion());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testReverseWriteRead() {
        try {
            relationEntityService.addReverseIndex(14, new Integer(12));
            assertEquals(12, relationEntityService.getReverseIndex(14));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
