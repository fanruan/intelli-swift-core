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
import com.finebi.cube.utils.BICubePathUtils;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.utils.algorithem.BIMD5Utils;

import java.util.ArrayList;
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
            assertEquals(Integer.valueOf(3), monthColumn.getOriginalValueByRow(0));
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
            assertEquals(Integer.valueOf(5), weekColumn.getOriginalValueByRow(0));
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
            assertEquals(Integer.valueOf(7), dayColumn.getOriginalValueByRow(0));
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
            assertEquals(Integer.valueOf(2), seasonColumn.getOriginalValueByRow(0));
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
            assertEquals(Long.valueOf(1459958400000l), yearMonthDayColumn.getOriginalValueByRow(0));
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
            assertFalse(relationLocation.contains(name));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
