package com.finebi.cube.gen;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.gen.oper.BIFieldIndexGenerator;
import com.finebi.cube.structure.ICubeTableEntityGetterService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnReaderService;
import com.finebi.cube.structure.column.date.BIDateColumnTool;
import com.finebi.cube.structure.table.BICubeTableEntity;
import com.finebi.cube.tools.BIMemDataSourceTestToolCube;
import com.finebi.cube.tools.BIMemoryDataSource;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.tools.GroupValueIndexTestTool;
import com.finebi.cube.utils.BITableKeyUtils;
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
            ICubeColumnReaderService columnReaderService = tableEntity.getColumnDataGetter(BIColumnKey.covertColumnKey(tableSource.getFieldsArray(null)[1]));
            List<String> content = duplicateRemove(tableSource.stringData);
            Collections.sort(content, ComparatorFacotry.CHINESE_ASC);
            for (int i = 0; i < content.size(); i++) {
                assertEquals(content.get(i), columnReaderService.getGroupValue(i));
                String one = content.get(i);
                GroupValueIndex groupValueIndex = columnReaderService.getBitmapIndex(i);
                assertEquals(groupValueIndex, GroupValueIndexTestTool.build(tableSource.stringData, one));
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
            ICubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));
            ICubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIColumnKey.covertColumnKey(tableData.getFieldsArray(null)[0]));
            List<Long> content = duplicateLongRemove(tableData.contents.get(0));
            Collections.sort(content, ComparatorFacotry.LONG_ASC);
            for (int i = 0; i < content.size(); i++) {
                assertEquals(content.get(i), columnReaderService.getGroupValue(i));
                Long one = content.get(i);
                GroupValueIndex groupValueIndex = columnReaderService.getBitmapIndex(i);
                assertEquals(groupValueIndex, GroupValueIndexTestTool.build(tableData.contents.get(0), one));
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
            ICubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));
            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            ICubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateYear(field));
            assertEquals(1991, columnReaderService.getGroupValue(0));
            assertEquals(1992, columnReaderService.getGroupValue(1));
            assertEquals(1993, columnReaderService.getGroupValue(2));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateYearIndex() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            ICubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));
            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            ICubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateYear(field));
            assertEquals(RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{5}), columnReaderService.getBitmapIndex(0));
            assertEquals(RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{0, 1, 2, 3, 4}), columnReaderService.getBitmapIndex(1));
            assertEquals(RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{6}), columnReaderService.getBitmapIndex(2));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateMonth() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            ICubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));

            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            ICubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateMonth(field));
            assertEquals(6, columnReaderService.getGroupValue(0));
            assertEquals(7, columnReaderService.getGroupValue(1));
            assertEquals(8, columnReaderService.getGroupValue(2));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateMonthIndex() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            ICubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));

            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            ICubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateMonth(field));
            assertEquals(RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{5}), columnReaderService.getBitmapIndex(0));
            assertEquals(RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{0, 1, 2, 3, 4}), columnReaderService.getBitmapIndex(1));
            assertEquals(RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{6}), columnReaderService.getBitmapIndex(2));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateDay() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            ICubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));

            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            ICubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateDay(field));
            assertEquals(6, columnReaderService.getGroupValue(0));
            assertEquals(9, columnReaderService.getGroupValue(1));
            assertEquals(10, columnReaderService.getGroupValue(2));
            assertEquals(11, columnReaderService.getGroupValue(3));
            assertEquals(12, columnReaderService.getGroupValue(4));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateDayIndex() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            ICubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));

            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            ICubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateDay(field));
            assertEquals(RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{0}), columnReaderService.getBitmapIndex(0));
            assertEquals(RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{1}), columnReaderService.getBitmapIndex(1));
            assertEquals(RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{2}), columnReaderService.getBitmapIndex(2));
            assertEquals(RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{3, 5}), columnReaderService.getBitmapIndex(3));
            assertEquals(RoaringGroupValueIndex.createGroupValueIndex(new Integer[]{4, 6}), columnReaderService.getBitmapIndex(4));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateWeek() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            ICubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));

            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            ICubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateWeek(field));
            assertEquals(1, columnReaderService.getGroupValue(0));
            assertEquals(2, columnReaderService.getGroupValue(1));
            assertEquals(3, columnReaderService.getGroupValue(2));
            assertEquals(4, columnReaderService.getGroupValue(3));
            assertEquals(5, columnReaderService.getGroupValue(4));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFieldIndexDateSeason() {
        try {
            BIMemoryDataSource tableData = (BIMemoryDataSource) BIMemoryDataSourceFactory.generateTableDate();
            fieldIndexGenerator(tableData, 0);
            ICubeTableEntityGetterService dataTable = cube.getCubeTable(BITableKeyUtils.convert(tableData));

            ICubeFieldSource field = tableData.getFieldsArray(null)[0];
            ICubeColumnReaderService columnReaderService = dataTable.getColumnDataGetter(BIDateColumnTool.generateSeason(field));
            assertEquals(3, columnReaderService.getGroupValue(0));
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
//            assertEquals(3, columnReaderService.getGroupValue(0));
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
            ICubeColumnReaderService columnReaderService = tableEntity.getColumnDataGetter(BIColumnKey.covertColumnKey(tableSource.getFieldsArray(null)[1]));
            List<String> content = duplicateRemove(tableSource.stringData);
            Collections.sort(content, ComparatorFacotry.CHINESE_ASC);

            for (int i = 0; i < tableSource.stringData.size(); i++) {
                String one = (String) columnReaderService.getOriginalValueByRow(i);
                GroupValueIndex groupValueIndex = columnReaderService.getIndexByRow(i);
                assertEquals(groupValueIndex, GroupValueIndexTestTool.build(tableSource.stringData, one));
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
