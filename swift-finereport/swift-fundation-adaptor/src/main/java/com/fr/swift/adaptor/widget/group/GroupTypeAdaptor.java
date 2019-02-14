package com.fr.swift.adaptor.widget.group;

import com.finebi.conf.constant.BICommonConstants.GROUP;
import com.finebi.conf.constant.BIConfConstants.CONF.ADD_COLUMN.TIME;
import com.finebi.conf.constant.BIConfConstants.CONF.ADD_COLUMN.TIME_GAP;
import com.finebi.conf.constant.BIDesignConstants;
import com.fr.swift.query.group.GroupType;

import static com.finebi.conf.constant.BIConfConstants.CONF.GROUP.DATE;

/**
 * @author anchore
 * @date 2018/2/28
 */
public class GroupTypeAdaptor {
    public static GroupType adaptGroupType(int groupType) {
        switch (groupType) {
            case GROUP.AUTO_GROUP:
                return GroupType.AUTO;
            case GROUP.CUSTOM_GROUP:
                return GroupType.CUSTOM;
            case GROUP.CUSTOM_NUMBER_GROUP:
                return GroupType.CUSTOM_NUMBER;
            case GROUP.NO_GROUP:
                return GroupType.NONE;
            case GROUP.Y:
                return GroupType.YEAR;
            case GROUP.S:
                return GroupType.QUARTER;
            case GROUP.M:
                return GroupType.MONTH;
            case GROUP.D:
                return GroupType.DAY;
            case GROUP.W:
                return GroupType.WEEK;
            case GROUP.HOUR:
                return GroupType.HOUR;
            case GROUP.MINUTE:
                return GroupType.MINUTE;
            case GROUP.SECOND:
                return GroupType.SECOND;
            case GROUP.WEEK_COUNT:
                return GroupType.WEEK_OF_YEAR;
            case GROUP.YMDHMS:
                return GroupType.Y_M_D_H_M_S;
            case GROUP.YMDHM:
                return GroupType.Y_M_D_H_M;
            case GROUP.YMDH:
                return GroupType.Y_M_D_H;
            case GROUP.YMD:
                return GroupType.Y_M_D;
            case GROUP.YS:
                return GroupType.Y_Q;
            case GROUP.YM:
                return GroupType.Y_M;
            case GROUP.YW:
                return GroupType.Y_W;
            default:
                return null;
        }
    }

    static GroupType adaptSingleValueGroupType(int type) {
        switch (type) {
            // 相同值为一组
            case 1:
                return GroupType.NONE;
            case DATE.YEAR:
                return GroupType.YEAR;
            case DATE.QUARTER:
                return GroupType.QUARTER;
            case DATE.MONTH:
                return GroupType.MONTH;
            case DATE.WEEK_COUNT:
                return GroupType.WEEK_OF_YEAR;
            case DATE.WEEKDAY:
                return GroupType.WEEK;
            case DATE.DAY:
                return GroupType.DAY;
            case DATE.HOUR:
                return GroupType.HOUR;
            case DATE.MINUTE:
                return GroupType.MINUTE;
            case DATE.SECOND:
                return GroupType.SECOND;
            case DATE.YMDHMS:
                return GroupType.Y_M_D_H_M_S;
            case DATE.YMDHM:
                return GroupType.Y_M_D_H_M;
            case DATE.YMDH:
                return GroupType.Y_M_D_H;
            case DATE.DATE:
                return GroupType.Y_M_D;
            case DATE.YQ:
                return GroupType.Y_Q;
            case DATE.YM:
                return GroupType.Y_M;
            case DATE.YW:
                return GroupType.Y_W;
            default:
                return null;
        }
    }

