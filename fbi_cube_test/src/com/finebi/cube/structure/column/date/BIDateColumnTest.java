package com.finebi.cube.structure.column.date;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.CubeRelationEntityGetterService;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.BICubeTableColumnManager;
import com.finebi.cube.structure.column.ICubeTableColumnManagerService;
import com.finebi.cube.tools.BIMemoryDataSourceFactory;
import com.finebi.cube.tools.BITableSourceRelationPathTestTool;
import com.finebi.cube.tools.BITableSourceTestTool;
import com.finebi.cube.tools.DBFieldTestTool;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.utils.algorithem.BIMD5Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This class created on 2016/4/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BIDateColumnTest extends BICubeTestBase {
    private ICubeTableColumnManagerService managerService;
    private List<ICubeFieldSource> fields = new ArrayList<ICubeFieldSource>();
    //2016-4-7 15:39:19
    private Long time = 1460014759867l;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fields.add(DBFieldTestTool.generateDATE());

        ITableKey tableKey = new BITableKey(BITableSourceTestTool.getDBTableSourceA());
        managerService = new BICubeTableColumnManager(tableKey, retrievalService, fields, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));

    }

    public void testYearColumn() {
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey year = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_YEAR);
            BICubeYearColumn yearColumn = (BICubeYearColumn) managerService.getColumn(year);
            date.forceReleaseWriter();
            assertEquals(Integer.valueOf(2016), yearColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testMonthColumn() {
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey month = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_MONTH);
            BICubeMonthColumn monthColumn = (BICubeMonthColumn) managerService.getColumn(month);
            date.forceReleaseWriter();
            assertEquals(Integer.valueOf(4), monthColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testSeasonColumn() {
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey season = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_SEASON);
            BICubeSeasonColumn seasonColumn = (BICubeSeasonColumn) managerService.getColumn(season);
            date.forceReleaseWriter();
            assertEquals(Integer.valueOf(2), seasonColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testWeekNumberColumn(){
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey weekNumber = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_WEEKNUMBER);
            BICubeWeekNumberColumn weekNumberColumn = (BICubeWeekNumberColumn) managerService.getColumn(weekNumber);
            date.forceReleaseWriter();
            assertEquals(Integer.valueOf(16), weekNumberColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testWeekColumn() {
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey week = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_WEEK);
            BICubeWeekColumn weekColumn = (BICubeWeekColumn) managerService.getColumn(week);
            date.forceReleaseWriter();
            assertEquals(Integer.valueOf(4), weekColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testDayColumn() {
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey day = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_DAY);
            BICubeDayColumn dayColumn = (BICubeDayColumn) managerService.getColumn(day);
            date.forceReleaseWriter();
            assertEquals(Integer.valueOf(7), dayColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testHourColumn(){
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey hour = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_HOUR);
            BICubeHourColumn hourColumn = (BICubeHourColumn) managerService.getColumn(hour);
            date.forceReleaseWriter();
            Calendar t = Calendar.getInstance();
            t.setTime(new Date(time));
            assertEquals(t.get(Calendar.HOUR_OF_DAY), hourColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testMinuteColumn(){
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey minute = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_MINUTE);
            BICubeMinuteColumn minuteColumn = (BICubeMinuteColumn) managerService.getColumn(minute);
            date.forceReleaseWriter();
            Calendar t = Calendar.getInstance();
            t.setTime(new Date(time));
            assertEquals(t.get(Calendar.MINUTE), minuteColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testSecondColumn(){
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey second = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_SECOND);
            BICubeSecondColumn secondColumn = (BICubeSecondColumn) managerService.getColumn(second);
            date.forceReleaseWriter();
            Calendar t = Calendar.getInstance();
            t.setTime(new Date(time));
            assertEquals(t.get(Calendar.SECOND), secondColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testYearSeasonColumn(){
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey yesrSeason = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_YEAR_SEASON);
            BICubeYearSeasonColumn yearSeasonColumn = (BICubeYearSeasonColumn) managerService.getColumn(yesrSeason);
            date.forceReleaseWriter();
            assertEquals("2016-2", yearSeasonColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testYearMonthColumn(){
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey yesrMonth = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_YEAR_MONTH);
            BICubeYearMonthColumn yearMonthColumn = (BICubeYearMonthColumn) managerService.getColumn(yesrMonth);
            date.forceReleaseWriter();
            assertEquals("2016-4", yearMonthColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testYearWeekNumberColumn(){
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey yesrWeekNumber = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_YEAR_WEEK_NUMBER);
            BICubeYearWeekNumberColumn yearMonthColumn = (BICubeYearWeekNumberColumn) managerService.getColumn(yesrWeekNumber);
            date.forceReleaseWriter();
            assertEquals("2016-16", yearMonthColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testYMDColumn() {
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey ymd = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_YEAR_MONTH_DAY);
            BICubeYearMonthDayColumn yearMonthDayColumn = (BICubeYearMonthDayColumn) managerService.getColumn(ymd);
            date.forceReleaseWriter();
            assertEquals(Long.valueOf(1459958400000l), yearMonthDayColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testYearMonthDayHourColumn() {
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey ymdh = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_YEAR_MONTH_DAY_HOUR);
            BICubeYearMonthDayHourColumn yearMonthDayHourColumn = (BICubeYearMonthDayHourColumn) managerService.getColumn(ymdh);
            date.forceReleaseWriter();
            Calendar t = Calendar.getInstance();
            t.setTime(new Date(time));
            t.set(Calendar.MINUTE,0);
            t.set(Calendar.SECOND,0);
            t.set(Calendar.MILLISECOND,0);
            assertEquals(t.getTimeInMillis(), yearMonthDayHourColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testYearMonthDayHourMinuteColumn() {
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey ymdhm = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_YEAR_MONTH_DAY_HOUR_MINUTE);
            BICubeYearMonthDayHourMinuteColumn yearMonthDayHourMinuteColumn = (BICubeYearMonthDayHourMinuteColumn) managerService.getColumn(ymdhm);
            date.forceReleaseWriter();
            Calendar t = Calendar.getInstance();
            t.setTime(new Date(time));
            t.set(Calendar.SECOND,0);
            t.set(Calendar.MILLISECOND,0);
            assertEquals(t.getTimeInMillis(), yearMonthDayHourMinuteColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testYearMonthDayHourMinuteSecondColumn() {
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);
            BIColumnKey ymdhms = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
            BICubeYearMonthDayHourMinuteSecondColumn yearMonthDayHourMinuteSecondColumn = (BICubeYearMonthDayHourMinuteSecondColumn) managerService.getColumn(ymdhms);
            date.forceReleaseWriter();
            Calendar t = Calendar.getInstance();
            t.setTime(new Date(time));
            t.set(Calendar.MILLISECOND,0);
            assertEquals(t.getTimeInMillis(), yearMonthDayHourMinuteSecondColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    /**
     * Detail:测试从子列获取Relation的时候，取出的Relation是来自主列
     * 而非来自子列
     * Author:Connery
     * Date:2016/6/20
     */
    public void testSubColumnRelation() {
        try {

            ITableKey tableKey = new BITableKey(BIMemoryDataSourceFactory.generateTableA());
            managerService = new BICubeTableColumnManager(tableKey, retrievalService, fields, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
            BIColumnKey ymd = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_YEAR_MONTH_DAY);
            BICubeYearMonthDayColumn yearMonthDayColumn = (BICubeYearMonthDayColumn) managerService.getColumn(ymd);
            CubeRelationEntityGetterService service = yearMonthDayColumn.getRelationIndexGetter(BICubePathUtils.convert(BITableSourceRelationPathTestTool.getABCDPath()));
            String relationLocation = service.getResourceLocation().getAbsolutePath();
            String[] values = new String[]{tableKey.getSourceID(), ymd.getKey(), BICubePathUtils.convert(BITableSourceRelationPathTestTool.getABCDPath()).getSourceID()};
            String name = BIMD5Utils.getMD5String(values);
            assertTrue(relationLocation.contains(name));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
