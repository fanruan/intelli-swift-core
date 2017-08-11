package com.finebi.integration.cube.structure.relation;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.adapter.BIUserCubeManager;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.exception.BICubeTableAbsentException;
import com.finebi.integration.cube.gen.BISourceDataTransportTest;
import com.finebi.cube.structure.ICubeRelationEntityService;
import com.finebi.cube.tools.BICubeRelationTestTool;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.fr.fs.control.UserControl;

/**
 * This class created on 2016/6/3.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeRelationEntityTest extends BICubeTestBase {

    private ICubeRelationEntityService relationEntityService;
    private BIUserCubeManager manager;
    private ICubeTableService tableService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        manager = new BIUserCubeManager(UserControl.getInstance().getSuperManagerID(), cube);
        try {
            tableService = manager.getTableIndex(BIMemoryDataSourceFactory.generateTableA());
        } catch (BICubeTableAbsentException e) {
            BISourceDataTransportTest transportTest = new BISourceDataTransportTest();
            transportTest.transport(BIMemoryDataSourceFactory.generateTableA());
        }
        try {
            tableService = manager.getTableIndex(BIMemoryDataSourceFactory.generateTableB());
        } catch (BICubeTableAbsentException e) {
            BISourceDataTransportTest transportTest = new BISourceDataTransportTest();
            transportTest.transport(BIMemoryDataSourceFactory.generateTableB());
        }


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
            relationEntityService.forceReleaseWriter();
            assertEquals(12, relationEntityService.getReverseIndex(14));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
