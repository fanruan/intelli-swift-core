package com.finebi.cube.gen;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.oper.BIFieldPathIndexBuilder;
import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.CubeRelationEntityGetterService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.CubeColumnReaderService;
import com.finebi.cube.tools.*;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;

/**
 * This class created on 2016/4/11.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFieldPathIndexTest extends BICubeTestBase {
    private BIFieldPathIndexBuilder indexBuilder;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        indexBuilder = new BIFieldPathIndexBuilder(cube, new BICubeFieldSource(BITableSourceTestTool.getDBTableSourceA(), "gender", DBConstant.CLASS.STRING, 6), BICubePathTestTool.getABC());
    }


    public void testFieldPathIndex() {
        try {
            BITablePathBuilderTest tablePathBuilderTest = new BITablePathBuilderTest();
            tablePathBuilderTest.buildTablePath();
            BIFieldIndexGeneratorTest fieldIndexGenerator = new BIFieldIndexGeneratorTest();
            fieldIndexGenerator.fieldIndexGenerator(BIMemoryDataSourceFactory.generateTableA(), 2);

            indexBuilder.mainTask(null);
            CubeColumnReaderService getterService = cube.getCubeColumn(BITableKeyUtils.convert(BIMemoryDataSourceFactory.generateTableA()), BIColumnKey.covertColumnKey(new BICubeFieldSource(BITableSourceTestTool.getDBTableSourceA(), "gender", DBConstant.CLASS.STRING, 6)));

            CubeRelationEntityGetterService relationEntityGetterService = getterService.getRelationIndexGetter(BICubePathTestTool.getABC());
            assertEquals(relationEntityGetterService.getBitmapIndex(getterService.getPositionOfGroup("girl")), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{1, 7}));
            assertEquals(relationEntityGetterService.getBitmapIndex(getterService.getPositionOfGroup(".dr")), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{4, 6}));
            assertEquals(relationEntityGetterService.getBitmapIndex(getterService.getPositionOfGroup("boy")), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));

            assertEquals(relationEntityGetterService.getNULLIndex(0), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{0, 2, 3, 5}));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

    }

    /**
     * Detail:如果非主键字段包含空值的情况
     * History:
     * Date:2016/6/15
     */
    public void testFieldPathContainNull() {
        try {
            BIRelationIndexBuilderTest relationIndexBuilderTest = new BIRelationIndexBuilderTest();
            BICubeRelation relation = BICubeRelationTestTool.getNullTableRelation();
            CubeTableSource parent = BIMemoryDataSourceFactory.generateTableNullParent();
            BICubeFieldSource fieldSource = new BICubeFieldSource(BIMemoryDataSourceFactory.generateTableNullParent(),
                    "Name", DBConstant.CLASS.STRING, 6);
            BIFieldIndexGeneratorTest.buildFieldIndex((BIMemoryDataSource) parent, (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableNullChild());
            relationIndexBuilderTest.generateRelationIndex(relation);
            indexBuilder = new BIFieldPathIndexBuilder(cube,
                    fieldSource, BICubePathTestTool.getContainNullPath());
            indexBuilder.mainTask(null);


            BIColumnKey columnKey = BIColumnKey.covertColumnKey(fieldSource);
            CubeColumnReaderService getterService = cube.getCubeColumn(BITableKeyUtils.convert(parent), columnKey);
            assertEquals(getterService.getNULLIndex(0), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{2}));
            CubeRelationEntityGetterService relationEntityGetterService = getterService.getRelationIndexGetter(BICubePathTestTool.getContainNullPath());
            assertEquals(relationEntityGetterService.getNULLIndex(0), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{2,3}));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

}
