package com.finebi.integration.cube.gen;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.oper.BIRelationIndexGenerator;
import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.CubeRelationEntityGetterService;
import com.finebi.cube.tools.BICubeRelationTestTool;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;

/**
 * This class created on 2016/4/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIRelationIndexBuilderTest extends BICubeTestBase {
    private static final int PMD4 = 4;
    private static final int PMD5 = 5;
    private static final int PMD6 = 6;
    private BIRelationIndexGenerator indexGenerator;
    CubeTableSource tableA;
    CubeTableSource tableB;
    BICubeRelation relation;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tableA = BIMemoryDataSourceFactory.generateTableA();
        tableB = BIMemoryDataSourceFactory.generateTableB();

        relation = BICubeRelationTestTool.getTaTb();
    }

    public void setTableA(CubeTableSource tableA) {
        this.tableA = tableA;
    }

    public void setTableB(CubeTableSource tableB) {
        this.tableB = tableB;
    }

    public void generateRelationIndex(BICubeRelation relation, CubeTableSource tableA,
                                      CubeTableSource tableB, int primaryIndex, int foreignIndex) {
        try {
            setUp();
        } catch (Exception e) {
            assertTrue(false);
        }
        BISourceDataTransportTest transportTest = new BISourceDataTransportTest();
        transportTest.transport(tableA);
        transportTest.transport(tableB);

        BIFieldIndexGeneratorTest fieldIndexGenerator = new BIFieldIndexGeneratorTest();
        fieldIndexGenerator.fieldIndexGenerator(tableA, primaryIndex);
        fieldIndexGenerator.fieldIndexGenerator(tableB, foreignIndex);

        indexGenerator = new BIRelationIndexGenerator(cube, null, relation, null);
        indexGenerator.mainTask(null);
    }

    public void generateRelationIndex(BICubeRelation relation) {
        try {
            setUp();
        } catch (Exception e) {
            assertTrue(false);
        }

        indexGenerator = new BIRelationIndexGenerator(cube, null, relation, null);
        indexGenerator.mainTask(null);
    }

    public void testRelationTest() {
        try {
            generateRelationIndex(relation, tableA, tableB, 1, 2);
            CubeRelationEntityGetterService relationService = cube.getCubeRelation(BITableKeyUtils.convert(tableA), relation);
            assertEquals(relationService.getBitmapIndex(0), GVIFactory.createGroupValueIndexBySimpleIndex(new int[]{0}));
            assertEquals(relationService.getBitmapIndex(1), GVIFactory.createGroupValueIndexBySimpleIndex(new int[]{2, 4, PMD6}));
            assertEquals(relationService.getBitmapIndex(2), GVIFactory.createGroupValueIndexBySimpleIndex(new int[]{}));
            assertEquals(relationService.getBitmapIndex(3), GVIFactory.createGroupValueIndexBySimpleIndex(new int[]{3}));
            assertEquals(relationService.getBitmapIndex(PMD4), GVIFactory.createGroupValueIndexBySimpleIndex(new int[]{}));
            assertEquals(relationService.getNULLIndex(0), GVIFactory.createGroupValueIndexBySimpleIndex(new int[]{1, PMD5}));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

    }

    /**
     * Detail:测试关联表中包含空值的索引情况
     * <p/>
     * Target:TableContainNull
     * History:
     * Date:2016/6/15
     */
    public void testTableContainNull() {
        try {
            BICubeRelation relation = BICubeRelationTestTool.getNullTableRelation();
            generateRelationIndex(relation, BIMemoryDataSourceFactory.generateTableNullParent(),
                    BIMemoryDataSourceFactory.generateTableNullChild(), 0, 1);
            CubeRelationEntityGetterService relationService = cube.getCubeRelation(relation.getPrimaryTable(), relation);
            assertEquals(relationService.getNULLIndex(0), RoaringGroupValueIndex.createGroupValueIndex(new int[]{3}));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
