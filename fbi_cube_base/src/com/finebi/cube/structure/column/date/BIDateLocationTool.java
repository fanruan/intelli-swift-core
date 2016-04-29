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
}
