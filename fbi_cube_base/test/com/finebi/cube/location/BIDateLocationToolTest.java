package com.finebi.cube.location;

import com.finebi.cube.tools.BICubeResourceLocationTestTool;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.date.BIDateLocationTool;
import junit.framework.TestCase;

/**
 * This class created on 2016/3/30.
 *
 * @author Connery
 * @since 4.0
 */
public class BIDateLocationToolTest extends TestCase {
    public void testCreateYear() {
        try {
            ICubeResourceLocation dataLocation = BICubeResourceLocationTestTool.getBasic("data");
            ICubeResourceLocation year = BIDateLocationTool.createYear(dataLocation);
            assertEquals(year.getAbsolutePath(), dataLocation.getAbsolutePath() + "/" + BIColumnKey.DATA_SUB_TYPE_YEAR);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testCreateMonth() {
        try {
            ICubeResourceLocation dataLocation = BICubeResourceLocationTestTool.getBasic("data");
            ICubeResourceLocation location = BIDateLocationTool.createMonth(dataLocation);
            assertEquals(location.getAbsolutePath(), dataLocation.getAbsolutePath() + "/" + BIColumnKey.DATA_SUB_TYPE_MONTH);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testCreateDay() {
        try {
            ICubeResourceLocation dataLocation = BICubeResourceLocationTestTool.getBasic("data");
            ICubeResourceLocation location = BIDateLocationTool.createDay(dataLocation);
            assertEquals(location.getAbsolutePath(), dataLocation.getAbsolutePath() + "/" + BIColumnKey.DATA_SUB_TYPE_DAY);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testCreateSeason() {
        try {
            ICubeResourceLocation dataLocation = BICubeResourceLocationTestTool.getBasic("data");
            ICubeResourceLocation location = BIDateLocationTool.createSeason(dataLocation);
            assertEquals(location.getAbsolutePath(), dataLocation.getAbsolutePath() + "/" + BIColumnKey.DATA_SUB_TYPE_SEASON);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testCreateWeek() {
        try {
            ICubeResourceLocation dataLocation = BICubeResourceLocationTestTool.getBasic("data");
            ICubeResourceLocation location = BIDateLocationTool.createWeek(dataLocation);
            assertEquals(location.getAbsolutePath(), dataLocation.getAbsolutePath() + "/" + BIColumnKey.DATA_SUB_TYPE_WEEK);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testCreateYearMonthDay() {
        try {
            ICubeResourceLocation dataLocation = BICubeResourceLocationTestTool.getBasic("data");
            ICubeResourceLocation location = BIDateLocationTool.createYearMonthDay(dataLocation);
            assertEquals(location.getAbsolutePath(), dataLocation.getAbsolutePath() + "/" + BIColumnKey.DATA_SUB_TYPE_YEAR_MONTH_DAY);
        } catch (Exception e) {
            assertTrue(false);
        }
    }


}
