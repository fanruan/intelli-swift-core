package com.finebi.cube.structure.column;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.column.date.*;
import com.finebi.cube.tools.BITableSourceTestTool;
import com.finebi.cube.tools.DBFieldTestTool;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.db.DBField;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2016/4/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTableColumnManagerTest extends BICubeTestBase {
    private ICubeTableColumnManagerService managerService;
    private List<DBField> fields = new ArrayList<DBField>();
    private Long time = 1460014759867l;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fields.add(DBFieldTestTool.generateDATE());

        ITableKey tableKey = new BITableKey(BITableSourceTestTool.getDBTableSourceA());
        managerService = new BICubeTableColumnManager(tableKey, retrievalService, fields, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
    }

    public void testDateColumn() {
        try {
            BICubeDateColumn date = (BICubeDateColumn) managerService.getColumn(BIColumnKey.covertColumnKey(DBFieldTestTool.generateDATE()));
            date.addOriginalDataValue(0, time);

            BIColumnKey year = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_YEAR);
            BICubeYearColumn yearColumn = (BICubeYearColumn) managerService.getColumn(year);
            assertEquals(Integer.valueOf(2016), yearColumn.getOriginalValueByRow(0));

            BIColumnKey month = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_MONTH);
            BICubeMonthColumn monthColumn = (BICubeMonthColumn) managerService.getColumn(month);
            assertEquals(Integer.valueOf(3), monthColumn.getOriginalValueByRow(0));

            BIColumnKey week = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_WEEK);
            BICubeWeekColumn weekColumn = (BICubeWeekColumn) managerService.getColumn(week);
            assertEquals(Integer.valueOf(5), weekColumn.getOriginalValueByRow(0));

            BIColumnKey day = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_DAY);
            BICubeDayColumn dayColumn = (BICubeDayColumn) managerService.getColumn(day);
            assertEquals(Integer.valueOf(7), dayColumn.getOriginalValueByRow(0));

            BIColumnKey season = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_SEASON);
            BICubeSeasonColumn seasonColumn = (BICubeSeasonColumn) managerService.getColumn(season);
            assertEquals(Integer.valueOf(2), seasonColumn.getOriginalValueByRow(0));

            BIColumnKey ymd = new BIColumnKey(DBFieldTestTool.generateDATE().getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.DATA_SUB_TYPE_YEAR_MONTH_DAY);
            BICubeYearMonthDayColumn yearMonthDayColumn = (BICubeYearMonthDayColumn) managerService.getColumn(ymd);
            assertEquals(Long.valueOf(1459958400000l), yearMonthDayColumn.getOriginalValueByRow(0));
        } catch (BICubeColumnAbsentException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }


}