    /**
     * nice job! bi-foundation
     */
    public static GroupType adaptDateUnit(int unit) {
        switch (unit) {
            case TIME.UNITS.YEAR:
                return GroupType.YEAR;
            case TIME.UNITS.QUARTER:
                return GroupType.QUARTER;
            case TIME.UNITS.MONTH:
                return GroupType.MONTH;
            case TIME.UNITS.WEEKDAY:
                return GroupType.WEEK;
            case TIME.UNITS.DAY:
                return GroupType.DAY;
            case TIME.UNITS.WEEK_COUNT:
                return GroupType.WEEK_OF_YEAR;
            case TIME.UNITS.HOUR:
                return GroupType.HOUR;
            case TIME.UNITS.MINUTE:
                return GroupType.MINUTE;
            case TIME.UNITS.SECOND:
                return GroupType.SECOND;
            case TIME.UNITS.YQ:
                return GroupType.Y_Q;
            case TIME.UNITS.YM:
                return GroupType.Y_M;
            case TIME.UNITS.YW:
                return GroupType.Y_W;
            case TIME.UNITS.YMDH:
                return GroupType.Y_M_D_H;
            case TIME.UNITS.YMDHM:
                return GroupType.Y_M_D_H_M;
            case TIME.UNITS.YMDHMS:
                return GroupType.Y_M_D_H_M_S;
            default:
                return null;
        }
    }

    /**
     * nice job! bi-foundation
     */
    public static GroupType adaptDateGapUnit(int unit) {
        switch (unit) {
            case TIME_GAP.UNITS.YEAR:
                return GroupType.YEAR;
            case TIME_GAP.UNITS.QUARTER:
                return GroupType.QUARTER;
            case TIME_GAP.UNITS.MONTH:
                return GroupType.MONTH;
            case TIME_GAP.UNITS.WEEK:
                return GroupType.WEEK;
            case TIME_GAP.UNITS.DAY:
                return GroupType.DAY;
            case TIME_GAP.UNITS.HOUR:
                return GroupType.HOUR;
            case TIME_GAP.UNITS.MINUTE:
                return GroupType.MINUTE;
            case TIME_GAP.UNITS.SECOND:
                return GroupType.SECOND;
            default:
                return null;
        }
    }


    public static GroupType adaptDashboardGroup(int groupType) {
        switch (groupType) {
            case BIDesignConstants.DESIGN.GROUP.AUTO_GROUP:
                return GroupType.AUTO;
            case BIDesignConstants.DESIGN.GROUP.CUSTOM_GROUP:
                return GroupType.CUSTOM;
            case BIDesignConstants.DESIGN.GROUP.CUSTOM_NUMBER_GROUP:
                return GroupType.CUSTOM_NUMBER;
            case BIDesignConstants.DESIGN.GROUP.NO_GROUP:
                return GroupType.NONE;
            case BIDesignConstants.DESIGN.GROUP.Y:
                return GroupType.YEAR;
            case BIDesignConstants.DESIGN.GROUP.S:
                return GroupType.QUARTER;
            case BIDesignConstants.DESIGN.GROUP.M:
                return GroupType.MONTH;
            case BIDesignConstants.DESIGN.GROUP.D:
                return GroupType.DAY;
            case BIDesignConstants.DESIGN.GROUP.W:
                return GroupType.WEEK;
            case BIDesignConstants.DESIGN.GROUP.HOUR:
                return GroupType.HOUR;
            case BIDesignConstants.DESIGN.GROUP.MINUTE:
                return GroupType.MINUTE;
            case BIDesignConstants.DESIGN.GROUP.SECOND:
                return GroupType.SECOND;
            case BIDesignConstants.DESIGN.GROUP.WEEK_COUNT:
                return GroupType.WEEK_OF_YEAR;
            case BIDesignConstants.DESIGN.GROUP.YMDHMS:
                return GroupType.Y_M_D_H_M_S;
            case BIDesignConstants.DESIGN.GROUP.YMDHM:
                return GroupType.Y_M_D_H_M;
            case BIDesignConstants.DESIGN.GROUP.YMDH:
                return GroupType.Y_M_D_H;
            case BIDesignConstants.DESIGN.GROUP.YMD:
                return GroupType.Y_M_D;
            case BIDesignConstants.DESIGN.GROUP.YS:
                return GroupType.Y_Q;
            case BIDesignConstants.DESIGN.GROUP.YM:
                return GroupType.Y_M;
            case BIDesignConstants.DESIGN.GROUP.YW:
                return GroupType.Y_W;
            default:
                return GroupType.NONE;
        }
    }

}