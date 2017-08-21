package com.finebi.integration.cube.gen;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.oper.BIFieldIndexGenerator;
import com.finebi.cube.structure.CubeTableEntityGetterService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.CubeColumnReaderService;
import com.finebi.cube.structure.column.date.BIDateColumnTool;
import com.finebi.cube.structure.table.BICubeTableEntity;
import com.finebi.cube.tools.BIMemDataSourceTestToolCube;
import com.finebi.cube.tools.BIMemoryDataSource;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.tools.GroupValueIndexTestTool;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;

import java.util.*;

/**
 * This class created on 2016/4/9.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFieldIndexGeneratorTest extends BICubeTestBase {

    private BIFieldIndexGenerator fieldIndexGenerator;
    private BIMemDataSourceTestToolCube tableSource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tableSource = new BIMemDataSourceTestToolCube();
        tableEntity = (BICubeTableEntity) cube.getCubeTableWriter(BITableKeyUtils.convert(tableSource));
    }

    public static void buildFieldIndex(BIMemoryDataSource... tables) {
        BISourceDataTransportTest transportTest = new BISourceDataTransportTest();

        BIFieldIndexGeneratorTest fieldIndexGenerator = new BIFieldIndexGeneratorTest();
        for (BIMemoryDataSource source : tables) {
            transportTest.transport(source);
            for (int i = 0; i < source.contents.size(); i++) {
                fieldIndexGenerator.fieldIndexGenerator(source, i);
            }
        }
    }

    public void fieldIndexGenerator(CubeTableSource tableSource, int columnIndex) {
        try {
            setUp();
            BISourceDataTransportTest transportTest = new BISourceDataTransportTest();
            transportTest.transport(tableSource);
            ICubeFieldSource field = tableSource.getFieldsArray(null)[columnIndex];
            Iterator<BIColumnKey> columnKeyIterator = BIColumnKey.generateColumnKey(field).iterator();
            while (columnKeyIterator.hasNext()) {
                BIColumnKey columnKey = columnKeyIterator.next();
                BIFieldIndexGenerator fieldIndexGenerator = new BIFieldIndexGenerator(cube, tableSource, tableSource.getFieldsArray(null)[columnIndex], columnKey);
                fieldIndexGenerator.mainTask(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndex() {
        try {
            fieldIndexGenerator(tableSource, 1);
            CubeColumnReaderService columnReaderService = tableEntity.getColumnDataGetter(BIColumnKey.covertColumnKey(tableSource.getFieldsArray(null)[1]));
            List<String> content = duplicateRemove(tableSource.stringData);
            Collections.sort(content, ComparatorFacotry.CHINESE_ASC);
            for (int i = 0; i < content.size(); i++) {
                assertEquals(content.get(i), columnReaderService.getGroupObjectValue(i));
                String one = content.get(i);
                GroupValueIndex groupValueIndex = columnReaderService.getBitmapIndex(i);
                assertTrue(groupValueIndex.hasSameValue(GroupValueIndexTestTool.build(tableSource.stringData, one))
                        || groupValueIndex.equals(GroupValueIndexTestTool.build(tableSource.stringData, one)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDate() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            CubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));
            CubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIColumnKey.covertColumnKey(tableData.getFieldsArray(null)[0]));
            List<Long> content = duplicateLongRemove(tableData.contents.get(0));
            Collections.sort(content, ComparatorFacotry.LONG_ASC);
            for (int i = 0; i < content.size(); i++) {
                assertEquals(content.get(i), columnReaderService.getGroupObjectValue(i));
                Long one = content.get(i);
                GroupValueIndex groupValueIndex = columnReaderService.getBitmapIndex(i);
                assertTrue(groupValueIndex.hasSameValue(GroupValueIndexTestTool.build(tableData.contents.get(0), one))
                        || groupValueIndex.equals(GroupValueIndexTestTool.build(tableData.contents.get(0), one)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateYear() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            CubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));
            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            CubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateYear(field));
            assertEquals(1991, columnReaderService.getGroupObjectValue(0));
            assertEquals(1992, columnReaderService.getGroupObjectValue(1));
            assertEquals(1993, columnReaderService.getGroupObjectValue(2));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateYearIndex() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            CubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));
            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            CubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateYear(field));
            assertTrue(RoaringGroupValueIndex.createGroupValueIndex(new int[]{5}).hasSameValue(columnReaderService.getBitmapIndex(0))
                    || RoaringGroupValueIndex.createGroupValueIndex(new int[]{5}).equals(columnReaderService.getBitmapIndex(0)));
            assertTrue(RoaringGroupValueIndex.createGroupValueIndex(new int[]{0, 1, 2, 3, 4}).hasSameValue(columnReaderService.getBitmapIndex(1))
                    || RoaringGroupValueIndex.createGroupValueIndex(new int[]{0, 1, 2, 3, 4}).equals(columnReaderService.getBitmapIndex(1)));
            assertTrue(RoaringGroupValueIndex.createGroupValueIndex(new int[]{6}).hasSameValue(columnReaderService.getBitmapIndex(2))
                    || RoaringGroupValueIndex.createGroupValueIndex(new int[]{6}).equals(columnReaderService.getBitmapIndex(2)));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateMonth() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            CubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));

            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            CubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateMonth(field));
            assertEquals(7, columnReaderService.getGroupObjectValue(0));
            assertEquals(8, columnReaderService.getGroupObjectValue(1));
            assertEquals(9, columnReaderService.getGroupObjectValue(2));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateMonthIndex() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            CubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));

            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            CubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateMonth(field));
            assertTrue(RoaringGroupValueIndex.createGroupValueIndex(new int[]{5}).hasSameValue(columnReaderService.getBitmapIndex(0)));
            assertTrue(RoaringGroupValueIndex.createGroupValueIndex(new int[]{0, 1, 2, 3, 4}).hasSameValue(columnReaderService.getBitmapIndex(1)));
            assertTrue(RoaringGroupValueIndex.createGroupValueIndex(new int[]{6}).hasSameValue(columnReaderService.getBitmapIndex(2)));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateDay() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            CubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));

            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            CubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateDay(field));
            assertEquals(6, columnReaderService.getGroupObjectValue(0));
            assertEquals(9, columnReaderService.getGroupObjectValue(1));
            assertEquals(10, columnReaderService.getGroupObjectValue(2));
            assertEquals(11, columnReaderService.getGroupObjectValue(3));
            assertEquals(12, columnReaderService.getGroupObjectValue(4));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateDayIndex() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            CubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));

            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            CubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateDay(field));
            assertTrue(RoaringGroupValueIndex.createGroupValueIndex(new int[]{0}).hasSameValue(columnReaderService.getBitmapIndex(0))
                    || RoaringGroupValueIndex.createGroupValueIndex(new int[]{0}).equals(columnReaderService.getBitmapIndex(0)));
            assertTrue(RoaringGroupValueIndex.createGroupValueIndex(new int[]{1}).hasSameValue(columnReaderService.getBitmapIndex(1))
                    || RoaringGroupValueIndex.createGroupValueIndex(new int[]{1}).equals(columnReaderService.getBitmapIndex(1)));
            assertTrue(RoaringGroupValueIndex.createGroupValueIndex(new int[]{2}).hasSameValue(columnReaderService.getBitmapIndex(2))
                    || RoaringGroupValueIndex.createGroupValueIndex(new int[]{2}).equals(columnReaderService.getBitmapIndex(2)));
            assertTrue(RoaringGroupValueIndex.createGroupValueIndex(new int[]{3, 5}).hasSameValue(columnReaderService.getBitmapIndex(3))
                    || RoaringGroupValueIndex.createGroupValueIndex(new int[]{3, 5}).equals(columnReaderService.getBitmapIndex(3)));
            assertTrue(RoaringGroupValueIndex.createGroupValueIndex(new int[]{4, 6}).hasSameValue(columnReaderService.getBitmapIndex(4))
                    || RoaringGroupValueIndex.createGroupValueIndex(new int[]{4, 6}).equals(columnReaderService.getBitmapIndex(4)));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateWeek() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            CubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));

            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            CubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateWeek(field));
            assertEquals(1, columnReaderService.getGroupObjectValue(0));
            assertEquals(2, columnReaderService.getGroupObjectValue(1));
            assertEquals(3, columnReaderService.getGroupObjectValue(2));
            assertEquals(4, columnReaderService.getGroupObjectValue(3));
            assertEquals(7, columnReaderService.getGroupObjectValue(4));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateSeason() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            CubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));

            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            CubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateSeason(field));
            assertEquals(3, columnReaderService.getGroupObjectValue(0));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

//    public void testFieldIndexDateYearMonthDay() {
//        try {
//            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
//            BICubeTableEntity dataTable = (BICubeTableEntity) cube.getCubeTable(BITableKeyUtils.convetTableRealtionToTableSourceRealtion(tableData));
//            fieldIndexGenerator(tableData, 0);
//            DBField field = tableData.getFieldsArray(null)[0];
//            ICubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateYearMonthDay(field));
//            assertEquals(3, columnReaderService.getGroupObjectValue(0));
//        } catch (Exception e) {
//            e.printStackTrace();
//            assertTrue(false);
//        }
//    }

    public void testFieldRowIndex() {
        try {
            BISourceDataTransportTest transportTest = new BISourceDataTransportTest();
            transportTest.transport(tableSource);
            fieldIndexGenerator(tableSource, 1);
            CubeColumnReaderService columnReaderService = tableEntity.getColumnDataGetter(BIColumnKey.covertColumnKey(tableSource.getFieldsArray(null)[1]));
            List<String> content = duplicateRemove(tableSource.stringData);
            Collections.sort(content, ComparatorFacotry.CHINESE_ASC);

            for (int i = 0; i < tableSource.stringData.size(); i++) {
                String one = (String) columnReaderService.getOriginalObjectValueByRow(i);
                int groupRow = columnReaderService.getPositionOfGroupByRow(i);
                GroupValueIndex groupValueIndex = columnReaderService.getBitmapIndex(groupRow);
                assertTrue(groupValueIndex.hasSameValue(GroupValueIndexTestTool.build(tableSource.stringData, one))
                        || groupValueIndex.equals(GroupValueIndexTestTool.build(tableSource.stringData, one)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private List<String> duplicateRemove(List<String> content) {
        Set<String> temp = new HashSet<String>();
        for (int i = 0; i < content.size(); i++) {
            temp.add(content.get(i));
        }
        List<String> result = new ArrayList();
        Iterator<String> it = temp.iterator();
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }

    private List<Long> duplicateLongRemove(List<Long> content) {
        Set<Long> temp = new HashSet<Long>();
        for (int i = 0; i < content.size(); i++) {
            temp.add(content.get(i));
        }
        List<Long> result = new ArrayList();
        Iterator<Long> it = temp.iterator();
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }

}
