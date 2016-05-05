package com.finebi.cube.gen;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.oper.BIFieldPathIndexBuilder;
import com.finebi.cube.structure.ICubeRelationEntityGetterService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnReaderService;
import com.finebi.cube.tools.BICubePathTestTool;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.DBField;
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
        indexBuilder = new BIFieldPathIndexBuilder(cube, new DBField("tableA", "gender", DBConstant.CLASS.STRING, 6), BICubePathTestTool.getABC());
    }

    public void testFieldPathIndex() {
        try {
            BITablePathBuilderTest tablePathBuilderTest = new BITablePathBuilderTest();
            tablePathBuilderTest.buildTablePath();
            BIFieldIndexGeneratorTest fieldIndexGenerator = new BIFieldIndexGeneratorTest();
            fieldIndexGenerator.fieldIndexGenerator(BIMemoryDataSourceFactory.generateTableA(), 2);

            indexBuilder.mainTask(null);
            ICubeColumnReaderService getterService = cube.getCubeColumn(BITableKeyUtils.convert(BIMemoryDataSourceFactory.generateTableA()), BIColumnKey.covertColumnKey(new DBField("tableA", "gender", DBConstant.CLASS.STRING, 6)));

            ICubeRelationEntityGetterService relationEntityGetterService = getterService.getRelationIndexGetter(BICubePathTestTool.getABC());
            assertEquals(relationEntityGetterService.getBitmapIndex(getterService.getPositionOfGroup("girl")), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{1, 7}));
            assertEquals(relationEntityGetterService.getBitmapIndex(getterService.getPositionOfGroup(".dr")), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{4, 6}));
            assertEquals(relationEntityGetterService.getBitmapIndex(getterService.getPositionOfGroup("boy")), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));

            assertEquals(relationEntityGetterService.getNULLIndex(0), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{0, 2, 3, 5}));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

    }

}
