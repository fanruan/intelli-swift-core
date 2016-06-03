package com.finebi.cube.adapter;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.exception.BICubeTableAbsentException;
import com.finebi.cube.gen.BISourceDataTransportTest;
import com.finebi.cube.tools.BIMemoryDataSource;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.tools.BITableSourceRelationPathTestTool;
import com.finebi.cube.tools.GroupValueIndexTestTool;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.fs.control.UserControl;

import java.util.Iterator;
import java.util.List;

/**
 * This class created on 2016/4/18.
 *
 * @author Connery
 * @since 4.0
 */
public class ICubeTableServiceTest extends BICubeTestBase {
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

    }

    public void testTableOriginalData() {
        assertEquals(tableService.getRow(new IndexKey("name"), 0), "Parker");
        assertEquals(tableService.getRow(new IndexKey("name"), 1), "Jam");
        assertEquals(tableService.getRow(new IndexKey("name"), 2), "Blue");
        assertEquals(tableService.getRow(new IndexKey("name"), 3), "Sam");
        assertEquals(tableService.getRow(new IndexKey("name"), 4), "Eliza");
        assertEquals(tableService.getRow(new IndexKey("name"), 5), "");
    }

    public void testTableRelation() {
        try {
            BIMemoryDataSource memoryDataSource = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableA();
            List<String> content = memoryDataSource.contents.get(1);
            Iterator<String> it = content.iterator();
            while (it.hasNext()) {
                String name = it.next();
                assertEquals(tableService.getIndexes(new IndexKey("name"), new Object[]{name})[0],
                        GroupValueIndexTestTool.build(content, name));
            }
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testTableColumnMax() {
        try {
            assertEquals(tableService.getMAXValue(new IndexKey("age")), Double.valueOf(5));
            assertEquals(tableService.getMAXValue(RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{0, 1, 2, 3}), new IndexKey("age")), Double.valueOf(4));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testTableColumnMin() {
        try {
            assertEquals(tableService.getMINValue(new IndexKey("age")), Double.valueOf(1));
            assertEquals(tableService.getMINValue(RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{1, 2, 3}), new IndexKey("age")), Double.valueOf(2));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testTableFieldRelation() {
        try {

            ICubeColumnIndexReader reader = tableService.loadGroup(new IndexKey("gender"), BITableSourceRelationPathTestTool.getABCList());
            assertEquals(reader.getGroupIndex(new Object[]{"girl"})[0], RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{1, 7}));
            assertEquals(reader.getGroupIndex(new Object[]{".dr"})[0], RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{4, 6}));
            assertEquals(reader.getGroupIndex(new Object[]{"boy"})[0], RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testTableTableRelation() {
        try {
            ICubeTableIndexReader reader = tableService.ensureBasicIndex(BITableSourceRelationPathTestTool.getABCList());
            assertEquals(reader.get(0), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{1, 7}));
            assertEquals(reader.get(1), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{4, 6}));
            assertEquals(reader.get(2), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));
            assertEquals(reader.get(3), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));
            assertEquals(reader.get(4), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testGetRow() {
        try {
            ICubeTableIndexReader reader = tableService.ensureBasicIndex(BITableSourceRelationPathTestTool.getABCList());
//          assertEquals(tableService.getIndexByRow(0), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{1, 7}));
            assertEquals(reader.get(1), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{4, 6}));
            assertEquals(reader.get(2), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));
            assertEquals(reader.get(3), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));
            assertEquals(reader.get(4), RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{}));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
