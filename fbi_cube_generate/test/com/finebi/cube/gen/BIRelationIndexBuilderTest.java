package com.finebi.cube.gen;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.oper.BIRelationIndexGenerator;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.ICubeRelationEntityGetterService;
import com.finebi.cube.tools.BICubeRelationTestTool;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;

/**
 * This class created on 2016/4/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIRelationIndexBuilderTest extends BICubeTestBase {
    private BIRelationIndexGenerator indexGenerator;
    ICubeTableSource tableA;
    ICubeTableSource tableB;
    BICubeRelation relation;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tableA = BIMemoryDataSourceFactory.generateTableA();
        tableB = BIMemoryDataSourceFactory.generateTableB();

        relation = BICubeRelationTestTool.getTaTb();
    }

    public void setTableA(ICubeTableSource tableA) {
        this.tableA = tableA;
    }

    public void setTableB(ICubeTableSource tableB) {
        this.tableB = tableB;
    }

    public void generateRelationIndex(BICubeRelation relation, int primaryIndex, int foreignIndex) {
        try {
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
        BISourceDataTransportTest transportTest = new BISourceDataTransportTest();
        transportTest.transport(tableA);
        transportTest.transport(tableB);

        BIFieldIndexGeneratorTest fieldIndexGenerator = new BIFieldIndexGeneratorTest();
        fieldIndexGenerator.fieldIndexGenerator(tableA, primaryIndex);
        fieldIndexGenerator.fieldIndexGenerator(tableB, foreignIndex);

        indexGenerator = new BIRelationIndexGenerator(cube, relation);
        indexGenerator.mainTask(null);

    }

    public void testRelationTest() {
        try {
            generateRelationIndex(relation, 1, 2);
            ICubeRelationEntityGetterService relationService = cube.getCubeRelation(BITableKeyUtils.convert(tableA), relation);
            assertEquals(relationService.getBitmapIndex(0), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{0}));
            assertEquals(relationService.getBitmapIndex(1), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{2, 4, 6}));
            assertEquals(relationService.getBitmapIndex(2), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));
            assertEquals(relationService.getBitmapIndex(3), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{3}));
            assertEquals(relationService.getBitmapIndex(4), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));
            assertEquals(relationService.getNULLIndex(0), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{1, 5}));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

    }

}
