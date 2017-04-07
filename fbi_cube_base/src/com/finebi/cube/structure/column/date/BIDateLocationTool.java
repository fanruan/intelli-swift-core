package com.finebi.cube.structure.column.date;

import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.column.BIColumnKey;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/3/30.
 *
 * @author Connery
 * @since 4.0
 */
public class BIDateLocationTool {

    public static ICubeResourceLocation createYear(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_YEAR);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }

    }

    public static ICubeResourceLocation createMonth(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_MONTH);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }

    }

    public static ICubeResourceLocation createWeek(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_WEEK);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }

    }

    public static ICubeResourceLocation createSeason(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_SEASON);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }

    }

    public static ICubeResourceLocation createDay(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_DAY);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }

    }

    public static ICubeResourceLocation createYearMonthDay(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_YEAR_MONTH_DAY);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }

    }
    public static ICubeResourceLocation createYearMonth(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_YEAR_MONTH);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }
    }

    public static ICubeResourceLocation createYearSeacon(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_YEAR_SEASON);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }
    }

    public static ICubeResourceLocation createYearWeekNumber(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_YEAR_WEEK_NUMBER);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }
    }

    public static ICubeResourceLocation createYearMonthDayHour(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_YEAR_MONTH_DAY_HOUR);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }
    }

    public static ICubeResourceLocation createYearMonthDayHourMinute(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_YEAR_MONTH_DAY_HOUR_MINUTE);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }
    }

    public static ICubeResourceLocation createYearMonthDayHourMinuteSecond(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }
    }

    public static ICubeResourceLocation createHour(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_HOUR);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }
    }

    public static ICubeResourceLocation createMinute(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_MINUTE);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }
    }

    public static ICubeResourceLocation createSecond(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_SECOND);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }
    }

    public static ICubeResourceLocation createWeekNumber(ICubeResourceLocation location) {
        try {
            return location.buildChildLocation(BIColumnKey.DATA_SUB_TYPE_WEEKNUMBER);
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl();
        }
    }
}
